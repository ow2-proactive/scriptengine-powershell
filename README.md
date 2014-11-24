# powershell-jsr223

PowerShell script engine for Java implementing JSR 223

## Requirements

* .NET 4
* Windows Management Framework 3.0

## Usage

Simply add the JAR and DLLs to your classpath and follow the [Java Scripting Programmer's guide](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html)

## Build

Run gradlew script, it produces a JAR file and DDLs in build/install

## How it works

It uses [jni4net](https://github.com/jni4net/jni4net) to call the PowerShell API.

### Bindings

Script bindings are exported in the PowerShell engine before running the script.

PowerShell supported types are: string, char, byte, int, long, bool, double, array, hashtable.

Java List are mapped to PowerShell array.
Java Map are mapped to PowerShell hashtable.

### Script Result

Results produced by return or Write-Output are retrieved and converted back to Java.


### To update jni4net bindings // TODO TO BE COMPLETED

This is most complex sample, because you need to generate proxies and compile both C# and Java side.

2) Look at the `utils.proxygen.xml` file. It defines what proxies will be generated.

3) Run `generateProxies.cmd`, it will generate 
- Java proxies of .NET classes 
- and C# codebe-hind classes

4) Now you need to compile codebe-hind classes into assembly. 
- Start Visual Studio, 
- open `utils.csproj`
- compile it
- verify that files exist `target\utils.dll`

5) copy all utils.dll to ..\lib\

6) java sources will be generate into ../src/
- start `samples\winforms\build.cmd`
- or run ant
- or build it in your Java IDE

7) run the test
- start `samples\winforms\run.cmd`
- or debug it step-by-step in Java IDE