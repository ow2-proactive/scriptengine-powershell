package jsr223.powershell;

import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;
import static org.junit.Assume.assumeTrue;
import org.junit.BeforeClass;

public class PowerShellScriptEngineNoForkTest {

    private static PowerShellScriptEngineNoFork scriptEngine;
    private static StringWriter scriptOutput;
    private static StringWriter scriptError;


    @BeforeClass
    public static void setup() {
        assumeTrue(System.getProperty("os.name").contains("Windows"));
        scriptEngine = new PowerShellScriptEngineNoFork();
        scriptOutput = new StringWriter();
        scriptEngine.getContext().setWriter(scriptOutput);
        scriptError = new StringWriter();
        scriptEngine.getContext().setErrorWriter(scriptError);
    }

    @Test
    public void evalHello() throws Exception {
        int res = (Integer) scriptEngine.eval("Write-Output 'hellooooo'");
        Assert.assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
        Assert.assertEquals("hellooooo", scriptOutput.toString());
    }
    
    @Test
    public void testWriteError() throws Exception {
        String errorMessage = "Halt!";
        int res = (Integer) scriptEngine.eval("Write-Error " + errorMessage);
        Assert.assertTrue("Script error output should contain the messages written by Write-Error cmdlet", scriptError.toString().contains(errorMessage));
        Assert.assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
    }
    
    @Test
    public void testWriteVerbose() throws Exception {
        String verboseMessage = "Blabla!";
        int res = (Integer) scriptEngine.eval("Write-Verbose " + verboseMessage);
        Assert.assertTrue("Script standard output should contain the messages written by Write-Verbose cmdlet", scriptOutput.toString().contains(verboseMessage));
        Assert.assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
    }

    @Test
    public void testWritHost() throws Exception {
        String message = "Blabla!";
        int res = (Integer) scriptEngine.eval("Write-Host " + message);
        Assert.assertTrue("Script standard output should contain the messages written by Write-Host cmdlet", scriptOutput.toString().contains(message));
        Assert.assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
    }

    @Test
    public void testWriteDebug() throws Exception {
        String debugMessage = "Debug!";
        int res = (Integer) scriptEngine.eval("Write-Debug " + debugMessage);
        Assert.assertTrue("Script standard output should contain the messages written by Write-Debug cmdlet", scriptOutput.toString().contains(debugMessage));
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
        Object res = scriptEngine.eval("Write-Output $stringVar"); // + $env:integerVar.CompareTo(42) + $env:floatVar.CompareTo(42.0)");
        Assert.assertEquals("aString", res.toString());
    }

    @Test
    public void testBindingStringWithReturn() throws Exception {
        scriptEngine.put("stringVar", "aString");
        Object res = scriptEngine.eval("return $stringVar"); // + $env:integerVar.CompareTo(42) + $env:floatVar.CompareTo(42.0)");
        Assert.assertEquals("aString", res.toString());
    }

    @Test
    public void testBindingStringWithReturnInt() throws Exception {
        scriptEngine.put("intVar", 42);
        Object res = scriptEngine.eval("return $intVar"); // + $env:integerVar.CompareTo(42) + $env:floatVar.CompareTo(42.0)");
        Assert.assertEquals(42, res);
    }
    
    @Test
    public void testEvalReader() throws Exception {
        StringReader sr = new StringReader("Write-Output 'Hello World'");
        int res = (Integer) scriptEngine.eval(sr);
        Assert.assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
    }
}
