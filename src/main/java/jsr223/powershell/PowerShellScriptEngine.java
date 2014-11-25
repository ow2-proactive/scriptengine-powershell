package jsr223.powershell;

import net.sf.jni4net.Bridge;
import system.EventArgs;
import system.EventHandler;
import system.collections.IEnumerable;
import system.collections.IEnumerator;
import system.collections.IList;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jsr223.powershell.CSharpJavaConverter.convertCSharpObjectToJavaObject;

public class PowerShellScriptEngine extends AbstractScriptEngine {

    private final PowerShellCachedCaller psCaller;

    public PowerShellScriptEngine() {
        File jsr223dll = this.initAndFindDll();
        this.psCaller = new PowerShellCachedCaller(jsr223dll);
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        system.Object powerShellInstance = null;
        try {
            powerShellInstance = psCaller.createNewPowerShellInstance();

            final ScriptException[] error = {null};

            addStreamsHandler(powerShellInstance, error, context);

            addProActivePropertiesAsScriptBindings(context);
            addScriptBindings(context, powerShellInstance);

            psCaller.addScript(powerShellInstance, script);

            system.Object scriptResults = psCaller.invoke(powerShellInstance);

            List<Object> resultAsList = convertResultToJava((IEnumerable) scriptResults);

            addScriptEngineVariablesToEngine(psCaller, powerShellInstance, context);

            if (error[0] != null) {
                throw error[0];
            }

            if (resultAsList.isEmpty()) {
                return null;
            } else if (resultAsList.size() == 1) {
                return resultAsList.get(0);
            } else {
                return resultAsList;
            }
        } finally {
            if (powerShellInstance != null) {
                psCaller.dispose(powerShellInstance);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addScriptEngineVariablesToEngine(PowerShellCachedCaller psCaller, system.Object ps, ScriptContext context) {
        readBindingFromPowerShellContext(psCaller, ps, context, "result");
        readBindingFromPowerShellContext(psCaller, ps, context, "selected");
        readBindingFromPowerShellContext(psCaller, ps, context, "command");
        readBindingFromPowerShellContext(psCaller, ps, context, "branch");
        Object variablesFromScheduler = context.getAttribute("variables");
        if (variablesFromScheduler instanceof Map) {
            Map variablesMapFromScheduler = (Map) variablesFromScheduler;
            Object variablesFromScript = convertCSharpObjectToJavaObject(psCaller.getVariables(ps, "variables"));
            if (variablesFromScript instanceof Map) {
                variablesMapFromScheduler.clear();
                variablesMapFromScheduler.putAll((Map) variablesFromScript);
            }
        }
    }

    private void readBindingFromPowerShellContext(PowerShellCachedCaller psCaller, system.Object ps, ScriptContext context, String bindingName) {
        Object binding = convertCSharpObjectToJavaObject(psCaller.getVariables(ps, bindingName));
        if (binding != null) {
            context.setAttribute(bindingName, binding, ScriptContext.ENGINE_SCOPE);
        }
    }

    private void addProActivePropertiesAsScriptBindings(ScriptContext context) {
        addSystemPropertyAsScriptBinding(context, "pasJobId", "pas.job.id");
        addSystemPropertyAsScriptBinding(context, "pasJobName", "pas.job.name");
        addSystemPropertyAsScriptBinding(context, "pasTaskId", "pas.task.id");
        addSystemPropertyAsScriptBinding(context, "pasTaskName", "pas.task.name");
        addSystemPropertyAsScriptBinding(context, "pasTaskIteration", "pas.task.iteration");
        addSystemPropertyAsScriptBinding(context, "pasTaskReplication", "pas.task.replication");
    }

    private void addSystemPropertyAsScriptBinding(ScriptContext context, String bindingName, String systemPropertyName) {
        String systemProperty = System.getProperty(systemPropertyName);
        if (systemProperty != null) {
            context.setAttribute(bindingName, systemProperty, ScriptContext.ENGINE_SCOPE);
        }
    }

    private List<Object> convertResultToJava(IEnumerable scriptResults) {
        List<Object> resultAsList = new ArrayList<>();
        IEnumerator scriptResultsEnumerator = scriptResults.GetEnumerator();
        while (scriptResultsEnumerator.MoveNext()) {
            system.management.automation.PSObject scriptResult = (system.management.automation.PSObject) scriptResultsEnumerator.getCurrent();
            if (scriptResult != null) {
                system.Object scriptResultObject = scriptResult.getBaseObject();
                Object javaScriptResult = convertCSharpObjectToJavaObject(scriptResultObject);
                resultAsList.add(javaScriptResult);
            }
        }
        return resultAsList;
    }

    private void addScriptBindings(ScriptContext context, system.Object ps) throws ScriptException {
        for (Map.Entry<String, Object> binding : context.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
            String bindingKey = binding.getKey();
            Object bindingValue = binding.getValue();
            psCaller.setVariable(ps, bindingKey, CSharpJavaConverter.convertJavaObjectToCSharpObject(psCaller, bindingValue));
        }
    }

    private void addStreamsHandler(system.Object ps, final ScriptException[] error, final ScriptContext context) {
        EventHandler outputHandler = new EventHandler() {
            public void Invoke(system.Object sender, EventArgs e) {
                Writer debugOutput = context.getWriter();
                String debugMessage = getMessageFromEvent(sender);
                try {
                    debugOutput.append(debugMessage);
                } catch (IOException ignored) {
                }
            }
        };

        EventHandler errorHandler = new EventHandler() {
            public void Invoke(system.Object sender, EventArgs e) {
                Writer errorOutput = context.getErrorWriter();
                String errorMessage = getMessageFromEvent(sender);
                try {
                    error[0] = new ScriptException(errorMessage);
                    errorOutput.append(errorMessage);
                } catch (IOException ignored) {
                }
            }
        };

        psCaller.addHandlers(ps, errorHandler, outputHandler, outputHandler);
    }

    private String getMessageFromEvent(system.Object sender) {
        try {
            IList ll = Bridge.cast(sender, IList.class);
            system.Object value = ll.getItem(0);
            String errorMessage = value.toString();
            ll.RemoveAt(0);
            return errorMessage;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return eval(IOUtils.toString(reader), context);
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return new PowerShellScriptEngineFactory();
    }

    private File initAndFindDll() {
        try {
            // create bridge, with default setup
            // it will lookup jni4net.n.dll next to jni4net.j.jar
            boolean isDebug = System.getProperty("powershell.debug") != null && Boolean.parseBoolean(System.getProperty("powershell.debug"));
            Bridge.setDebug(isDebug);
            Bridge.setVerbose(isDebug);
            Bridge.init();

            // Get directory that contains the jni4net.jar
            URL jni4netJarURL = Bridge.class.getProtectionDomain().getCodeSource().getLocation();
            File jni4netJarFile = new File(jni4netJarURL.toURI());
            return new File(jni4netJarFile.getParentFile(), "jsr223utils.dll");
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize the jn4net Bridge", ex);
        }
    }

}
