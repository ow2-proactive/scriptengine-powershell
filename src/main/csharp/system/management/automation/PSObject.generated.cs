//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//     Runtime Version:4.0.30319.42000
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace System.Management.Automation {
    
    
    #region Component Designer generated code 
    public partial class PSObject_ {
        
        public static global::java.lang.Class _class {
            get {
                return global::System.Management.Automation.@__PSObject.staticClass;
            }
        }
    }
    #endregion
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaProxyAttribute(typeof(global::System.Management.Automation.PSObject), typeof(global::System.Management.Automation.PSObject_))]
    [global::net.sf.jni4net.attributes.ClrWrapperAttribute(typeof(global::System.Management.Automation.PSObject), typeof(global::System.Management.Automation.PSObject_))]
    internal sealed partial class @__PSObject : global::java.lang.Object {
        
        internal new static global::java.lang.Class staticClass;
        
        private @__PSObject(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::System.Management.Automation.@__PSObject.staticClass = @__class;
        }
        
        private static global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> @__Init(global::net.sf.jni4net.jni.JNIEnv @__env, global::java.lang.Class @__class) {
            global::System.Type @__type = typeof(__PSObject);
            global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> methods = new global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod>();
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "ToString", "ToString0", "(Ljava/lang/String;Lsystem/IFormatProvider;)Ljava/lang/String;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "CompareTo", "CompareTo1", "(Lsystem/Object;)I"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "GetObjectData", "GetObjectData2", "(Lsystem/runtime/serialization/SerializationInfo;Lsystem/runtime/serialization/St" +
                        "reamingContext;)V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "GetMetaObject", "GetMetaObject3", "(Lsystem/Object;)Lsystem/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getMembers", "Members4", "()Lsystem/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getProperties", "Properties5", "()Lsystem/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getMethods", "Methods6", "()Lsystem/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getImmediateBaseObject", "ImmediateBaseObject7", "()Lsystem/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getBaseObject", "BaseObject8", "()Lsystem/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "getTypeNames", "TypeNames9", "()Lsystem/Object;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "AsPSObject", "AsPSObject10", "(Lsystem/Object;)Lsystem/management/automation/PSObject;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "Copy", "Copy11", "()Lsystem/management/automation/PSObject;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "__ctorPSObject0", "__ctorPSObject0", "(Lnet/sf/jni4net/inj/IClrProxy;)V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "__ctorPSObject1", "__ctorPSObject1", "(Lnet/sf/jni4net/inj/IClrProxy;Lsystem/Object;)V"));
            return methods;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle ToString0(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle format, global::net.sf.jni4net.utils.JniLocalHandle formatProvider) {
            // (Ljava/lang/String;Lsystem/IFormatProvider;)Ljava/lang/String;
            // (LSystem/String;LSystem/IFormatProvider;)LSystem/String;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2JString(@__env, ((global::System.IFormattable)(@__real)).ToString(global::net.sf.jni4net.utils.Convertor.StrongJ2CString(@__env, format), global::net.sf.jni4net.utils.Convertor.FullJ2C<global::System.IFormatProvider>(@__env, formatProvider)));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static int CompareTo1(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle obj) {
            // (Lsystem/Object;)I
            // (LSystem/Object;)I
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            int @__return = default(int);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = ((int)(((global::System.IComparable)(@__real)).CompareTo(global::net.sf.jni4net.utils.Convertor.FullJ2C<object>(@__env, obj))));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static void GetObjectData2(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle info, global::net.sf.jni4net.utils.JniLocalHandle context) {
            // (Lsystem/runtime/serialization/SerializationInfo;Lsystem/runtime/serialization/StreamingContext;)V
            // (LSystem/Runtime/Serialization/SerializationInfo;LSystem/Runtime/Serialization/StreamingContext;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            ((global::System.Runtime.Serialization.ISerializable)(@__real)).GetObjectData(global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Runtime.Serialization.SerializationInfo>(@__env, info), global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Runtime.Serialization.StreamingContext>(@__env, context));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static global::net.sf.jni4net.utils.JniHandle GetMetaObject3(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle parameter) {
            // (Lsystem/Object;)Lsystem/Object;
            // (LSystem/Linq/Expressions/Expression;)LSystem/Dynamic/DynamicMetaObject;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2Jp<global::System.Dynamic.DynamicMetaObject>(@__env, ((global::System.Dynamic.IDynamicMetaObjectProvider)(@__real)).GetMetaObject(global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Linq.Expressions.Expression>(@__env, parameter)));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle Members4(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Lsystem/Object;
            // ()[[LSystem/Management/Automation/PSMemberInfoCollection`1;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2Jp<global::System.Management.Automation.PSMemberInfoCollection<System.Management.Automation.PSMemberInfo>>(@__env, @__real.Members);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle Properties5(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Lsystem/Object;
            // ()[[LSystem/Management/Automation/PSMemberInfoCollection`1;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2Jp<global::System.Management.Automation.PSMemberInfoCollection<System.Management.Automation.PSPropertyInfo>>(@__env, @__real.Properties);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle Methods6(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Lsystem/Object;
            // ()[[LSystem/Management/Automation/PSMemberInfoCollection`1;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2Jp<global::System.Management.Automation.PSMemberInfoCollection<System.Management.Automation.PSMethodInfo>>(@__env, @__real.Methods);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle ImmediateBaseObject7(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Lsystem/Object;
            // ()LSystem/Object;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.FullC2J<object>(@__env, @__real.ImmediateBaseObject);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle BaseObject8(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Lsystem/Object;
            // ()LSystem/Object;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.FullC2J<object>(@__env, @__real.BaseObject);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle TypeNames9(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Lsystem/Object;
            // ()[[LSystem/Collections/ObjectModel/Collection`1;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2Jp<global::System.Collections.ObjectModel.Collection<string>>(@__env, @__real.TypeNames);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle AsPSObject10(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, global::net.sf.jni4net.utils.JniLocalHandle obj) {
            // (Lsystem/Object;)Lsystem/management/automation/PSObject;
            // (LSystem/Object;)LSystem/Management/Automation/PSObject;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2Jp<global::System.Management.Automation.PSObject>(@__env, global::System.Management.Automation.PSObject.AsPSObject(global::net.sf.jni4net.utils.Convertor.FullJ2C<object>(@__env, obj)));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static global::net.sf.jni4net.utils.JniHandle Copy11(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Lsystem/management/automation/PSObject;
            // ()LSystem/Management/Automation/PSObject;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::System.Management.Automation.PSObject @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::System.Management.Automation.PSObject>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2Jp<global::System.Management.Automation.PSObject>(@__env, @__real.Copy());
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static void @__ctorPSObject0(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()V
            // ()V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::System.Management.Automation.PSObject @__real = new global::System.Management.Automation.PSObject();
            global::net.sf.jni4net.utils.Convertor.InitProxy(@__env, @__obj, @__real);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void @__ctorPSObject1(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle obj) {
            // (Lsystem/Object;)V
            // (LSystem/Object;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::System.Management.Automation.PSObject @__real = new global::System.Management.Automation.PSObject(global::net.sf.jni4net.utils.Convertor.FullJ2C<object>(@__env, obj));
            global::net.sf.jni4net.utils.Convertor.InitProxy(@__env, @__obj, @__real);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        new internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::System.Management.Automation.@__PSObject(@__env);
            }
        }
    }
    #endregion
}
