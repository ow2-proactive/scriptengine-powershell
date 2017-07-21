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
