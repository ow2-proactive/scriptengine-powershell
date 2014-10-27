

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
import java.io.StringReader;
import java.io.StringWriter;
import jsr223.powershell.PowerShellScriptEngine;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class CmdScriptEngineTest {

    private PowerShellScriptEngine scriptEngine;
    private StringWriter scriptOutput;
    private StringWriter scriptError;

    @Before
    public void runOnlyOnWindows() {
        assumeTrue(System.getProperty("os.name").contains("Windows"));
    }

    @Before
    public void setup() {
        scriptEngine = new PowerShellScriptEngine();
        scriptOutput = new StringWriter();
        scriptEngine.getContext().setWriter(scriptOutput);
        scriptError = new StringWriter();
        scriptEngine.getContext().setErrorWriter(scriptError);
    }

    @Test
    public void evaluate_echo_command() throws Exception {
        Integer returnCode = (Integer) scriptEngine.eval("Write-Output hello");

        //assertEquals(NativeShellRunner.RETURN_CODE_OK, returnCode);
        //assertEquals("hello\n", scriptOutput.toString());
    }
}
