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

public class PowerShellScriptEngine extends AbstractScriptEngine {

    private final PowerShellCachedCaller psCaller;

    public PowerShellScriptEngine() {
        File jsr223dll = this.initAndFindDll();
        this.psCaller = new PowerShellCachedCaller(jsr223dll);
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        system.Object ps = null;
        try {
            ps = psCaller.createNewPowerShellInstance();

            final ScriptException[] error = {null};

            addStreamsHandler(ps, error);

            addScriptBindings(context, ps);

            psCaller.addScript(ps, script);

            system.Object scriptResults = psCaller.invoke(ps);

            List<Object> resultAsList = convertResultToJava((IEnumerable) scriptResults);

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
            if (ps != null) {
                psCaller.dispose(ps);
            }
        }
    }

    private List<Object> convertResultToJava(IEnumerable scriptResults) {
        List<Object> resultAsList = new ArrayList<>();
        IEnumerator scriptResultsEnumerator = scriptResults.GetEnumerator();
        while (scriptResultsEnumerator.MoveNext()) {
            system.management.automation.PSObject scriptResult = (system.management.automation.PSObject) scriptResultsEnumerator.getCurrent();
            system.Object scriptResultObject = scriptResult.getBaseObject();
            Object javaScriptResult = CSharpJavaConverter.convertCSharpObjectToJavaObject(scriptResultObject);
            resultAsList.add(javaScriptResult);
        }
        return resultAsList;
    }

    private void addScriptBindings(ScriptContext context, system.Object ps) {
        for (Map.Entry<String, Object> binding : context.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
            String bindingKey = binding.getKey();
            Object bindingValue = binding.getValue();
            psCaller.setVariable(ps, bindingKey, CSharpJavaConverter.convertJavaObjectToCSharpObject(psCaller, bindingValue));
        }
    }

    private void addStreamsHandler(system.Object ps, final ScriptException[] error) {
        EventHandler outputHandler = new EventHandler() {
            public void Invoke(system.Object sender, EventArgs e) {
                Writer debugOutput = PowerShellScriptEngine.this.getContext().getWriter();
                String debugMessage = getMessageFromEvent(sender);
                try {
                    debugOutput.append(debugMessage);
                } catch (IOException ignored) {
                }
            }
        };

        EventHandler errorHandler = new EventHandler() {
            public void Invoke(system.Object sender, EventArgs e) {
                Writer errorOutput = PowerShellScriptEngine.this.getContext().getErrorWriter();
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

    public File initAndFindDll() {
        try {
            // create bridge, with default setup
            // it will lookup jni4net.n.dll next to jni4net.j.jar
//            Bridge.setDebug(true);
//            Bridge.setVerbose(true);
            Bridge.init();

            // Get directory that contains the jni4net.jar
            URL jni4netJarURL = Bridge.class.getProtectionDomain().getCodeSource().getLocation();
            File jni4netJarFile = new File(jni4netJarURL.toURI());
            return new File(jni4netJarFile.getParentFile(), "jsr223utils.dll");
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize the jn4net Bridge", ex);
        }
    }

//
//        // Add extra variables from scheduler
//        String pasJobId = System.getProperty("pas.job.id");
//        if (pasJobId != null) {
//            environment.put("JOB_ID", pasJobId);
//        }
//        String pasJobName = System.getProperty("pas.job.name");
//        if (pasJobName != null) {
//            environment.put("JOB_NAME", pasJobName);
//        }
//        String pasTaskId = System.getProperty("pas.task.id");
//        if (pasTaskId != null) {
//            environment.put("TASK_ID", pasTaskId);
//        }
//        String pasTaskName = System.getProperty("pas.task.name");
//        if (pasTaskName != null) {
//            environment.put("TASK_NAME", pasTaskName);
//        }
//        String pasTaskIteration = System.getProperty("pas.task.iteration");
//        if (pasTaskIteration != null) {
//            environment.put("TASK_ITERATION", pasTaskIteration);
//        }
//        String pasTaskReplication = System.getProperty("pas.task.replication");
//        if (pasTaskReplication != null) {
//            environment.put("TASK_REPLICATION", pasTaskReplication);
//        }
//    }

    // return can be an an array of object, test many cases with wrapping and co
//    [string]    Fixed-length string of Unicode characters
//    [char]      A Unicode 16-bit character
//    [byte]      An 8-bit unsigned character
//
//    [int]       32-bit signed integer
//    [long]      64-bit signed integer
//    [bool]      Boolean True/False value
//
//    [decimal]   A 128-bit decimal value
//    [single]    Single-precision 32-bit floating point number
//    [double]    Double-precision 64-bit floating point number
//    [DateTime]  Date and Time
//
//    [xml]       Xml object
//    [array]     An array of values
//    [hashtable] Hashtable object
}
