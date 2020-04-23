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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;

import javax.script.ScriptContext;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.io.output.WriterOutputStream;

import net.sf.jni4net.Bridge;
import system.EventHandler;
import system.Object;
import system.Type;
import system.reflection.Assembly;
import system.reflection.MethodInfo;


// Load all required standard and utils types
// Then performs calls on .Net objects by reflection
class PowerShellCachedCaller {

    public final Type[] EMPTY_TYPE_ARRAY = new Type[] {};

    public final Type HandlerUtils, Int32, String, PowerShell, PSDataStreams, Runspace, DataAddedEventArgs, Object,
            Hashtable, VariablesMap, PrintStream;

    public final Type IList;

    static PowerShellCachedCaller instance;

    public static PowerShellCachedCaller getInstance() {
        if (instance == null) {
            instance = new PowerShellCachedCaller(initAndFindDll());
        }
        return instance;
    }

    private PowerShellCachedCaller(File jsr223dll) {
        // Load jsr223utils.dll and register as main assembly
        Assembly wfAssembly = Assembly.LoadFrom(jsr223dll.getAbsolutePath());
        Bridge.RegisterAssembly(wfAssembly);
        Int32 = Type.GetType("System.Int32");
        String = Type.GetType("System.String");
        IList = Type.GetType("System.Collections.IList");
        Hashtable = Type.GetType("System.Collections.Hashtable");
        Object = Type.GetType("System.Object");
        HandlerUtils = wfAssembly.GetType("utils.HandlerUtils");
        VariablesMap = wfAssembly.GetType("utils.VariablesMap");

        PrintStream = Type.GetType("java.io.PrintStream");

        // Load automation assembly for PowerShell
        Assembly smaAssembly = Assembly.LoadWithPartialName("System.Management.Automation");
        PowerShell = smaAssembly.GetType("System.Management.Automation.PowerShell");
        PSDataStreams = smaAssembly.GetType("System.Management.Automation.PSDataStreams");
        DataAddedEventArgs = smaAssembly.GetType("System.Management.Automation.DataAddedEventArgs");
        Runspace = smaAssembly.GetType("System.Management.Automation.Runspaces.Runspace");

    }

    private synchronized static File initAndFindDll() {
        try {
            // create bridge, with default setup
            // it will lookup jni4net.n.dll next to jni4net.j.jar
            boolean isDebug = System.getProperty("powershell.debug") != null &&
                              Boolean.parseBoolean(System.getProperty("powershell.debug"));
            Bridge.setDebug(isDebug);
            Bridge.setVerbose(isDebug);
            Bridge.init();

            // Get directory that contains the jni4net.jar
            URL jni4netJarURL = Bridge.class.getProtectionDomain().getCodeSource().getLocation();
            File jni4netJarFile = new File(jni4netJarURL.toURI());
            return new File(jni4netJarFile.getParentFile(), "jsr223utils.dll");

        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize the jn4net Bridge", ex);
        }
    }

    public synchronized PowerShellEnvironment createNewPowerShellInstance(ScriptContext context) {

        PrintStream outStream = new PrintStream(new WriterOutputStream(context.getWriter(), Charset.defaultCharset()));
        PrintStream errStream = new PrintStream(new WriterOutputStream(context.getErrorWriter(),
                                                                       Charset.defaultCharset()));

        system.Object ps = PowerShell.GetMethod("Create", EMPTY_TYPE_ARRAY).Invoke(null, null);
        system.Object runSpace = HandlerUtils.GetMethod("CreateRunspaceAndAttachToPowerShell",
                                                        new Type[] { PowerShell, PrintStream, PrintStream })
                                             .Invoke(null,
                                                     new system.Object[] { ps, Bridge.wrapJVM(outStream),
                                                                           Bridge.wrapJVM(errStream) });

        return new PowerShellEnvironment(ps, runSpace, outStream, errStream);
    }

    public synchronized void closeRunspace(system.Object runspace) {
        Runspace.GetMethod("Close", EMPTY_TYPE_ARRAY).Invoke(runspace, null);
    }

    public synchronized void addHandlers(system.Object pslInstance, EventHandler err) {
        HandlerUtils.GetMethod("AddErrorHandler").Invoke(null, new system.Object[] { pslInstance, err });
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
        return HandlerUtils.GetMethod(methodName).Invoke(null, new system.Object[] { new system.String(value) });
    }

    public system.Object fromDataContractXMLToObject(String json) {
        return HandlerUtils.GetMethod("DeserializeWithNetDcs").Invoke(null,
                                                                      new system.Object[] { new system.String(json) });
    }

    public String fromObjectToDataContractXML(system.Object object) {
        return Bridge.convert((system.String) HandlerUtils.GetMethod("SerializeWithNetDcs")
                                                          .Invoke(null, new system.Object[] { object }));
    }

    public system.Object createVariablesMap(system.collections.Hashtable inheritedMap,
            system.collections.Hashtable scopeMap, system.collections.Hashtable scriptMap) {
        return HandlerUtils.GetMethod("createVariablesMap")
                           .Invoke(null, new system.Object[] { inheritedMap, scopeMap, scriptMap });
    }

    public system.collections.Hashtable getInheritedMap(system.Object variablesMap) {
        return (system.collections.Hashtable) HandlerUtils.GetMethod("getInheritedMap")
                                                          .Invoke(null, new Object[] { variablesMap });
    }

    public system.collections.Hashtable getScopeMap(system.Object variablesMap) {
        return (system.collections.Hashtable) HandlerUtils.GetMethod("getScopeMap")
                                                          .Invoke(null, new Object[] { variablesMap });
    }

    public system.collections.Hashtable getScriptMap(system.Object variablesMap) {
        return (system.collections.Hashtable) HandlerUtils.GetMethod("getScriptMap")
                                                          .Invoke(null, new Object[] { variablesMap });
    }

    public void addScript(system.Object psInstance, String script, system.Object args) {
        PowerShell.GetMethod("AddScript", new Type[] { String })
                  .Invoke(psInstance, new system.Object[] { new system.String(script) });
        if (args != null) {
            PowerShell.GetMethod("AddParameters", new Type[] { IList }).Invoke(psInstance,
                                                                               new system.Object[] { args });
        }
    }

    public void setVariable(system.Object psInstance, java.lang.String variableName, system.Object variableValue) {
        PowerShell.GetMethod("AddCommand", new Type[] { String })
                  .Invoke(psInstance, new system.Object[] { new system.String("set-variable") });
        PowerShell.GetMethod("AddArgument", new Type[] { String })
                  .Invoke(psInstance, new system.Object[] { new system.String(variableName) });
        PowerShell.GetMethod("AddArgument").Invoke(psInstance, new system.Object[] { variableValue });
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

    public system.Object getVariables(system.Object psInstance, String name) {
        return HandlerUtils.GetMethod("GetVariable")
                           .Invoke(null, new system.Object[] { psInstance, new system.String(name) });
    }

    public static class PowerShellEnvironment {
        system.Object powershell;

        system.Object runspace;

        PrintStream outStream;

        PrintStream errStream;

        public PowerShellEnvironment(system.Object powershell, system.Object runspace, PrintStream outStream,
                PrintStream errStream) {
            this.powershell = powershell;
            this.runspace = runspace;
            this.outStream = outStream;
            this.errStream = errStream;

        }

        public system.Object getPowershell() {
            return powershell;
        }

        public system.Object getRunspace() {
            return runspace;
        }

        public java.io.PrintStream getOutStream() {
            return outStream;
        }

        public java.io.PrintStream getErrStream() {
            return errStream;
        }
    }
}
