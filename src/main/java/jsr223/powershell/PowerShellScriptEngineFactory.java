/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsr223.powershell;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerShellScriptEngineFactory implements ScriptEngineFactory {

    private static final String NAME = "PowerShell";
    private static final String ENGINE = "PowerShell interpreter";
    private static final String ENGINE_VERSION = "3";
    private static final String LANGUAGE = "PowerShell";
    private static final String LANGUAGE_VERSION = "3";

    private static final Map<String, Object> PARAMETERS = new HashMap<String, Object>();

    static {
        PARAMETERS.put(ScriptEngine.NAME, NAME);
        PARAMETERS.put(ScriptEngine.ENGINE, ENGINE);
        PARAMETERS.put(ScriptEngine.ENGINE_VERSION, ENGINE_VERSION);
        PARAMETERS.put(ScriptEngine.LANGUAGE, LANGUAGE);
        PARAMETERS.put(ScriptEngine.LANGUAGE_VERSION, LANGUAGE_VERSION);
    }

    @Override
    public String getEngineName() {
        return NAME;
    }

    @Override
    public String getEngineVersion() {
        return ENGINE_VERSION;
    }

    @Override
    public List<String> getExtensions() {
        return Arrays.asList("ps1");
    }

    @Override
    public List<String> getMimeTypes() {
        return Arrays.asList("application/x-powershell", "application/x-ps1", "application/ps1",
                "application/x-powershell-program", "application/textedit", "application/octet-stream");
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
        return LANGUAGE_VERSION;
    }

    @Override
    public Object getParameter(String key) {
        return PARAMETERS.get(key);
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
}
