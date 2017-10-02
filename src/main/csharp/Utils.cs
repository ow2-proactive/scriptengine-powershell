using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Globalization;
using System.Management.Automation;
using System.Management.Automation.Host;
using System.Management.Automation.Runspaces;
using System.Security;
using System.Threading;
using System.IO;
using System.Runtime.Serialization;
using System.Text;
using System.Collections;

namespace utils
{
    public class HandlerUtils
    {
        public static void AddErrorHandler(PowerShell ps, System.EventHandler errorEvt)
        {
            ps.Streams.Error.DataAdded += new EventHandler<DataAddedEventArgs>(errorEvt);
        }

        public static void AddDebugHandler(PowerShell ps, System.EventHandler errorEvt)
        {
            ps.Streams.Debug.DataAdded += new EventHandler<DataAddedEventArgs>(errorEvt);
        }

        public static void AddInformationHandler(PowerShell ps, System.EventHandler errorEvt)
        {
            ps.Streams.Information.DataAdded += new EventHandler<DataAddedEventArgs>(errorEvt);
        }

        public static void AddWarningHandler(PowerShell ps, System.EventHandler errorEvt)
        {
            ps.Streams.Warning.DataAdded += new EventHandler<DataAddedEventArgs>(errorEvt);
        }

        public static void AddProgressHandler(PowerShell ps, System.EventHandler errorEvt)
        {
            ps.Streams.Progress.DataAdded += new EventHandler<DataAddedEventArgs>(errorEvt);
        }

        public static void AddVerboseHandler(PowerShell ps, System.EventHandler errorEvt)
        {
            ps.Streams.Verbose.DataAdded += new EventHandler<DataAddedEventArgs>(errorEvt);
        }

        public static object GetVariable(PowerShell ps, System.String name)
        {
            return ps.Runspace.SessionStateProxy.GetVariable(name);
        }

        public static System.Boolean toBool(String value)
        {
            return System.Boolean.Parse(value);
        }

        public static System.Int64 toLong(String value)
        {
            return System.Int64.Parse(value);
        }

        public static System.Int32 toInt(String value)
        {
            return System.Int32.Parse(value);
        }

        public static System.Double toDouble(String value)
        {
            return System.Double.Parse(value);
        }

        public static System.Byte toByte(String value)
        {
            return System.Byte.Parse(value);
        }

        public static System.Char toChar(String value)
        {
            return System.Char.Parse(value);
        }

        public static VariablesMap createVariablesMap(Hashtable inheritedMap, Hashtable scopeMap, Hashtable scriptMap)
        {
            return new VariablesMap(inheritedMap, scopeMap, scriptMap);
        }

        public static Hashtable getInheritedMap(VariablesMap map)
        {
            return map.InheritedMap;
        }

        public static Hashtable getScopeMap(VariablesMap map)
        {
            return map.ScopeMap;
        }

        public static Hashtable getScriptMap(VariablesMap map)
        {
            return map.ScriptMap;
        }

        public static string SerializeWithNetDcs(object obj)
        {
            using (var ms = new MemoryStream())
            {
                using (var sr = new StreamReader(ms, Encoding.UTF8))
                {
                    var serializer = new NetDataContractSerializer();
                    serializer.WriteObject(ms, obj);
                    ms.Position = 0;
                    return sr.ReadToEnd();
                }
            }
        }

        public static object DeserializeWithNetDcs(string xml)
        {
            using (var ms = new MemoryStream())
            {
                using (var sw = new StreamWriter(ms, Encoding.UTF8))
                {
                    sw.Write(xml);
                    sw.Flush();
                    ms.Position = 0;
                    var deserializer = new NetDataContractSerializer();
                    return deserializer.ReadObject(ms);
                }
            }
        }

        public static Runspace CreateRunspaceAndAttachToPowerShell(PowerShell ps, java.io.PrintStream outStream, java.io.PrintStream errStream)
        {
            JVMPSHostUserInterface ui = new JVMPSHostUserInterface(outStream, errStream);
            JVMPSHost psHost = new JVMPSHost(ui);
            Runspace jvm_Runspace = RunspaceFactory.CreateRunspace(psHost);
            ps.Runspace = jvm_Runspace;
            jvm_Runspace.Open();
            return jvm_Runspace;
        }

    }

    public class VariablesMap : Hashtable
    {
        private Hashtable inheritedMap;
        private Hashtable scopeMap;
        private Hashtable scriptMap;

        public VariablesMap(Hashtable inheritedMap, Hashtable scopeMap, Hashtable scriptMap)
        {
            this.inheritedMap = inheritedMap;
            this.scopeMap = scopeMap;
            this.scriptMap = scriptMap;      
        }

        public Hashtable InheritedMap
        {
            get
            {
                return inheritedMap;
            }
        }

        public Hashtable ScopeMap
        {
            get
            {
                return scopeMap;
            }

        }

        public Hashtable ScriptMap
        {
            get
            {
                return scriptMap;
            }
        }

        public Hashtable PropagatedVariables
        {
            get
            {
                Hashtable variables = new Hashtable(inheritedMap);
                foreach (DictionaryEntry item in scriptMap)
                {
                    variables[item.Key] = item.Value;
                }
                return variables;
            }
        }

        public Hashtable MergedMap
        {
            get { 
                Hashtable variables = new Hashtable(inheritedMap);
                foreach (DictionaryEntry item in scopeMap)
                {
                    variables[item.Key] = item.Value;
                }
                foreach (DictionaryEntry item in scriptMap)
                {
                    variables[item.Key] = item.Value;
                }          
                return variables;
            }
        }

        override
        public void Add(object key, object value)
        {
            scriptMap.Add(key, value);          
        }

