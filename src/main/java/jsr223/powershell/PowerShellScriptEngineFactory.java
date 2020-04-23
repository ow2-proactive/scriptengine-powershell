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

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

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
        try {
            return new PowerShellScriptEngine();
        } catch (Throwable e) {
            throw new RuntimeException("Powershell Script engine is not available", e);
        }
    }

    private String findEngineVersion() {
        if (engineVersion == null) {
            try (StringWriter output = new StringWriter(); StringWriter error = new StringWriter()) {
                ScriptEngine engine = getScriptEngine();
                if (engine != null) {
                    engine.getContext().setWriter(output);
                    engine.getContext().setErrorWriter(error);
                    int engineVersionNumeric = (int) engine.eval("$PSVersionTable.PSVersion.Major");
                    engineVersion = "" + engineVersionNumeric;
                } else {
                    return "0";
                }
            } catch (Throwable e) {
                logger.debug("Unable to load powershell script engine and determine version", e);
                return "0";
            }
        }
        return engineVersion;
    }
}
