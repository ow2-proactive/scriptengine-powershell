package jsr223.powershell;

import javax.script.ScriptException;

public class Main {

    public static void main(String[] args) throws ScriptException {
        System.out.println("----------> ok");
        String script = "";
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            script += arg + " ";
        }

        Object returnCode = new PowerShellScriptEngine().eval(script);
        System.exit((Integer) returnCode);
    }
}
