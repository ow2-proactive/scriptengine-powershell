using System;
using System.Collections.Generic;
using System.Linq;
using System.Management.Automation;
using System.Management.Automation.Runspaces;
using System.Text;
using System.Threading.Tasks;

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

    }
}
