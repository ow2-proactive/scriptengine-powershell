package jsr223.powershell;

import com.google.common.io.CharStreams;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import net.sf.jni4net.Bridge;
import org.objectweb.proactive.extensions.dataspaces.api.DataSpacesFileObject;
import org.objectweb.proactive.extensions.dataspaces.vfs.adapter.VFSFileObjectAdapter;
import system.Type;
import system.collections.IList;
import system.reflection.Assembly;

public class PowerShellScriptEngineNoFork extends AbstractScriptEngine {

    public static String PSHELL_PATH = "C:\\Windows\\System32\\WindowsPowershell\\v1.0\\PowerShell.exe";
    public static String WINDOW_STYLE = "Hidden";
    public static String EXEC_POLICY = "RemoteSigned";//"Unrestricted";    

    public static int OK_EXIT_CODE = 0;
    
    Type[] EMPTY_TYPE_ARRAY = new Type[]{};    
    private final HashMap<String, Type> types;
    
    private system.EventHandler errorHandler;
    private system.EventHandler debugHandler;
    private system.EventHandler verboseHandler;
    private system.EventHandler outputHandler;
    
    public PowerShellScriptEngineNoFork(){
        this.types = new HashMap<String, Type>();
        this.init();
    }
    
    public void init(){
        // create bridge, with default setup
        // it will lookup jni4net.n.dll next to jni4net.j.jar 
        Bridge.setDebug(true);
        Bridge.setVerbose(true);

        try {
            Bridge.init();
            
            // Get directory that contains the jni4net.jar
            URL jni4netJarURL = Bridge.class.getProtectionDomain().getCodeSource().getLocation();
            File jni4netJarFile = new File(jni4netJarURL.toURI());            
            File jsr223Dll = new File(jni4netJarFile.getParentFile(), "jsr223utils.dll");            
            System.out.println("---> utils dll location " + jsr223Dll);
            
            // Load jsr223utils.dll and register as main assembly
            Assembly wfAssembly = Assembly.LoadFrom(jsr223Dll.getAbsolutePath());
            Bridge.RegisterAssembly(wfAssembly);
            
            // Load powershell assembly
            Assembly smaAssembly = Assembly.LoadWithPartialName("System.Management.Automation");

            // Load all required standard and utils types
            types.put("PowerShellStreamsHandlerAdder", wfAssembly.GetType("utils.PowerShellStreamsHandlerAdder"));            
            types.put("Int32", Type.GetType("System.Int32"));
            types.put("String",  Type.GetType("System.String"));
            types.put("PowerShell",  smaAssembly.GetType("System.Management.Automation.PowerShell"));
            types.put("PSDataStreams",  smaAssembly.GetType("System.Management.Automation.PSDataStreams"));
            types.put("DataAddedEventArgs",  smaAssembly.GetType("System.Management.Automation.DataAddedEventArgs"));
            
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

        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize the jn4net Bridge", ex);
        }               
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {                
        system.Object pslInstance = null;
        try {
            // Create new instance of PowerShell
            long s = System.currentTimeMillis();
            pslInstance = types.get("PowerShell").GetMethod("Create", EMPTY_TYPE_ARRAY).Invoke(null, null);
            System.out.println("---> Created new instance of PowerShell in " + (System.currentTimeMillis() - s));

            // Add handlers
            types.get("PowerShellStreamsHandlerAdder").GetMethod("AddErrorHandler").Invoke(null, new system.Object[]{pslInstance, this.errorHandler});
            types.get("PowerShellStreamsHandlerAdder").GetMethod("AddDebugHandler").Invoke(null, new system.Object[]{pslInstance, this.debugHandler});
            types.get("PowerShellStreamsHandlerAdder").GetMethod("AddVerboseHandler").Invoke(null, new system.Object[]{pslInstance, this.verboseHandler});

            // Add script to run
            system.String scriptS = new system.String(script);
            types.get("PowerShell").GetMethod("AddScript", new Type[]{system.String.typeof()}).Invoke(pslInstance, new system.Object[]{scriptS});

            // Run script asynchronously
            system.IAsyncResult result = (system.IAsyncResult) types.get("PowerShell").GetMethod("BeginInvoke", EMPTY_TYPE_ARRAY).Invoke(pslInstance, null);

            // Wait until
            while (!result.isCompleted()) {
                System.out.println("---------> waiting for script to finish ... ");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }

            System.out.println("--> disposing the PowerShellInstance");
        } finally {
            if (pslInstance != null) {
                types.get("PowerShell").GetMethod("Dispose", EMPTY_TYPE_ARRAY).Invoke(pslInstance, null);
            }
        }
        return 0;     
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
}
