package jsr223.powershell;

import com.google.common.io.CharStreams;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
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
import system.Type;
import system.reflection.Assembly;

public class PowerShellScriptEngineNoFork extends AbstractScriptEngine {

    public static String PSHELL_PATH = "C:\\Windows\\System32\\WindowsPowershell\\v1.0\\PowerShell.exe";
    public static String WINDOW_STYLE = "Hidden";
    public static String EXEC_POLICY = "RemoteSigned";//"Unrestricted";    

    public static int OK_EXIT_CODE = 0;
    
    public void init(){
        // create bridge, with default setup
        // it will lookup jni4net.n.dll next to jni4net.j.jar 
        Bridge.setDebug(true);
        Bridge.setVerbose(true);

        try {
            Bridge.init();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to initialize the jn4net Bridge", ex);
        }
        
        // From System
        Type[] EMPTY_TYPE_ARRAY = new Type[]{};
//        Type Int32Type = Type.GetType("System.Int32");
        Type StringType = Type.GetType("System.String");
        
        Assembly smaAssembly = Assembly.LoadWithPartialName("System.Management.Automation");
        final Type PowerShellType = smaAssembly.GetType("System.Management.Automation.PowerShell");
        final Type PSDataStreamsType = smaAssembly.GetType("System.Management.Automation.PSDataStreams");
        final Type DataAddedEventArgsType = smaAssembly.GetType("System.Management.Automation.DataAddedEventArgs");
    }
    

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {

        
        
        return null;
     
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
