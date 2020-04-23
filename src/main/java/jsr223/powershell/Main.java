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

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;


public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printHelpAndExit();
        }
        String script = "";
        String filename = args[0];
        if (new File(filename).exists()) {
            script = IOUtils.toString(new FileInputStream(filename));
        } else {
            for (String arg : args) {
                script += arg + " ";
            }
        }
        PowerShellScriptEngine scriptEngine = new PowerShellScriptEngine();
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        scriptEngine.getContext().setWriter(writer);
        Object result = scriptEngine.eval(script);
        writer.flush();
        if (result != null) {
            System.out.println(result);
        }
    }

    public static void printHelpAndExit() {
        System.out.println("Please specify: PowerShell script file or PowerShell expression");
        System.exit(0);
    }
}
