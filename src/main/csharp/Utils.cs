using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Management.Automation;
namespace utils
{
    class HandlerUtils
    {
        public static void AddErrorHandler(PowerShell ps, System.EventHandler errorEvt)
        {
            ps.Streams.Error.DataAdded += new EventHandler<DataAddedEventArgs>(errorEvt);
        }

        public static void AddDebugHandler(PowerShell ps, System.EventHandler errorEvt)
        {
            ps.Streams.Debug.DataAdded += new EventHandler<DataAddedEventArgs>(errorEvt);
        }

        public static void AddVerboseHandler(PowerShell ps, System.EventHandler errorEvt)
        {
            ps.Streams.Verbose.DataAdded += new EventHandler<DataAddedEventArgs>(errorEvt);
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

    }

    class PsUtils
    {
        public static Collection<PSObject> Run(PowerShell ps)
        {
            return ps.Invoke();
        }



    }
}
