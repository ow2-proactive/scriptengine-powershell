package jsr223.powershell;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

public class PowerShellScriptEngine extends AbstractScriptEngine{
    
    public static String PSHELL_PATH = "C:\\Windows\\System32\\WindowsPowershell\\v1.0\\PowerShell.exe";
    public static String WINDOW_STYLE = "Hidden";
    public static String EXEC_POLICY = "RemoteSigned";//"Unrestricted";    

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        
        System.out.println("--> Running " + script);
        // dump string into a file
        // start powershell process with security that allows running scripts
        // redirect output
        // get result
        File tempDir = new File(System.getProperty("java.io.tmpdir"),"powershell-jsr223");
        tempDir.deleteOnExit();
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }        
        
        String scriptName = (String)context.getAttribute("scriptName");
        if (scriptName == null) {
            scriptName = "pshellScript";
        }
        
        File scriptFile = new File(scriptName);
        scriptFile.deleteOnExit();
        if (!scriptFile.exists()) {
            try {            
                scriptFile.createNewFile();
            } catch (IOException e) {
                throw new ScriptException(e);
            }
        }

        try {
            Files.write( script, scriptFile , Charsets.UTF_8 );
        } catch( IOException e ) {
            throw new ScriptException(e);
        }
        
        //int exitValue = run(commandAsTemporaryFile, context);
        //commandAsTemporaryFile.delete();
        
        ProcessBuilder b = new ProcessBuilder();
        b.command(PSHELL_PATH, 
                "-WindowStyle", WINDOW_STYLE,
                "-ExecutionPolicy", EXEC_POLICY,
                "-NoLogo", 
                "-File", scriptFile.getAbsolutePath());
        
        //b.redirectError(ProcessBuilder.Redirect.)
        try {
            final Process process = b.start();
            
            //Thread output = readProcessOutput(process.getInputStream(), processOutput);
            //Thread error = readProcessOutput(process.getErrorStream(), processError);

            //output.start();
            //error.start();

            process.waitFor();
            //output.join();
            //error.join();
            System.out.println("--------->< OK");
            return process.exitValue();
        } catch (IOException | InterruptedException e) {
            throw new ScriptException(e);
        }        
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
