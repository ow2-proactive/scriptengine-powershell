package jsr223.powershell;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

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
    public void evalWorkingScript() throws Exception {
        String result = (String) scriptEngine.eval("Write-Output 'hello'");
        assertEquals("hello", result);
    }

    @Test
    public void emptyScript() throws Exception {
        assertEquals(null, scriptEngine.eval(""));
    }

    @Test(expected = ScriptException.class)
    public void invalidScript() throws Exception {
        scriptEngine.eval("Write-Output2 'hello'");
    }

    @Test(expected = ScriptException.class)
    public void testWriteError() throws Exception {
        scriptEngine.eval("Write-Error 'hello'");
    }

    @Test
    public void testWriteVerbose() throws Exception {
        String verboseMessage = "Blabla!";
        int res = (Integer) scriptEngine.eval("Write-Verbose " + verboseMessage);
        assertTrue("Script standard output should contain the messages written by Write-Verbose cmdlet", scriptOutput.toString().contains(verboseMessage));
        assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
    }

    @Test
    public void testWritHost() throws Exception {
        String message = "Blabla!";
        int res = (Integer) scriptEngine.eval("Write-Host " + message);
        assertTrue("Script standard output should contain the messages written by Write-Host cmdlet", scriptOutput.toString().contains(message));
        assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
    }

    @Test
    public void testWriteDebug() throws Exception {
        String debugMessage = "Debug!";
        int res = (Integer) scriptEngine.eval("Write-Debug " + debugMessage);
        assertTrue("Script standard output should contain the messages written by Write-Debug cmdlet", scriptOutput.toString().contains(debugMessage));
        assertEquals(PowerShellScriptEngine.OK_EXIT_CODE, res);
    }

    @Test
    public void exitCodeIsNotHandledUseErrors() throws Exception {
        assertEquals(null, scriptEngine.eval("exit 123"));
    }

    @Test
    public void testEvalReader() throws Exception {
        StringReader sr = new StringReader("Write-Output 'hello'");
        assertEquals("hello", scriptEngine.eval(sr));
    }
}
