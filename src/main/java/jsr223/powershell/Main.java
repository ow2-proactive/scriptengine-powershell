package jsr223.powershell;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {        
        if (args.length == 0) {
            printHelpAndExit();
        }        
        String script = "";         
        String option = args[0];
        switch (option) {
            case "-e":
                script = args[1];
                break;
            case "-f":   
                script = Resources.toString(new File(args[1]).toURI().toURL(), Charsets.UTF_8);
                break;
            default:
                script = args[0];
        }

        Object returnCode = new PowerShellScriptEngine().eval(script);
        System.exit((Integer) returnCode);
    }
    
    public static void printHelpAndExit(){
        System.out.println("Please specify: -f <script.ps1> or -e <PowerShell expression>");
        System.exit(0);
    }
}
