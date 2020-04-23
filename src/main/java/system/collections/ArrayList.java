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
package system.collections;

// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/
//
//      Changes to this file may cause incorrect behavior and will be lost if
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

@net.sf.jni4net.attributes.ClrType
public class ArrayList extends system.Object implements system.collections.IList, system.collections.ICollection,
        system.collections.IEnumerable, system.ICloneable {

    //<generated-proxy>
    private static system.Type staticType;

    protected ArrayList(net.sf.jni4net.inj.INJEnv __env, long __handle) {
        super(__env, __handle);
    }

    @net.sf.jni4net.attributes.ClrConstructor("()V")
    public ArrayList() {
        super(((net.sf.jni4net.inj.INJEnv) (null)), 0);
        system.collections.ArrayList.__ctorArrayList0(this);
    }

    @net.sf.jni4net.attributes.ClrConstructor("(I)V")
    public ArrayList(int capacity) {
        super(((net.sf.jni4net.inj.INJEnv) (null)), 0);
        system.collections.ArrayList.__ctorArrayList1(this, capacity);
    }

    @net.sf.jni4net.attributes.ClrConstructor("(LSystem/Collections/ICollection;)V")
    public ArrayList(system.collections.ICollection c) {
        super(((net.sf.jni4net.inj.INJEnv) (null)), 0);
        system.collections.ArrayList.__ctorArrayList2(this, c);
    }

    @net.sf.jni4net.attributes.ClrMethod("()V")
    private native static void __ctorArrayList0(net.sf.jni4net.inj.IClrProxy thiz);

    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    private native static void __ctorArrayList1(net.sf.jni4net.inj.IClrProxy thiz, int capacity);

    @net.sf.jni4net.attributes.ClrMethod("(Lsystem/collections/ICollection;)V")
    private native static void __ctorArrayList2(net.sf.jni4net.inj.IClrProxy thiz, system.collections.ICollection c);

    @net.sf.jni4net.attributes.ClrMethod("()LSystem/Collections/IEnumerator;")
    public native system.collections.IEnumerator GetEnumerator();

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Array;I)V")
    public native void CopyTo(system.Array array, int index);

    @net.sf.jni4net.attributes.ClrMethod("()I")
    public native int getCount();

    @net.sf.jni4net.attributes.ClrMethod("()LSystem/Object;")
    public native system.Object getSyncRoot();

    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isSynchronized();

    @net.sf.jni4net.attributes.ClrMethod("(I)LSystem/Object;")
    public native system.Object getItem(int index);

    @net.sf.jni4net.attributes.ClrMethod("(ILSystem/Object;)V")
    public native void setItem(int index, system.Object value);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;)I")
    public native int Add(system.Object value);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;)Z")
    public native boolean Contains(system.Object value);

    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void Clear();

    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isReadOnly();

    @net.sf.jni4net.attributes.ClrMethod("()Z")
    public native boolean isFixedSize();

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;)I")
    public native int IndexOf(system.Object value);

    @net.sf.jni4net.attributes.ClrMethod("(ILSystem/Object;)V")
    public native void Insert(int index, system.Object value);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;)V")
    public native void Remove(system.Object value);

    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    public native void RemoveAt(int index);

    @net.sf.jni4net.attributes.ClrMethod("()LSystem/Object;")
    public native system.Object Clone();

    @net.sf.jni4net.attributes.ClrMethod("()I")
    public native int getCapacity();

    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    public native void setCapacity(int value);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Collections/IList;)LSystem/Collections/ArrayList;")
    public native static system.collections.ArrayList Adapter(system.collections.IList list);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Collections/ICollection;)V")
    public native void AddRange(system.collections.ICollection c);

    @net.sf.jni4net.attributes.ClrMethod("(IILSystem/Object;LSystem/Collections/IComparer;)I")
    public native int BinarySearch(int index, int count, system.Object value, system.Object comparer);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;)I")
    public native int BinarySearch(system.Object value);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;LSystem/Collections/IComparer;)I")
    public native int BinarySearch(system.Object value, system.Object comparer);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Array;)V")
    public native void CopyTo(system.Array array);

    @net.sf.jni4net.attributes.ClrMethod("(ILSystem/Array;II)V")
    public native void CopyTo(int index, system.Array array, int arrayIndex, int count);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Collections/IList;)LSystem/Collections/IList;")
    public native static system.collections.IList FixedSize(system.collections.IList list);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Collections/ArrayList;)LSystem/Collections/ArrayList;")
    public native static system.collections.ArrayList FixedSize(system.collections.ArrayList list);

    @net.sf.jni4net.attributes.ClrMethod("(II)LSystem/Collections/IEnumerator;")
    public native system.collections.IEnumerator GetEnumerator(int index, int count);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;I)I")
    public native int IndexOf(system.Object value, int startIndex);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;II)I")
    public native int IndexOf(system.Object value, int startIndex, int count);

    @net.sf.jni4net.attributes.ClrMethod("(ILSystem/Collections/ICollection;)V")
    public native void InsertRange(int index, system.collections.ICollection c);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;)I")
    public native int LastIndexOf(system.Object value);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;I)I")
    public native int LastIndexOf(system.Object value, int startIndex);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;II)I")
    public native int LastIndexOf(system.Object value, int startIndex, int count);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Collections/IList;)LSystem/Collections/IList;")
    public native static system.collections.IList ReadOnly(system.collections.IList list);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Collections/ArrayList;)LSystem/Collections/ArrayList;")
    public native static system.collections.ArrayList ReadOnly(system.collections.ArrayList list);

    @net.sf.jni4net.attributes.ClrMethod("(II)V")
    public native void RemoveRange(int index, int count);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Object;I)LSystem/Collections/ArrayList;")
    public native static system.collections.ArrayList Repeat(system.Object value, int count);

    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void Reverse();

    @net.sf.jni4net.attributes.ClrMethod("(II)V")
    public native void Reverse(int index, int count);

    @net.sf.jni4net.attributes.ClrMethod("(ILSystem/Collections/ICollection;)V")
    public native void SetRange(int index, system.collections.ICollection c);

    @net.sf.jni4net.attributes.ClrMethod("(II)LSystem/Collections/ArrayList;")
    public native system.collections.ArrayList GetRange(int index, int count);

    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void Sort();

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Collections/IComparer;)V")
    public native void Sort(system.Object comparer);

    @net.sf.jni4net.attributes.ClrMethod("(IILSystem/Collections/IComparer;)V")
    public native void Sort(int index, int count, system.Object comparer);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Collections/IList;)LSystem/Collections/IList;")
    public native static system.collections.IList Synchronized(system.collections.IList list);

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Collections/ArrayList;)LSystem/Collections/ArrayList;")
    public native static system.collections.ArrayList Synchronized(system.collections.ArrayList list);

    @net.sf.jni4net.attributes.ClrMethod("()[LSystem/Object;")
    public native system.Object[] ToArray();

    @net.sf.jni4net.attributes.ClrMethod("(LSystem/Type;)LSystem/Array;")
    public native system.Array ToArray(system.Type type);

    @net.sf.jni4net.attributes.ClrMethod("()V")
    public native void TrimToSize();

    public static system.Type typeof() {
        return system.collections.ArrayList.staticType;
    }

    private static void InitJNI(net.sf.jni4net.inj.INJEnv env, system.Type staticType) {
        system.collections.ArrayList.staticType = staticType;
    }
    //</generated-proxy>
}
