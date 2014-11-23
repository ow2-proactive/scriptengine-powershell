package jsr223.powershell;

import com.google.common.io.CharStreams;
import net.sf.jni4net.Bridge;
import system.Decimal;
import system.EventHandler;
import system.Type;
import system.ValueType;
import system.collections.IDictionary;
import system.collections.IDictionaryEnumerator;
import system.collections.IEnumerable;
import system.collections.IEnumerator;
import system.collections.IList;
import system.reflection.Assembly;
import system.reflection.MethodInfo;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PowerShellScriptEngine extends AbstractScriptEngine {

    private final PowerShellCachedCaller psCaller;

    private EventHandler errorHandler;
    private final EventHandler debugHandler;
    private final EventHandler verboseHandler;

    public PowerShellScriptEngine() {
        File jsr223dll = this.initAndFindDll();
        // Load Type Cache
        this.psCaller = new PowerShellCachedCaller(jsr223dll);
        // Set handlers
        this.errorHandler = new system.EventHandler() {
            public void Invoke(system.Object sender, system.EventArgs e) {
                Writer errorOutput = PowerShellScriptEngine.this.getContext().getErrorWriter();
                String errorMessage = null;
                try {
                    IList ll = Bridge.cast(sender, system.collections.IList.class);
                    system.Object value = ll.getItem(0);
                    errorMessage = value.toString();
                    ll.RemoveAt(0);
                } catch (Exception ex) {
                    errorMessage = ex.getMessage();
                } finally {
                    try {
                        errorOutput.append(errorMessage);
                    } catch (IOException ex) {
                    }
                }
            }
        };

        this.debugHandler = new system.EventHandler() {

            public void Invoke(system.Object sender, system.EventArgs e) {
                Writer debugOutput = PowerShellScriptEngine.this.getContext().getWriter();
                String debugMessage = null;
                try {
                    IList ll = Bridge.cast(sender, system.collections.IList.class);
                    system.Object value = ll.getItem(0);
                    debugMessage = value.toString();
                    ll.RemoveAt(0);
                } catch (Exception ex) {
                    debugMessage = ex.getMessage();
                } finally {
                    try {
                        debugOutput.append(debugMessage);
                    } catch (IOException ex) {
                    }
                }
            }
        };

        this.verboseHandler = new system.EventHandler() {

            public void Invoke(system.Object sender, system.EventArgs e) {
                Writer debugOutput = PowerShellScriptEngine.this.getContext().getWriter();
                String debugMessage = null;
                try {
                    IList ll = Bridge.cast(sender, system.collections.IList.class);
                    system.Object value = ll.getItem(0);
                    debugMessage = value.toString();
                    ll.RemoveAt(0);
                } catch (Exception ex) {
                    debugMessage = ex.getMessage();
                } finally {
                    try {
                        debugOutput.append(debugMessage);
                    } catch (IOException ex) {
                    }
                }
            }
        };

    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        system.Object ps = null;
        try {
            ps = psCaller.createNewPowerShellInstance();

            final ScriptException[] error = {null};
            this.errorHandler = new system.EventHandler() {

                public void Invoke(system.Object sender, system.EventArgs e) {
                    Writer errorOutput = PowerShellScriptEngine.this.getContext().getErrorWriter();
                    String errorMessage = null;
                    try {
                        IList ll = Bridge.cast(sender, system.collections.IList.class);
                        system.Object value = ll.getItem(0);
                        errorMessage = value.toString();
                        error[0] = new ScriptException(errorMessage);
                        ll.RemoveAt(0);
                    } catch (Exception ex) {
                        errorMessage = ex.getMessage();
                    } finally {
                        try {
                            errorOutput.append(errorMessage);
                        } catch (IOException ex) {
                        }
                    }
                }
            };

            // Add handlers
            psCaller.addHandlers(ps, this.errorHandler, this.debugHandler, this.verboseHandler);

            // Add variables from context
            for (Map.Entry<String, java.lang.Object> binding : context.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
                String bindingKey = binding.getKey();
                Object bindingValue = binding.getValue();
                psCaller.setVariable(ps, bindingKey, convertJavaObjectToCSharpObject(bindingValue));
            }
            // Add script to run
            psCaller.addScript(ps, script);

            system.Object scriptResults = psCaller.invoke(ps);

            List<Object> resultAsList = new ArrayList<>();
            IEnumerator scriptResultsEnumerator = ((IEnumerable) scriptResults).GetEnumerator();
            while (scriptResultsEnumerator.MoveNext()) {
                system.management.automation.PSObject scriptResult = (system.management.automation.PSObject) scriptResultsEnumerator.getCurrent();
                system.Object scriptResultObject = scriptResult.getBaseObject();
                Object javaScriptResult = convertCSharpObjectToJavaObject(scriptResultObject);
                resultAsList.add(javaScriptResult);
            }

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

    @SuppressWarnings("unchecked")
    private system.Object convertJavaObjectToCSharpObject(java.lang.Object bindingValue) {
        if (bindingValue instanceof String) {
            return new system.String((String) bindingValue);
        } else if (bindingValue instanceof Integer) {
            return psCaller.toInt(bindingValue.toString());
        } else if (bindingValue instanceof Long) {
            return psCaller.toLong(bindingValue.toString());
        } else if (bindingValue instanceof Double) {
            return psCaller.toDouble(bindingValue.toString());
        } else if (bindingValue instanceof Byte) {
            return psCaller.toByte(bindingValue.toString());
        } else if (bindingValue instanceof Character) {
            return psCaller.toChar(bindingValue.toString());
        } else if (bindingValue instanceof Boolean) {
            return psCaller.toBool(bindingValue.toString());
        } else if (bindingValue instanceof List) {
            system.collections.ArrayList cSharpList = new system.collections.ArrayList();
            for (Object entry : (List) bindingValue) {
                cSharpList.Add(convertJavaObjectToCSharpObject(entry));
            }
            return cSharpList;
        } else if (bindingValue instanceof Map) {
            system.collections.Hashtable cSharpMap = new system.collections.Hashtable();
            for (Map.Entry entry : (Set<Map.Entry>) ((Map) bindingValue).entrySet()) {
                cSharpMap.Add(new system.String(entry.getKey().toString()), convertJavaObjectToCSharpObject(entry.getValue()));
            }
            return cSharpMap;
        }
        return null;
    }

    private java.lang.Object convertCSharpObjectToJavaObject(system.Object scriptResultObject) {
        if (scriptResultObject instanceof Decimal) {
            Decimal decimal = (Decimal) scriptResultObject;
            return Decimal.ToInt32(decimal);
        } else if (scriptResultObject instanceof system.String) {
            system.String asString = (system.String) scriptResultObject;
            return asString.ToString();
        } else if (scriptResultObject instanceof ValueType) {
            ValueType scriptResultValue = (ValueType) scriptResultObject;
            if (scriptResultValue.GetType().getName().equals("Int32")) {
                return Integer.parseInt(scriptResultValue.ToString());
            } else if (scriptResultValue.GetType().getName().equals("Int64")) {
                return Long.parseLong(scriptResultObject.toString());
            } else if (scriptResultValue.GetType().getName().equals("Double")) {
                return Double.parseDouble(scriptResultObject.toString());
            } else if (scriptResultValue.GetType().getName().equals("Byte")) {
                return Byte.parseByte(scriptResultValue.toString());
            } else if (scriptResultValue.GetType().getName().equals("Char")) {
                return scriptResultValue.toString().charAt(0);
            } else if (scriptResultValue.GetType().getName().equals("Boolean")) {
                return Boolean.parseBoolean(scriptResultValue.toString());
            } else {
                return scriptResultValue.toString();
            }
        } else if (scriptResultObject instanceof system.collections.IList) {
            system.collections.IList asList = ((IList) scriptResultObject);
            List<Object> javaList = new ArrayList<>();
            for (int i = 0; i < asList.getCount(); i++) {
                javaList.add(convertCSharpObjectToJavaObject(asList.getItem(i)));
            }
            return javaList;
        } else if (scriptResultObject instanceof IDictionary) {
            IDictionary asMap = ((IDictionary) scriptResultObject);
            IDictionaryEnumerator enumerator = asMap.GetEnumerator();
            Map<String, Object> javaMap = new HashMap<>();
            while (enumerator.MoveNext()) {
                String key = enumerator.getKey().toString();
                Object value = convertCSharpObjectToJavaObject(enumerator.getValue());
                javaMap.put(key, value);
            }
            return javaMap;
        } else {
            return scriptResultObject.toString();
        }
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        String s;
        try {
            s = CharStreams.toString(reader);
        } catch (IOException ex) {
            throw new ScriptException(ex);
        }
        return eval(s, context);
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
            Bridge.setDebug(true);
            Bridge.setVerbose(true);
            Bridge.init();

            // Get directory that contains the jni4net.jar
            URL jni4netJarURL = Bridge.class.getProtectionDomain().getCodeSource().getLocation();
            File jni4netJarFile = new File(jni4netJarURL.toURI());
            System.out.println("---> utils dll location " + jni4netJarFile);
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

    // Load all required standard and utils types
// Then performs calls on .Net objects by reflection
    private class PowerShellCachedCaller {

        final Type[] EMPTY_TYPE_ARRAY = new Type[]{};
        final Type HandlerUtils, Int32, String, PowerShell, PSDataStreams, DataAddedEventArgs;

        public PowerShellCachedCaller(File jsr223dll) {
            // Load jsr223utils.dll and register as main assembly
            Assembly wfAssembly = Assembly.LoadFrom(jsr223dll.getAbsolutePath());
            Bridge.RegisterAssembly(wfAssembly);
            Int32 = Type.GetType("System.Int32");
            String = Type.GetType("System.String");
            HandlerUtils = wfAssembly.GetType("utils.HandlerUtils");

            // Load automation assembly for PowerShell
            Assembly smaAssembly = Assembly.LoadWithPartialName("System.Management.Automation");
            PowerShell = smaAssembly.GetType("System.Management.Automation.PowerShell");
            PSDataStreams = smaAssembly.GetType("System.Management.Automation.PSDataStreams");
            DataAddedEventArgs = smaAssembly.GetType("System.Management.Automation.DataAddedEventArgs");
        }

        private system.Object createNewPowerShellInstance() {
            return PowerShell.GetMethod("Create", EMPTY_TYPE_ARRAY).Invoke(null, null);
        }

        private void addHandlers(system.Object pslInstance, EventHandler err, EventHandler debug, EventHandler verbose) {
            HandlerUtils.GetMethod("AddErrorHandler").Invoke(null, new system.Object[]{pslInstance, err});
            HandlerUtils.GetMethod("AddDebugHandler").Invoke(null, new system.Object[]{pslInstance, debug});
            HandlerUtils.GetMethod("AddVerboseHandler").Invoke(null, new system.Object[]{pslInstance, verbose});
        }

        private system.Object toBool(String bool) {
            return toSomething("toBool", bool);
        }

        private system.Object toInt(String bool) {
            return toSomething("toInt", bool);
        }

        private system.Object toLong(String bool) {
            return toSomething("toLong", bool);
        }

        private system.Object toDouble(String bool) {
            return toSomething("toDouble", bool);
        }

        private system.Object toByte(String bool) {
            return toSomething("toByte", bool);
        }

        private system.Object toChar(String bool) {
            return toSomething("toChar", bool);
        }

        private system.Object toSomething(String methodName, String value) {
            return HandlerUtils.GetMethod(methodName).Invoke(null, new system.Object[]{new system.String(value)});
        }

        private void addScript(system.Object psInstance, String script) {
            PowerShell.GetMethod("AddScript", new Type[]{String}).Invoke(psInstance, new system.Object[]{new system.String(script)});
        }

        private void setVariable(system.Object psInstance, java.lang.String variableName, system.Object variableValue) {
            PowerShell.GetMethod("AddCommand", new Type[]{String}).Invoke(psInstance, new system.Object[]{new system.String("set-variable")});
            PowerShell.GetMethod("AddArgument", new Type[]{String}).Invoke(psInstance, new system.Object[]{new system.String(variableName)});
            PowerShell.GetMethod("AddArgument").Invoke(psInstance, new system.Object[]{variableValue});
        }

        private system.Object invoke(system.Object psInstance) {


            return findInvokeMethod().Invoke(psInstance, null);
        }

        private MethodInfo findInvokeMethod() {
            for (MethodInfo methodInfo : PowerShell.GetMethods()) {
                if (methodInfo.getName().equals("Invoke")) {
                    if (methodInfo.getReturnType().GetGenericArguments()[0].getName().equals("PSObject")) {
                        return methodInfo;
                    }
                }
            }
            return null;
        }

        private void dispose(system.Object pslInstance) {
            PowerShell.GetMethod("Dispose", EMPTY_TYPE_ARRAY).Invoke(pslInstance, null);
        }


    }

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
