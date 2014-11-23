package jsr223.powershell;

import net.sf.jni4net.Bridge;
import system.EventHandler;
import system.Type;
import system.reflection.Assembly;
import system.reflection.MethodInfo;

import java.io.File;

// Load all required standard and utils types
// Then performs calls on .Net objects by reflection
class PowerShellCachedCaller {

    final Type[] EMPTY_TYPE_ARRAY = new Type[]{};
    final Type HandlerUtils, Int32, String, PowerShell, PSDataStreams, DataAddedEventArgs;

    public PowerShellCachedCaller(File jsr223dll) {
        // Load jsr223utils.dll and register as main assembly
        Assembly wfAssembly = Assembly.LoadFrom(jsr223dll.getAbsolutePath());
        Bridge.RegisterAssembly(wfAssembly);
        Int32 = Type.GetType("System.Int32");
        String = Type.GetType("System.String");
        HandlerUtils = wfAssembly.GetType("utils.HandlerUtils");

        // Load automation assembly for PowerShell
        Assembly smaAssembly = Assembly.LoadWithPartialName("System.Management.Automation");
        PowerShell = smaAssembly.GetType("System.Management.Automation.PowerShell");
        PSDataStreams = smaAssembly.GetType("System.Management.Automation.PSDataStreams");
        DataAddedEventArgs = smaAssembly.GetType("System.Management.Automation.DataAddedEventArgs");
    }

    public system.Object createNewPowerShellInstance() {
        return PowerShell.GetMethod("Create", EMPTY_TYPE_ARRAY).Invoke(null, null);
    }

    public void addHandlers(system.Object pslInstance, EventHandler err, EventHandler debug, EventHandler verbose) {
        HandlerUtils.GetMethod("AddErrorHandler").Invoke(null, new system.Object[]{pslInstance, err});
        HandlerUtils.GetMethod("AddDebugHandler").Invoke(null, new system.Object[]{pslInstance, debug});
        HandlerUtils.GetMethod("AddVerboseHandler").Invoke(null, new system.Object[]{pslInstance, verbose});
    }

    public system.Object toBool(String bool) {
        return toSomething("toBool", bool);
    }

    public system.Object toInt(String bool) {
        return toSomething("toInt", bool);
    }

    public system.Object toLong(String bool) {
        return toSomething("toLong", bool);
    }

    public system.Object toDouble(String bool) {
        return toSomething("toDouble", bool);
    }

    public system.Object toByte(String bool) {
        return toSomething("toByte", bool);
    }

    public system.Object toChar(String bool) {
        return toSomething("toChar", bool);
    }

    public system.Object toSomething(String methodName, String value) {
        return HandlerUtils.GetMethod(methodName).Invoke(null, new system.Object[]{new system.String(value)});
    }

    public void addScript(system.Object psInstance, String script) {
        PowerShell.GetMethod("AddScript", new Type[]{String}).Invoke(psInstance, new system.Object[]{new system.String(script)});
    }

    public void setVariable(system.Object psInstance, java.lang.String variableName, system.Object variableValue) {
        PowerShell.GetMethod("AddCommand", new Type[]{String}).Invoke(psInstance, new system.Object[]{new system.String("set-variable")});
        PowerShell.GetMethod("AddArgument", new Type[]{String}).Invoke(psInstance, new system.Object[]{new system.String(variableName)});
        PowerShell.GetMethod("AddArgument").Invoke(psInstance, new system.Object[]{variableValue});
    }

    public system.Object invoke(system.Object psInstance) {


        return findInvokeMethod().Invoke(psInstance, null);
    }

    private MethodInfo findInvokeMethod() {
        for (MethodInfo methodInfo : PowerShell.GetMethods()) {
            if (methodInfo.getName().equals("Invoke")) {
                if (methodInfo.getReturnType().GetGenericArguments()[0].getName().equals("PSObject")) {
                    return methodInfo;
                }
            }
        }
        return null;
    }

    public void dispose(system.Object pslInstance) {
        PowerShell.GetMethod("Dispose", EMPTY_TYPE_ARRAY).Invoke(pslInstance, null);
    }
}
