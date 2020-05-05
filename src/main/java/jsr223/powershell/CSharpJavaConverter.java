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
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.extensions.dataspaces.vfs.adapter.VFSFileObjectAdapter;
import org.ow2.proactive.core.properties.PropertyDecrypter;
import org.ow2.proactive.scheduler.common.task.TaskResult;
import org.ow2.proactive.scheduler.task.utils.VariablesMap;

import system.Decimal;
import system.ValueType;
import system.collections.IDictionary;
import system.collections.IDictionaryEnumerator;
import system.collections.IList;


public class CSharpJavaConverter {

    private static Logger logger = Logger.getLogger(CSharpJavaConverter.class);

    public static String NOT_SUPPORTED_JAVA_OBJECT = "[NOT_SUPPORTED_JAVA_OBJECT]";

    @SuppressWarnings("unchecked")
    static system.Object convertJavaObjectToCSharpObject(PowerShellCachedCaller psCaller, Object bindingValue)
            throws ScriptException {
        if (bindingValue instanceof String) {
            return new system.String((String) bindingValue);
        } else if (bindingValue instanceof Integer) {
            return psCaller.toInt(String.format(java.util.Locale.getDefault(), "%d", bindingValue));
        } else if (bindingValue instanceof Long) {
            return psCaller.toLong(String.format(java.util.Locale.getDefault(), "%d", bindingValue));
        } else if (bindingValue instanceof Double) {
            return psCaller.toDouble(String.format(java.util.Locale.getDefault(), "%f", bindingValue));
        } else if (bindingValue instanceof Byte) {
            return psCaller.toByte(String.format(java.util.Locale.getDefault(), "%d", bindingValue));
        } else if (bindingValue instanceof Character) {
            return psCaller.toChar(bindingValue.toString());
        } else if (bindingValue instanceof Boolean) {
            return psCaller.toBool(bindingValue.toString());
        } else if (bindingValue instanceof TaskResult) {
            Serializable taskResultValue;
            try {
                taskResultValue = ((TaskResult) bindingValue).value();
            } catch (Throwable throwable) {
                taskResultValue = throwable;
            }
            return convertJavaObjectToCSharpObject(psCaller, taskResultValue);
        } else if (bindingValue instanceof VFSFileObjectAdapter) {
            VFSFileObjectAdapter vfsFileObject = (VFSFileObjectAdapter) bindingValue;
            try {
                return new system.String(convertToPath(vfsFileObject));
            } catch (Exception e) {
                return new system.String(vfsFileObject.getRealURI()); // dataspace not supported if it is not filesystem
            }
        } else if (bindingValue instanceof List) {
            system.collections.ArrayList cSharpList = new system.collections.ArrayList();
            for (Object entry : (List) bindingValue) {
                cSharpList.Add(convertJavaObjectToCSharpObject(psCaller, entry));
            }
            return cSharpList;
        } else if (bindingValue instanceof VariablesMap) {
            VariablesMap variablesMapBinding = (VariablesMap) bindingValue;
            return psCaller.createVariablesMap(convertMap(psCaller,
                                                          convertVariableMap(variablesMapBinding.getInheritedMap())),
                                               convertMap(psCaller,
                                                          convertVariableMap(variablesMapBinding.getScopeMap())),
                                               convertMap(psCaller, variablesMapBinding.getScriptMap()));
        } else if (bindingValue instanceof Map) {
            return convertMap(psCaller, (Map) bindingValue);
        } else if (bindingValue instanceof Object[]) {
            system.collections.ArrayList cSharpList = new system.collections.ArrayList();
            for (Object entry : (Object[]) bindingValue) {
                cSharpList.Add(convertJavaObjectToCSharpObject(psCaller, entry));
            }
            return cSharpList;
        } else if (bindingValue instanceof DataContractObject) {
            if (((DataContractObject) bindingValue).getXMLValue() == null) {
                return new system.String(bindingValue.toString());
            } else {
                return psCaller.fromDataContractXMLToObject(((DataContractObject) bindingValue).getXMLValue());
            }
        } else if (bindingValue != null) {
            return new system.String(NOT_SUPPORTED_JAVA_OBJECT + "[" + bindingValue.getClass().getName() + "]" +
                                     bindingValue.toString());
        }
        return null;
    }

    private static Map<String, Serializable> convertVariableMap(Map<String, Serializable> variableMap) {
        Map<String, Serializable> convertedScopeMap = new LinkedHashMap<>();
        for (Map.Entry<String, Serializable> entry : variableMap.entrySet()) {
            Serializable value = entry.getValue();
            if (value != null && value instanceof String && value.toString().startsWith("ENC(")) {
                // if the value is encrypted, use the variables map global get method to decrypt it
                convertedScopeMap.put(entry.getKey(), PropertyDecrypter.decryptData((String) value));
            } else {
                convertedScopeMap.put(entry.getKey(), entry.getValue());
            }
        }
        return convertedScopeMap;
    }

