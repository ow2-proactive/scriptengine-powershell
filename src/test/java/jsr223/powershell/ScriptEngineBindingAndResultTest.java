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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.crypto.Data;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ow2.proactive.scheduler.common.SchedulerConstants;
import org.ow2.proactive.scheduler.common.job.TaskFlowJob;
import org.ow2.proactive.scheduler.task.utils.VariablesMap;

import com.google.common.collect.Lists;


// TODO add some null tests
public class ScriptEngineBindingAndResultTest {

    private static PowerShellScriptEngine scriptEngine;

    @BeforeClass
    public static void setup() {
        assumeTrue(System.getProperty("os.name").contains("Windows"));
        scriptEngine = new PowerShellScriptEngine();
    }

    @Test
    public void string() throws Exception {
        scriptEngine.put("var", "hello");
        assertEquals("hello", scriptEngine.eval("return $var"));
    }

    @Test
    public void integer() throws Exception {
        scriptEngine.put("var", 42);
        assertEquals(42, scriptEngine.eval("return $var"));
    }

    @Test
    public void double_variable() throws Exception {
        scriptEngine.put("var", 42.0);
        assertEquals(42.0, scriptEngine.eval("return $var"));
    }

    @Test
    public void long_variable() throws Exception {
        scriptEngine.put("var", 42L);
        assertEquals(42L, scriptEngine.eval("return $var"));
    }

    @Test
    public void char_variable() throws Exception {
        scriptEngine.put("var", 'a');
        assertEquals('a', scriptEngine.eval("return $var"));
    }

    @Test
    public void byte_variable() throws Exception {
        scriptEngine.put("var", (byte) 'a');
        assertEquals((byte) 'a', scriptEngine.eval("return $var"));
    }

    @Test
    public void bool() throws Exception {
        scriptEngine.put("trueVar", true);
        scriptEngine.put("falseVar", false);
        assertEquals(true, scriptEngine.eval("return $trueVar"));
        assertEquals(false, scriptEngine.eval("return $falseVar"));
    }

    @Test
    public void list() throws Exception {
        scriptEngine.put("aList", asList(1, 2, 3));
        assertEquals(Lists.newArrayList(1, 2, 3), scriptEngine.eval("return $aList"));

        scriptEngine.put("aListMixedTypes", asList(1, "abc", 3.2));
        assertEquals(Lists.newArrayList(1, "abc", 3.2), scriptEngine.eval("return $aListMixedTypes"));

        // http://stackoverflow.com/questions/18476634/powershell-doesnt-return-an-empty-array-as-an-array
        scriptEngine.put("emptyList", emptyList());
        assertEquals(emptyList(), scriptEngine.eval("return ,$emptyList"));
    }

    @Test
    public void array() throws Exception {
        scriptEngine.put("anArray", new Object[] { 1, "abc", 4.2 });
        assertEquals(Lists.newArrayList(1, "abc", 4.2), scriptEngine.eval("return $anArray"));
        assertEquals(Lists.newArrayList(1, "abc", 4.2), scriptEngine.eval("Write-Output $anArray"));
        assertEquals(1, scriptEngine.eval("Write-Output $anArray[0]"));
        assertEquals("abc", scriptEngine.eval("return $anArray[1]"));
    }

    @Test
    public void script_args() throws Exception {
        scriptEngine.put("args", new Object[] { "abc" });
        assertEquals(asList("abc"), scriptEngine.eval("return ,$args"));
        assertEquals("abc", scriptEngine.eval("return $args")); // single element array is converted to its element automatically
        assertEquals("abc", scriptEngine.eval("return $args[0]"));
    }

    @Test
    public void nested_list() throws Exception {
        scriptEngine.put("nestedList", asList(asList(1, 2, 3), 2, 3));
        assertEquals(asList(asList(1, 2, 3), 2, 3), scriptEngine.eval("return $nestedList"));
    }

    @Test
    public void null_result() throws Exception {
        scriptEngine.put("nullVar", null);
        assertEquals(null, scriptEngine.eval("return $nullVar"));
        assertEquals(null, scriptEngine.eval("return $null"));
    }

    @Test
    public void map() throws Exception {
        scriptEngine.put("emptyMap", emptyMap());
        assertEquals(emptyMap(), scriptEngine.eval("return $emptyMap"));

        scriptEngine.put("singletonMap", singletonMap("key", "value"));
        assertEquals(singletonMap("key", "value"), scriptEngine.eval("return $singletonMap"));

        Map<String, Object> aMap = new HashMap<String, Object>();
        aMap.put("key", "value");
        aMap.put("key2", 42);
        scriptEngine.put("aMap", aMap);
        assertEquals(aMap, scriptEngine.eval("return $aMap"));
    }

    @Test
    public void nested_map() throws Exception {
        scriptEngine.put("nestedMap", singletonMap("key", singletonMap("key", "value")));
        assertEquals(singletonMap("key", singletonMap("key", "value")), scriptEngine.eval("return $nestedMap"));
    }

    @Test
    public void other_object() throws Exception {
        // tests that a PowerShell native object other than standard types can be serialized using DataContract, and reused
        Object date = scriptEngine.eval("Get-Date");
        Assert.assertTrue(date instanceof DataContractObject);
        scriptEngine.put("aDate", date);
        Object aDay = scriptEngine.eval("return $aDate.Day");
        Assert.assertTrue(aDay instanceof Integer);
        Assert.assertEquals(Calendar.getInstance().get(Calendar.DAY_OF_MONTH), aDay);
    }

    @Test
    public void variables_map_bindings_and_conversion_error() throws Exception {
        // tests that a java object which is not convertible to PowerShell is marked so, 
        // and that the original java binding is preserved after the script execution
        VariablesMap map = new VariablesMap();
        map.getScopeMap().put("scopeVar", "value");
        // we use a TaskFlowJob as a non-convertible object
        TaskFlowJob job = new TaskFlowJob();
        map.getInheritedMap().put("unconvertible", job);
        scriptEngine.put(SchedulerConstants.VARIABLES_BINDING_NAME, map);

        // this tests that we can use scope variable from the map
        Object scopeVariable = scriptEngine.eval("$" + SchedulerConstants.VARIABLES_BINDING_NAME +
                                                 ".Get_Item('scopeVar')");
        Assert.assertTrue(scopeVariable instanceof String);
        Assert.assertEquals("value", scopeVariable);

        // we test that the scope variables are not propagated
        Assert.assertNull(map.getPropagatedVariables().get("scopeVar"));

        // we test that the script variables are propagated
        scriptEngine.eval("$" + SchedulerConstants.VARIABLES_BINDING_NAME + ".Set_Item('newVar', 'newValue')");

        Assert.assertNotNull(map.getPropagatedVariables().get("newVar"));

        Assert.assertEquals("newValue", map.getPropagatedVariables().get("newVar"));

        // we test that unconvertible variables are marked so, and that the initial value is not modified
        Object unconvertibleVariable = scriptEngine.eval("$" + SchedulerConstants.VARIABLES_BINDING_NAME +
                                                         ".Get_Item('unconvertible')");
        Assert.assertTrue(unconvertibleVariable instanceof String);
        Assert.assertTrue(((String) unconvertibleVariable).startsWith(CSharpJavaConverter.NOT_SUPPORTED_JAVA_OBJECT));
        Assert.assertEquals(job, map.getPropagatedVariables().get("unconvertible"));
        System.out.println(map);
    }

}
