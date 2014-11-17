Utils to generate jni4net code for System.EventHandler
---

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