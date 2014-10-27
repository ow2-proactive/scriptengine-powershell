package jsr223.powershell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Before;
import org.junit.Test;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.channels.Channel;
import javax.script.ScriptEngine;
import org.junit.Assert;
import static org.junit.Assume.assumeTrue;

public class PowerShellScriptEngineTest {

    private PowerShellScriptEngine scriptEngine;

    @Before
    public void runOnlyOnWindows() {
        assumeTrue(System.getProperty("os.name").contains("Windows"));
    }

    @Before
    public void setup() {
        scriptEngine = new PowerShellScriptEngine();       
    }

    @Test
    public void evalHello() throws Exception {
        int res = (Integer) scriptEngine.eval("Write-Output 'hellooooo'");
        Assert.assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
    }
    
    @Test
    public void testExitCode() throws Exception {
        int res = (Integer) scriptEngine.eval("exit 123");
        Assert.assertEquals(123, res);
    }        
    
    @Test
    public void testBindingString() throws Exception {
        scriptEngine.put("stringVar", "aString");        
        scriptEngine.put("integerVar", 42);
        scriptEngine.put("floatVar", 42.0);
        int res = (Integer) scriptEngine.eval("exit $env:stringVar.CompareTo('aString') + $env:integerVar.CompareTo('42') + $env:floatVar.CompareTo('42.0')");
        Assert.assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
    }
}
