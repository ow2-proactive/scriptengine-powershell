/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsr223.powershell;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

/**
 *
 * @author vbodnart
 */
public class PowerShellScriptEngineFactory implements ScriptEngineFactory {
    
    private static final String NAME = "PowerShell";
    private static final String ENGINE = "PowerShell interpreter";
    private static final String ENGINE_VERSION = "2";//new NativeShellRunner(new Cmd()).getInstalledVersion();
    private static final String LANGUAGE = "PowerShell";
    private static final String LANGUAGE_VERSION = "2";//new NativeShellRunner(new Cmd()).getMajorVersion();
    
    private static final ImmutableMap<String, Object> parameters = ImmutableMap.of(
                ScriptEngine.NAME, (Object)NAME,
                ScriptEngine.ENGINE, (Object)ENGINE,
                ScriptEngine.ENGINE_VERSION, (Object)ENGINE_VERSION,
                ScriptEngine.LANGUAGE, (Object)LANGUAGE,
                ScriptEngine.LANGUAGE_VERSION, (Object)LANGUAGE_VERSION);

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
        return parameters.get(key);
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
        return "Write-Output " + toDisplay;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
}