    private static String convertToPath(VFSFileObjectAdapter dsfo) throws URISyntaxException, IOException {
        String path = dsfo.getRealURI();
        URI uri = new URI(path);
        File f = new File(uri);
        return f.getCanonicalPath();
    }

    private static system.collections.Hashtable convertMap(PowerShellCachedCaller psCaller, Map javaMap)
            throws ScriptException {
        system.collections.Hashtable cSharpMap = new system.collections.Hashtable();
        for (Object key : javaMap.keySet()) {
            cSharpMap.Add(new system.String(key.toString()),
                          convertJavaObjectToCSharpObject(psCaller, javaMap.get(key)));
        }
        return cSharpMap;
    }

    static java.lang.Object convertCSharpObjectToJavaObject(PowerShellCachedCaller psCaller,
            system.Object scriptResultObject, boolean conversionErrorsAsDebug) {
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
                try {
                    return Double.parseDouble(scriptResultObject.toString());
                } catch (NumberFormatException nfe) {
                    try {
                        // in case a locale using , as separator is used
                        return NumberFormat.getInstance().parse(scriptResultObject.toString()).doubleValue();
                    } catch (ParseException pe) {
                        return scriptResultObject.toString();
                    }
                }
            } else if (scriptResultValue.GetType().getName().equals("Byte")) {
                return Byte.parseByte(scriptResultValue.toString());
            } else if (scriptResultValue.GetType().getName().equals("Char")) {
                return scriptResultValue.toString().charAt(0);
            } else if (scriptResultValue.GetType().getName().equals("Boolean")) {
                return Boolean.parseBoolean(scriptResultValue.toString());
            } else {
                return convertOtherObject(psCaller, scriptResultObject, conversionErrorsAsDebug);
            }
        } else if (scriptResultObject instanceof system.collections.IList) {
            system.collections.IList asList = ((IList) scriptResultObject);
            List<Object> javaList = new ArrayList<>();
            for (int i = 0; i < asList.getCount(); i++) {
                javaList.add(convertCSharpObjectToJavaObject(psCaller, asList.getItem(i), conversionErrorsAsDebug));
            }
            return javaList;
        } else if (scriptResultObject != null && scriptResultObject.GetType().Equals(psCaller.VariablesMap)) {
            VariablesMap variablesMap = new VariablesMap();
            variablesMap.setInheritedMap((Map<String, Serializable>) convertCSharpObjectToJavaObject(psCaller,
                                                                                                     psCaller.getInheritedMap(scriptResultObject),
                                                                                                     conversionErrorsAsDebug));
            variablesMap.setScopeMap((Map<String, Serializable>) convertCSharpObjectToJavaObject(psCaller,
                                                                                                 psCaller.getScopeMap(scriptResultObject),
                                                                                                 conversionErrorsAsDebug));
            variablesMap.putAll((Map<String, Serializable>) convertCSharpObjectToJavaObject(psCaller,
                                                                                            psCaller.getScriptMap(scriptResultObject),
                                                                                            conversionErrorsAsDebug));
            return variablesMap;

        } else if (scriptResultObject instanceof IDictionary) {
            IDictionary asMap = ((IDictionary) scriptResultObject);
            IDictionaryEnumerator enumerator = asMap.GetEnumerator();
            Map<String, Object> javaMap = new HashMap<String, Object>();
            while (enumerator.MoveNext()) {
                String key = enumerator.getKey().toString();
                Object value = convertCSharpObjectToJavaObject(psCaller,
                                                               enumerator.getValue(),
                                                               conversionErrorsAsDebug);
                javaMap.put(key, value);
            }
            return javaMap;
        } else if (scriptResultObject != null) {
            return convertOtherObject(psCaller, scriptResultObject, conversionErrorsAsDebug);
        } else {
            return null;
        }
    }

    private static Object convertOtherObject(PowerShellCachedCaller psCaller, system.Object scriptResultObject,
            boolean conversionErrorsAsDebug) {
        try {
            String json = psCaller.fromObjectToDataContractXML(scriptResultObject);
            DataContractObject dataContractObject = new DataContractObjectImpl();
            dataContractObject.setXMLValue(json);
            return dataContractObject;
        } catch (Exception e) {
            if (conversionErrorsAsDebug) {
                logger.debug("Error when serializing object " + scriptResultObject.ToString(), e);
            } else {
                logger.error("Error when serializing object " + scriptResultObject.ToString(), e);
            }
        }

        return scriptResultObject.toString();
    }
}
