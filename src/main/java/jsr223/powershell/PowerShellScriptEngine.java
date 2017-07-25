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
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jsr223.powershell.CSharpJavaConverter.convertCSharpObjectToJavaObject;

import org.apache.commons.io.IOUtils;
import org.ow2.proactive.scheduler.common.SchedulerConstants;
import org.ow2.proactive.scheduler.common.task.flow.FlowScript;
import org.ow2.proactive.scheduler.task.utils.VariablesMap;
import org.ow2.proactive.scripting.SelectionScript;
import org.ow2.proactive.scripting.TaskScript;


public class PowerShellScriptEngine extends AbstractScriptEngine {

    private final PowerShellCachedCaller psCaller;

    public PowerShellScriptEngine() {
        this.psCaller = PowerShellCachedCaller.getInstance();
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {

        PowerShellCachedCaller.PowerShellEnvironment environment = null;
        try {
            environment = psCaller.createNewPowerShellInstance(context);

            final ScriptException[] error = { null };

            addStreamsHandler(environment.getPowershell(), error, context);

            addScriptBindings(context, environment.getPowershell());

            psCaller.addScript(environment.getPowershell(), script, getScriptArguments(context));

            system.Object scriptResults = psCaller.invoke(environment.getPowershell());

            List<Object> resultAsList = convertResultToJava(psCaller, (IEnumerable) scriptResults);

            displayResults(resultAsList, environment);

            readOutputVariablesFromEngine(psCaller, environment.getPowershell(), context);

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
            if (environment.getRunspace() != null) {
                psCaller.closeRunspace(environment.getRunspace());
            }
            if (environment.getPowershell() != null) {
                psCaller.dispose(environment.getPowershell());
            }
            environment.getOutStream().flush();
            environment.getErrStream().flush();
        }
    }

    private void displayResults(List<Object> resultAsList, PowerShellCachedCaller.PowerShellEnvironment environment) {
        if (resultAsList != null) {
            for (Object object : resultAsList) {
                environment.getOutStream().println(object);
            }
        }
    }

    private system.Object getScriptArguments(ScriptContext context) throws ScriptException {
        Object argsFromBinding = context.getBindings(ScriptContext.ENGINE_SCOPE).get(TaskScript.ARGUMENTS_NAME);
        return CSharpJavaConverter.convertJavaObjectToCSharpObject(psCaller, argsFromBinding);
    }

    @SuppressWarnings("unchecked")
    private void readOutputVariablesFromEngine(PowerShellCachedCaller psCaller, system.Object ps,
            ScriptContext context) {
        readBindingFromPowerShellContext(psCaller, ps, context, TaskScript.RESULT_VARIABLE);
        readBindingFromPowerShellContext(psCaller, ps, context, SelectionScript.RESULT_VARIABLE);
        readBindingFromPowerShellContext(psCaller, ps, context, FlowScript.branchSelectionVariable);
        readBindingFromPowerShellContext(psCaller, ps, context, FlowScript.replicateRunsVariable);
        readBindingFromPowerShellContext(psCaller, ps, context, FlowScript.loopVariable);
        updateVariableBindings(psCaller, ps, context, SchedulerConstants.VARIABLES_BINDING_NAME);
        updateVariableBindings(psCaller, ps, context, SchedulerConstants.RESULT_METADATA_VARIABLE);
    }

    private void updateVariableBindings(PowerShellCachedCaller psCaller, system.Object ps, ScriptContext context,
            String bindingName) {
        Object variablesFromScheduler = context.getAttribute(bindingName);
        if (variablesFromScheduler != null && variablesFromScheduler instanceof Map) {
            Map variablesMapFromScheduler = (Map) variablesFromScheduler;
            Object convertedVariablesFromScript = convertCSharpObjectToJavaObject(psCaller,
                                                                                  psCaller.getVariables(ps,
                                                                                                        bindingName));
            if (convertedVariablesFromScript instanceof VariablesMap) {
                Map<String, Serializable> scriptVariableMap = ((VariablesMap) convertedVariablesFromScript).getScriptMap();
                Map<String, Serializable> filteredScriptVariableMap = filterUnconvertibleVariables(scriptVariableMap);
                ((Map) variablesFromScheduler).putAll(filteredScriptVariableMap);
            } else if (convertedVariablesFromScript instanceof Map) {
                Map<String, Serializable> scriptVariableMap = (Map) convertedVariablesFromScript;
                Map<String, Serializable> filteredScriptVariableMap = filterUnconvertibleVariables(scriptVariableMap);
                ((Map) variablesFromScheduler).putAll(filteredScriptVariableMap);
            }
        }
    }

    private Map<String, Serializable> filterUnconvertibleVariables(Map<String, Serializable> map) {
        Map<String, Serializable> answer = new HashMap<>();
        for (Map.Entry<String, Serializable> entry : map.entrySet()) {
            if (entry.getValue() instanceof String &&
                ((String) entry.getValue()).startsWith(CSharpJavaConverter.NOT_SUPPORTED_JAVA_OBJECT)) {
                // Conversion from java to C# was not possible, filter this entry
            } else {
                answer.put(entry.getKey(), entry.getValue());
            }
        }
        return answer;
    }

    private void readBindingFromPowerShellContext(PowerShellCachedCaller psCaller, system.Object ps,
            ScriptContext context, String bindingName) {
        Object binding = convertCSharpObjectToJavaObject(psCaller, psCaller.getVariables(ps, bindingName));
        if (binding != null) {
            context.setAttribute(bindingName, binding, ScriptContext.ENGINE_SCOPE);
        }
    }

    private List<Object> convertResultToJava(PowerShellCachedCaller psCaller, IEnumerable scriptResults) {
        List<Object> resultAsList = new ArrayList<>();
        IEnumerator scriptResultsEnumerator = scriptResults.GetEnumerator();
        while (scriptResultsEnumerator.MoveNext()) {
            system.management.automation.PSObject scriptResult = (system.management.automation.PSObject) scriptResultsEnumerator.getCurrent();
            if (scriptResult != null) {
                system.Object scriptResultObject;
                try {
                    scriptResultObject = scriptResult.getBaseObject();
                } catch (Exception e) {
                    scriptResultObject = scriptResult;
                }
                Object javaScriptResult = convertCSharpObjectToJavaObject(psCaller, scriptResultObject);
                resultAsList.add(javaScriptResult);
            }
        }
        return resultAsList;
    }

    private void addScriptBindings(ScriptContext context, system.Object ps) throws ScriptException {
        for (Map.Entry<String, Object> binding : context.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
            String bindingKey = binding.getKey();
            Object bindingValue = binding.getValue();
            psCaller.setVariable(ps,
                                 bindingKey,
                                 CSharpJavaConverter.convertJavaObjectToCSharpObject(psCaller, bindingValue));
        }
    }

    private void addStreamsHandler(system.Object ps, final ScriptException[] error, final ScriptContext context) {

        EventHandler errorHandler = new EventHandler() {
            public void Invoke(system.Object sender, EventArgs e) {
                Writer errorOutput = context.getErrorWriter();
                String errorMessage = getMessageFromEvent(sender);
                try {
                    error[0] = new ScriptException(errorMessage);
                    errorOutput.append(errorMessage);
                    errorOutput.flush();

                } catch (IOException ignored) {
                }
            }
        };

        psCaller.addHandlers(ps, errorHandler);
    }

    private String getMessageFromEvent(system.Object sender) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            IList ll = Bridge.cast(sender, IList.class);
            int count = ll.getCount();
            for (int i = 0; i < count; i++) {
                system.Object value = ll.getItem(i);
                stringBuilder.append(value.toString());
                stringBuilder.append(System.lineSeparator());
            }

            return stringBuilder.toString();
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        try {
            return eval(IOUtils.toString(reader), context);
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return new PowerShellScriptEngineFactory();
    }

}