        public override int Count
        {
            get
            {
                return MergedMap.Count;
            }
        }

        override
        public bool ContainsKey(object key)
        {
            return MergedMap.ContainsKey(key);
        }

        override
        public bool ContainsValue(object value)
        {
            return MergedMap.ContainsValue(value);
        }

        override
        public object this[object key]
        {
            get
            {
                return MergedMap[key];
            }
            set
            {
                scriptMap[key] = value;
            }
        }

        override
        public void Remove(object key)
        {
            scriptMap.Remove(key);
            scopeMap.Remove(key);
            inheritedMap.Remove(key);
        }

        override
        public void Clear()
        {
            scriptMap.Clear();
            scopeMap.Clear();
            inheritedMap.Clear();
        }

        override
        public ICollection Keys {
            get
            {
                return MergedMap.Keys;
            }
        }

        override
        public ICollection Values
        {
            get
            {
                return MergedMap.Values;
            }
        }

        override
        public string ToString()
        {
            return MergedMap.ToString();
        }

        override
        public bool Equals(object o)
        {
            if (o is VariablesMap)
            {
                VariablesMap oVarMap = (VariablesMap)o;
                return inheritedMap.Equals(oVarMap.inheritedMap) && scopeMap.Equals(oVarMap.scopeMap) && scriptMap.Equals(oVarMap.scriptMap);
            }
            else if (o is Hashtable)
            {
                return MergedMap.Equals(o);
            }
            else return false;
        }

        override
        public int GetHashCode()
        {
            return MergedMap.GetHashCode();
        }

    }

    public class JVMPSHostUserInterface : PSHostUserInterface
    {

        java.io.PrintStream outStream;
        java.io.PrintStream errStream;

        public JVMPSHostUserInterface(java.io.PrintStream outStream, java.io.PrintStream errStream)
        {
            this.outStream = outStream;
            this.errStream = errStream;
        }

        public override PSHostRawUserInterface RawUI
        {
            get
            {
                return null;
            }
        }

        public override Dictionary<string, PSObject> Prompt(string caption, string message, Collection<FieldDescription> descriptions)
        {
            throw new NotImplementedException("Prompt is not implemented in PowerShell script engine");
        }

        public override int PromptForChoice(string caption, string message, Collection<ChoiceDescription> choices, int defaultChoice)
        {
            outStream.print(message);
            outStream.flush();
            return 1;
        }

        public override PSCredential PromptForCredential(string caption, string message, string userName, string targetName)
        {
            throw new NotImplementedException("PromptForCredential is not implemented in PowerShell script engine");
        }

        public override PSCredential PromptForCredential(string caption, string message, string userName, string targetName, PSCredentialTypes allowedCredentialTypes, PSCredentialUIOptions options)
        {
            throw new NotImplementedException("PromptForCredential is not implemented in PowerShell script engine");
        }

        public override string ReadLine()
        {
            throw new NotImplementedException("ReadLine is not implemented in PowerShell script engine");
        }

        public override SecureString ReadLineAsSecureString()
        {
            throw new NotImplementedException("ReadLineAsSecureString is not implemented in PowerShell script engine");
        }

        public override void Write(string value)
        {
            outStream.print(value);
            outStream.flush();
        }

        public override void Write(ConsoleColor foregroundColor, ConsoleColor backgroundColor, string value)
        {
            outStream.print(value);
            outStream.flush();
        }

        public override void WriteDebugLine(string message)
        {
            outStream.print(message);
            outStream.flush();
        }

        public override void WriteErrorLine(string value)
        {
            errStream.print(value);
            errStream.flush();
        }

        public override void WriteLine(string value)
        {
            outStream.println(value);
            outStream.flush();
        }

        public override void WriteProgress(long sourceId, ProgressRecord record)
        {
            outStream.println(record.ToString());
            outStream.flush();
        }

        public override void WriteVerboseLine(string message)
        {
            outStream.println(message);
            outStream.flush();
        }

        public override void WriteWarningLine(string message)
        {
            errStream.print(message);
            errStream.flush();
        }

        public override void WriteInformation(System.Management.Automation.InformationRecord record)
        {
            outStream.println(record.ToString());
            outStream.flush();
        }

        public void Close()
        {

        }

    }

    public class JVMPSHost : PSHost
    {

        private Guid _hostId = Guid.NewGuid();

        JVMPSHostUserInterface ui;

        public JVMPSHost(JVMPSHostUserInterface ui)
        {
            this.ui = ui;
        }


        public override CultureInfo CurrentCulture
        {
            get
            {
                return Thread.CurrentThread.CurrentCulture;
            }
        }

        public override CultureInfo CurrentUICulture
        {
            get
            {
                return Thread.CurrentThread.CurrentCulture;
            }
        }

        public override Guid InstanceId
        {
            get
            {
                return _hostId;
            }
        }

        public override string Name
        {
            get
            {
                return "JVMPSHost";
            }
        }

        public override PSHostUserInterface UI
        {
            get
            {
                return ui;
            }
        }

        public override Version Version
        {
            get
            {
                return new Version(1, 0);
            }
        }

        public override void EnterNestedPrompt()
        {
            throw new NotImplementedException("EnterNestedPrompt");
        }

        public override void ExitNestedPrompt()
        {
            throw new NotImplementedException("ExitNestedPrompt");
        }

        public override void NotifyBeginApplication()
        {
            return;
        }

        public override void NotifyEndApplication()
        {
            ui.Close();              
        }

        public override void SetShouldExit(int exitCode)
        {
            return;
        }
    }

    public class PsUtils
    {
        public static Collection<PSObject> Run(PowerShell ps)
        {
            return ps.Invoke();
        }



    }
}
