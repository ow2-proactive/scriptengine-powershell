package jsr223.powershell;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

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
        assertEquals(asList(1, 2, 3), scriptEngine.eval("return $aList"));

        scriptEngine.put("aListMixedTypes", asList(1, "abc", 3.2));
        assertEquals(asList(1, "abc", 3.2), scriptEngine.eval("return $aListMixedTypes"));

        // http://stackoverflow.com/questions/18476634/powershell-doesnt-return-an-empty-array-as-an-array
        scriptEngine.put("emptyList", emptyList());
        assertEquals(emptyList(), scriptEngine.eval("return ,$emptyList"));
    }

    @Test
    public void array() throws Exception {
        scriptEngine.put("anArray", new Object[]{1, "abc", 4.2});
        assertEquals(asList(1, "abc", 4.2), scriptEngine.eval("return $anArray"));
        assertEquals(asList(1, "abc", 4.2), scriptEngine.eval("Write-Output $anArray"));
        assertEquals(1, scriptEngine.eval("Write-Output $anArray[0]"));
        assertEquals("abc", scriptEngine.eval("return $anArray[1]"));
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

        Map<String, Object> aMap = new HashMap<>();
        aMap.put("key", "value");
        aMap.put("key2", 42);
        scriptEngine.put("aMap", aMap);
        assertEquals(aMap, scriptEngine.eval("return $aMap"));
    }

    @Test
    public void nested_map() throws Exception {
        scriptEngine.put("nestedMap", singletonMap("key", singletonMap("key", "value")));
        assertEquals(singletonMap("key", singletonMap("key", "value")),
                scriptEngine.eval("return $nestedMap"));
    }

    // TODO single decimal datetime xml
}
