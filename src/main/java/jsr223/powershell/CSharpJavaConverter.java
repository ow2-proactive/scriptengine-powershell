package jsr223.powershell;

import system.Decimal;
import system.ValueType;
import system.collections.IDictionary;
import system.collections.IDictionaryEnumerator;
import system.collections.IList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CSharpJavaConverter {

    @SuppressWarnings("unchecked")
    static system.Object convertJavaObjectToCSharpObject(PowerShellCachedCaller psCaller, Object bindingValue) {
        if (bindingValue instanceof String) {
            return new system.String((String) bindingValue);
        } else if (bindingValue instanceof Integer) {
            return psCaller.toInt(bindingValue.toString());
        } else if (bindingValue instanceof Long) {
            return psCaller.toLong(bindingValue.toString());
        } else if (bindingValue instanceof Double) {
            return psCaller.toDouble(bindingValue.toString());
        } else if (bindingValue instanceof Byte) {
            return psCaller.toByte(bindingValue.toString());
        } else if (bindingValue instanceof Character) {
            return psCaller.toChar(bindingValue.toString());
        } else if (bindingValue instanceof Boolean) {
            return psCaller.toBool(bindingValue.toString());
        } else if (bindingValue instanceof List) {
            system.collections.ArrayList cSharpList = new system.collections.ArrayList();
            for (Object entry : (List) bindingValue) {
                cSharpList.Add(convertJavaObjectToCSharpObject(psCaller, entry));
            }
            return cSharpList;
        } else if (bindingValue instanceof Map) {
            system.collections.Hashtable cSharpMap = new system.collections.Hashtable();
            for (Map.Entry entry : (Set<Map.Entry>) ((Map) bindingValue).entrySet()) {
                cSharpMap.Add(new system.String(entry.getKey().toString()), convertJavaObjectToCSharpObject(psCaller, entry.getValue()));
            }
            return cSharpMap;
        }
        return null;
    }

    static java.lang.Object convertCSharpObjectToJavaObject(system.Object scriptResultObject) {
        if (scriptResultObject instanceof Decimal) {
            Decimal decimal = (Decimal) scriptResultObject;
            return Decimal.ToInt32(decimal);
        } else if (scriptResultObject instanceof system.String) {
            system.String asString = (system.String) scriptResultObject;
            return asString.ToString();
        } else if (scriptResultObject instanceof ValueType) {
            ValueType scriptResultValue = (ValueType) scriptResultObject;
            if (scriptResultValue.GetType().getName().equals("Int32")) {
                return Integer.parseInt(scriptResultValue.ToString());
            } else if (scriptResultValue.GetType().getName().equals("Int64")) {
                return Long.parseLong(scriptResultObject.toString());
            } else if (scriptResultValue.GetType().getName().equals("Double")) {
                return Double.parseDouble(scriptResultObject.toString());
            } else if (scriptResultValue.GetType().getName().equals("Byte")) {
                return Byte.parseByte(scriptResultValue.toString());
            } else if (scriptResultValue.GetType().getName().equals("Char")) {
                return scriptResultValue.toString().charAt(0);
            } else if (scriptResultValue.GetType().getName().equals("Boolean")) {
                return Boolean.parseBoolean(scriptResultValue.toString());
            } else {
                return scriptResultValue.toString();
            }
        } else if (scriptResultObject instanceof system.collections.IList) {
            system.collections.IList asList = ((IList) scriptResultObject);
            List<Object> javaList = new ArrayList<>();
            for (int i = 0; i < asList.getCount(); i++) {
                javaList.add(convertCSharpObjectToJavaObject(asList.getItem(i)));
            }
            return javaList;
        } else if (scriptResultObject instanceof IDictionary) {
            IDictionary asMap = ((IDictionary) scriptResultObject);
            IDictionaryEnumerator enumerator = asMap.GetEnumerator();
            Map<String, Object> javaMap = new HashMap<>();
            while (enumerator.MoveNext()) {
                String key = enumerator.getKey().toString();
                Object value = convertCSharpObjectToJavaObject(enumerator.getValue());
                javaMap.put(key, value);
            }
            return javaMap;
        } else {
            return scriptResultObject.toString();
        }
    }
}
