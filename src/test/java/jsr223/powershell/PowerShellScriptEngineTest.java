package jsr223.powershell;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class PowerShellScriptEngineTest {

    private static PowerShellScriptEngine scriptEngine;
    private static StringWriter scriptOutput;
    private static StringWriter scriptError;

    @BeforeClass
    public static void setup() {
        assumeTrue(System.getProperty("os.name").contains("Windows"));
        scriptEngine = new PowerShellScriptEngine();
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

    @Test
    public void noUnderlyingBaseObject() throws Exception {
        assertNotNull(scriptEngine.eval("Get-Variable"));
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
        scriptEngine.eval("Write-Verbose " + verboseMessage + " -verbose");
        assertTrue("Script standard output should contain the messages written by Write-Verbose cmdlet", scriptOutput.toString().contains(verboseMessage));
    }

    @Test(expected = ScriptException.class)
    public void testWriteHostIsNotSupported() throws Exception {
        String message = "Blabla!";
        scriptEngine.eval("Write-Host " + message);
    }

    @Test
    public void testWriteDebug() throws Exception {
        String debugMessage = "Debug!";
        scriptEngine.eval("Write-Debug " + debugMessage + " -debug");
        assertTrue("Script standard output should contain the messages written by Write-Debug cmdlet", scriptOutput.toString().contains(debugMessage));
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
