package jsr223.powershell;

import com.google.common.io.CharStreams;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import net.sf.jni4net.Bridge;
import org.objectweb.proactive.extensions.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extensions.dataspaces.vfs.adapter.VFSFileObjectAdapter;
import system.Console;
import system.EventHandler;
import system.IAsyncResult;
import system.Type;
import system.collections.IEnumerable;
import system.collections.IEnumerator;
import system.collections.IList;
import system.io.StringWriter;
import system.io.TextWriter;
import system.reflection.Assembly;
import system.reflection.MethodInfo;

public class PowerShellScriptEngineNoFork extends AbstractScriptEngine {

    public static String PSHELL_PATH = "C:\\Windows\\System32\\WindowsPowershell\\v1.0\\PowerShell.exe";
    public static String WINDOW_STYLE = "Hidden";
    public static String EXEC_POLICY = "RemoteSigned";//"Unrestricted";    

    public static int OK_EXIT_CODE = 0;

    private final PowerShellCachedCaller psCaller;

    private final EventHandler errorHandler;
    private final EventHandler debugHandler;
    private final EventHandler verboseHandler;
    private final EventHandler outputHandler;

    public PowerShellScriptEngineNoFork() {
        File jsr223dll = this.initAndFindDll();
        // Load Type Cache
        this.psCaller = new PowerShellCachedCaller(jsr223dll);
        // Set handlers
        this.errorHandler = new system.EventHandler() {
            public void Invoke(system.Object sender, system.EventArgs e) {
                Writer errorOutput = PowerShellScriptEngineNoFork.this.getContext().getErrorWriter();
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
                Writer debugOutput = PowerShellScriptEngineNoFork.this.getContext().getWriter();
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

                System.out.println("-----------------> verbose:  " + sender + " " + e);
                try {
                    IList ll = Bridge.cast(sender, system.collections.IList.class);
                    for (int i = 0; i < ll.getCount(); i++) {
                        System.out.println("on a  ---> " + ll.getItem(i));
                        ll.RemoveAt(i);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        this.outputHandler = new system.EventHandler() {

            public void Invoke(system.Object sender, system.EventArgs e) {

                System.out.println("-----------------> output:  " + sender + " " + e);
                try {
                    IList ll = Bridge.cast(sender, system.collections.IList.class);
                    for (int i = 0; i < ll.getCount(); i++) {
                        System.out.println("on a  ---> " + ll.getItem(i));
                        ll.RemoveAt(i);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        system.Object ps = null;
        try {
            // Create new instance of PowerShell
            long s = System.currentTimeMillis();
            StringWriter textWriter = new StringWriter();
            Console.SetOut(textWriter);
            ps = psCaller.createNewPowerShellInstance();
            System.out.println(" --> " + ps);
            System.out.println("---> Created new instance of PowerShell in " + (System.currentTimeMillis() - s));

            // Add handlers
            psCaller.addHandlers(ps, this.errorHandler, this.debugHandler, this.verboseHandler);

            // Add variables from context
            for (Map.Entry<String, Object> binding : context.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
                String bindingKey = binding.getKey();
                Object bindingValue = binding.getValue();
                psCaller.setVariable(ps, bindingKey, bindingValue);
            }
            //PowerShellInstance.AddCommand("set-variable").AddArgument("toto").AddArgument(i);
            // Add script to run
            psCaller.addScript(ps, script);

            // Run script asynchronously
            IAsyncResult asyncResult = psCaller.runAsynchronously(ps);

            // Wait until
            Object result = null;
            while (!asyncResult.isCompleted()) {
                System.out.println("---------> waiting for script to finish ... ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }

            IEnumerator res = psCaller.res(ps, asyncResult).GetEnumerator();
            while (res.MoveNext()) {
                Object nextElement =  res.getCurrent();
                System.out.println(nextElement);
            }

            System.out.println("Outout console " + textWriter.toString());

            System.out.println(res);

            System.out.println("--> disposing the PowerShellInstance");
            return result;
        } finally {
            if (ps != null) {
                psCaller.dispose(ps);
            }
        }
        //return null;
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

    private void addBindingsAsEnvironmentVariables(ScriptContext scriptContext, ProcessBuilder processBuilder) throws ScriptException {
        Map<String, String> environment = processBuilder.environment();
        for (Map.Entry<String, Object> binding : scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).entrySet()) {
            String bindingKey = binding.getKey();
            Object bindingValue = binding.getValue();

            if (bindingValue instanceof Object[]) {
                addArrayBindingAsEnvironmentVariable(bindingKey, (Object[]) bindingValue, environment);
            } else if (bindingValue instanceof Collection) {
                addCollectionBindingAsEnvironmentVariable(bindingKey, (Collection) bindingValue, environment);
            } else if (bindingValue instanceof Map) {
                addMapBindingAsEnvironmentVariable(bindingKey, (Map<?, ?>) bindingValue, environment);
            } else if (bindingValue instanceof VFSFileObjectAdapter) {
                try {
                    environment.put(bindingKey, convertToPath((VFSFileObjectAdapter) bindingValue).toString());
                } catch (Exception ex) {
                    throw new ScriptException(ex);
                }
            } else {
                environment.put(bindingKey, bindingValue.toString());
            }
        }

        // Add extra variables from scheduler
        String pasJobId = System.getProperty("pas.job.id");
        if (pasJobId != null) {
            environment.put("JOB_ID", pasJobId);
        }
        String pasJobName = System.getProperty("pas.job.name");
        if (pasJobName != null) {
            environment.put("JOB_NAME", pasJobName);
        }
        String pasTaskId = System.getProperty("pas.task.id");
        if (pasTaskId != null) {
            environment.put("TASK_ID", pasTaskId);
        }
        String pasTaskName = System.getProperty("pas.task.name");
        if (pasTaskName != null) {
            environment.put("TASK_NAME", pasTaskName);
        }
        String pasTaskIteration = System.getProperty("pas.task.iteration");
        if (pasTaskIteration != null) {
            environment.put("TASK_ITERATION", pasTaskIteration);
        }
        String pasTaskReplication = System.getProperty("pas.task.replication");
        if (pasTaskReplication != null) {
            environment.put("TASK_REPLICATION", pasTaskReplication);
        }
    }

    private void addMapBindingAsEnvironmentVariable(String bindingKey, Map<?, ?> bindingValue, Map<String, String> environment) {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) bindingValue).entrySet()) {
            environment.put(bindingKey + "_" + entry.getKey(), (entry.getValue() == null ? "" : entry.getValue().toString()));
        }
    }

    private void addCollectionBindingAsEnvironmentVariable(String bindingKey, Collection bindingValue, Map<String, String> environment) {
        Object[] bindingValueAsArray = bindingValue.toArray();
        addArrayBindingAsEnvironmentVariable(bindingKey, bindingValueAsArray, environment);
    }

    private void addArrayBindingAsEnvironmentVariable(String bindingKey, Object[] bindingValue, Map<String, String> environment) {
        for (int i = 0; i < bindingValue.length; i++) {
            environment.put(bindingKey + "_" + i, (bindingValue[i] == null ? "" : bindingValue[i].toString()));
        }
    }

    private String convertToPath(DataSpacesFileObject dsfo) throws Exception {
        String path = dsfo.getRealURI();
        URI uri = new URI(path);
        File f = new File(uri);
        return f.getCanonicalPath();
    }

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

        private void addHandlers(system.Object pslInstance,  EventHandler err, EventHandler debug, EventHandler verbose) {
            HandlerUtils.GetMethod("AddErrorHandler").Invoke(null, new system.Object[]{pslInstance, err});
            HandlerUtils.GetMethod("AddDebugHandler").Invoke(null, new system.Object[]{pslInstance, debug});
            HandlerUtils.GetMethod("AddVerboseHandler").Invoke(null, new system.Object[]{pslInstance, verbose});
        }

        private void addScript(system.Object psInstance, String script) {
            PowerShell.GetMethod("AddScript", new Type[]{String}).Invoke(psInstance, new system.Object[]{new system.String(script)});
        }

        private void setVariable(system.Object psInstance, String variableName, Object variableValue) {
            PowerShell.GetMethod("AddCommand", new Type[]{String}).Invoke(psInstance, new system.Object[]{new system.String("set-variable")});
            PowerShell.GetMethod("AddArgument", new Type[]{String}).Invoke(psInstance, new system.Object[]{new system.String(variableName)});
            PowerShell.GetMethod("AddArgument").Invoke(psInstance, new system.Object[]{Bridge.wrapJVM(variableValue)});
        }

        private IAsyncResult runAsynchronously(system.Object psInstance) {
            return (IAsyncResult) PowerShell.GetMethod("BeginInvoke", EMPTY_TYPE_ARRAY).Invoke(psInstance, null);
        }

        private IEnumerable res(system.Object psInstance, IAsyncResult res) {
            return (IEnumerable) PowerShell.GetMethod("EndInvoke").Invoke(psInstance,new  system.Object[]{(system.Object)res});
        }
        
        private void dispose(system.Object pslInstance) {
            PowerShell.GetMethod("Dispose", EMPTY_TYPE_ARRAY).Invoke(pslInstance, null);
        }
    }
}
