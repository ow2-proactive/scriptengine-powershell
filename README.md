# scriptengine-powershell

PowerShell script engine for Java implementing JSR 223

## Requirements

* .NET 4
* Windows Management Framework 3.0

## Usage

Simply add the JAR and DLLs to your classpath and follow the [Java Scripting Programmer's guide](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html)

## Build

Run gradlew script, it produces JARs and DDLs in build/install

## How it works

It uses [jni4net](https://github.com/jni4net/jni4net) to call the PowerShell API.

### Bindings

Script bindings are exported in the PowerShell engine before running the script.

PowerShell supported types are: string, char, byte, int, long, bool, double, array, hashtable.

Java List are mapped to PowerShell array.
Java Map are mapped to PowerShell hashtable.

The following types are NOT supported: single decimal datetime xml.

To pass arguments/parameters to a PowerShell ($args), you can set a binding called args.

### Script Result

Results produced by return or Write-Output are retrieved and converted back to Java.

### To update jni4net bindings

* Look at the `utils.proxygen.xml` file. It defines what proxies will be generated.
* Run `generateProxies.cmd`, it will generate
    * Java proxies of .NET classes
    * and C# codebe-hind classes
* Now you need to compile codebe-hind classes into assembly.
    * Start Visual Studio,
    * open `utils.csproj`
    * eventually add newly generated files to the project
    * compile it
