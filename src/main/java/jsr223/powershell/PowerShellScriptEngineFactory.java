/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsr223.powershell;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class PowerShellScriptEngineFactory implements ScriptEngineFactory {

    private static final String NAME = "PowerShell";

    private static final String ENGINE = "PowerShell interpreter";

    private static final String LANGUAGE = "PowerShell";

    private static final Logger logger = Logger.getLogger(PowerShellScriptEngineFactory.class);

    private static String engineVersion;

    @Override
    public String getEngineName() {
        return NAME;
    }

    @Override
    public String getEngineVersion() {
        return findEngineVersion();
    }

    @Override
    public List<String> getExtensions() {
        return Arrays.asList("ps1");
    }

    @Override
    public List<String> getMimeTypes() {
        return Arrays.asList("application/x-powershell",
                             "application/x-ps1",
                             "application/ps1",
                             "application/x-powershell-program",
                             "application/textedit",
                             "application/octet-stream");
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("powershell", "PowerShell");
    }

    @Override
    public String getLanguageName() {
        return LANGUAGE;
    }

    @Override
    public String getLanguageVersion() {
        return getEngineVersion();
    }

    @Override
    public Object getParameter(String key) {
        switch (key) {
            case ScriptEngine.NAME:
                return getEngineName();
            case ScriptEngine.ENGINE:
                return ENGINE;
            case ScriptEngine.ENGINE_VERSION:
                return getEngineVersion();
            case ScriptEngine.LANGUAGE:
                return getLanguageName();
            case ScriptEngine.LANGUAGE_VERSION:
                return getLanguageVersion();
            default:
                return null;
        }
    }

    @Override
    public String getMethodCallSyntax(String obj, String m, String... args) {
        String methodCall = m + " ";
        for (String arg : args) {
            methodCall += arg + " ";
        }
        return methodCall;
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        return "Write-Host " + toDisplay;
    }

    @Override
    public String getProgram(String... statements) {
        String program = "";
        for (String statement : statements) {
            program += statement + "\n";
        }
        return program;
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new PowerShellScriptEngine();
    }

    private String findEngineVersion() {
        if (engineVersion == null) {
            try (StringWriter output = new StringWriter(); StringWriter error = new StringWriter()) {
                ScriptEngine engine = getScriptEngine();
                engine.getContext().setWriter(output);
                engine.getContext().setErrorWriter(error);
                int engineVersionNumeric = (int) engine.eval("$PSVersionTable.PSVersion.Major");
                engineVersion = "" + engineVersionNumeric;
            } catch (Exception e) {
                logger.warn("Unable to load powershell script engine and determine version", e);
                return "0";
            }
        }
        return engineVersion;
    }
}
