/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package jsr223.powershell;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.script.ScriptException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class PowerShellScriptEngineTest {

    private static PowerShellScriptEngine scriptEngine;

    private static StringWriter scriptOutput;

    private static StringWriter scriptError;

    @Before
    public void setup() {
        assumeTrue(System.getProperty("os.name").contains("Windows"));
        scriptEngine = new PowerShellScriptEngine();
        scriptOutput = new StringWriter();
        scriptEngine.getContext().setWriter(scriptOutput);
        scriptError = new StringWriter();
        scriptEngine.getContext().setErrorWriter(scriptError);
    }

    @After
    public void clean() {

    }

    @Test
    public void testWriteOutput() throws Exception {
        String result = (String) scriptEngine.eval("Write-Output 'OriteWutput'");
        assertEquals("OriteWutput", result);
        writeOutputs();
        assertTrue(scriptOutput.toString().contains("OriteWutput"));
    }

    @Test
    public void writeInfoTest() throws Exception {
        String version = scriptEngine.getFactory().getLanguageVersion();
        int numericVersion = Integer.parseInt(version);
        if (numericVersion >= 5) {
            scriptEngine.eval("Write-Information 'advertisement'");
            writeOutputs();
            assertTrue(scriptOutput.toString().contains("advertisement"));
        } else {
            System.out.println("Write-Information not available on Powershell version " + numericVersion);
        }
    }

    @Test
    public void writeWarningTest() throws Exception {
        scriptEngine.eval("Write-Warning 'attention'");
        writeOutputs();
        assertTrue(scriptError.toString().contains("attention"));
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
        try {
            scriptEngine.eval("Write-Output2 'hello'");
        } catch (ScriptException e) {
            System.out.println(scriptError.toString());
            assertTrue(scriptError.toString().contains("Write-Output2"));
            throw e;
        }
    }

    @Test(expected = ScriptException.class)
    public void testWriteError() throws Exception {
        try {
            scriptEngine.eval("Write-Error 'this is an error'");
        } catch (ScriptException e) {
            writeOutputs();
            assertTrue(scriptError.toString().contains("this is an error"));
            throw e;
        }
    }

    @Test
    public void testWriteVerbose() throws Exception {
        String verboseMessage = "VriteWerbose...";
        scriptEngine.eval("Write-Verbose " + verboseMessage + " -verbose");
        writeOutputs();
        assertTrue("Script standard output should contain the messages written by Write-Verbose cmdlet",
                   scriptOutput.toString().contains(verboseMessage));
    }

    private void writeOutputs() {
        // the following line displays the test name executing this method
        System.out.println("----- " + Thread.currentThread().getStackTrace()[2].getMethodName() + " -----");
        System.out.println("Output:" + System.lineSeparator() + scriptOutput.toString());
        System.out.println("Error: " + System.lineSeparator() + scriptError.toString());
    }

    @Test
    public void testWriteHost() throws Exception {
        String version = scriptEngine.getFactory().getLanguageVersion();
        int numericVersion = Integer.parseInt(version);
        if (numericVersion >= 5) {
            // apparently it is used to be not supported.
            String message = "HriteWost...";
            scriptEngine.eval("Write-Host '" + message + "'");
            writeOutputs();
            assertTrue(scriptOutput.toString().contains(message));
        } else {
            System.out.println("Write-Host not available on Powershell engine version " + numericVersion);
        }
    }

    @Test
    public void testWriteDebug() throws Exception {
        String debugMessage = "DriteWebug!";
        try {
            scriptEngine.eval("Write-Debug " + debugMessage + " -debug");
        } catch (Exception e) {
            Assert.fail();
        } finally {
            writeOutputs();
        }
        assertTrue("Script standard output should contain the messages written by Write-Debug cmdlet",
                   scriptOutput.toString().contains(debugMessage));
    }

    @Test
    public void testWriteProgress() throws Exception {
        String progressScript = "for ($I = 1; $I -le 100; $I++ )\n" +
                                "{Write-Progress -Activity \"Search in Progress\" -Status \"$I% Complete:\" -PercentComplete $I;}";
        try {
            scriptEngine.eval(progressScript);
        } catch (Exception e) {
            Assert.fail();
        } finally {
            writeOutputs();
        }
        assertTrue("Script standard output should contain the messages written by Write-Progress cmdlet",
                   scriptOutput.toString().contains("Search in Progress"));
    }

    @Test
    public void exitCodeIsNotHandledUseErrors() throws Exception {
        assertEquals(null, scriptEngine.eval("exit 123"));
    }

    @Test
    public void testEvalReader() throws Exception {
        StringReader sr = new StringReader("'hello';'hella'");
        Object result = scriptEngine.eval(sr);
        writeOutputs();

        assertTrue(result instanceof List);
        List listResult = (List) result;
        assertTrue(listResult.size() == 2);
        assertTrue(listResult.contains("hello"));
        assertTrue(listResult.contains("hella"));
        assertTrue("Script standard output should contain the messages written by Write-Output cmdlet",
                   scriptOutput.toString().contains("hello"));
    }
}
