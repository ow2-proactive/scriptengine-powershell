package jsr223.powershell;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public class ScriptEngineResultTest {

    private static PowerShellScriptEngineNoFork scriptEngine;

    @BeforeClass
    public static void setup() {
        assumeTrue(System.getProperty("os.name").contains("Windows"));
        scriptEngine = new PowerShellScriptEngineNoFork();
    }

    @Test
    public void string() throws Exception {
        assertEquals("hello", scriptEngine.eval("return 'hello'"));
    }

    @Test
    public void integer() throws Exception {
        assertEquals(42, scriptEngine.eval("return 42"));
    }

    @Test
    public void double_result() throws Exception {
        assertEquals(42.0, scriptEngine.eval("return 42.0"));
    }

    @Test
    public void long_result() throws Exception {
        assertEquals(42L, scriptEngine.eval("return [long]42"));
    }

    @Test
    public void char_result() throws Exception {
        assertEquals('a', scriptEngine.eval("return [char]'a'"));
    }

    @Test
    public void byte_result() throws Exception {
        assertEquals((byte) 'a', scriptEngine.eval("return [byte]97"));
    }

    @Test
    public void bool() throws Exception {
        assertEquals(false, scriptEngine.eval("return $false"));
        assertEquals(true, scriptEngine.eval("return $true"));
    }

    @Test
    public void array() throws Exception {
        assertEquals(Arrays.asList(1, 2, 3), scriptEngine.eval("return @( 1, 2,3)"));
        assertEquals(Arrays.asList(1, "abc", 3.2), scriptEngine.eval("return @( 1, \"abc\" , 3.2)"));
        assertEquals(Collections.emptyList(), scriptEngine.eval("return @()"));
    }

    @Test
    public void nested_array() throws Exception {
        assertEquals(Arrays.asList(Arrays.asList(1, 2, 3), 2, 3), scriptEngine.eval("return @( @(1,2,3), 2,3)"));
    }

    @Test
    public void map() throws Exception {
        assertEquals(Collections.emptyMap(), scriptEngine.eval("return @{}"));
        assertEquals(Collections.singletonMap("key", "value"), scriptEngine.eval("return @{ key = \"value\"}"));
        Map<String, Object> aMap = new HashMap<>();
        aMap.put("key", "value");
        aMap.put("key2", 42);
        assertEquals(aMap, scriptEngine.eval("return @{ key = \"value\"; key2 = 42}"));
    }

    @Test
    public void nested_map() throws Exception {
        assertEquals(Collections.singletonMap("key", Collections.singletonMap("key", "value")),
                scriptEngine.eval("return @{ key = @{ key = \"value\"}}"));
    }

    // TODO single decimal datetime xml
}
