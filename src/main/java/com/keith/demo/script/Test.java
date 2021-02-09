package com.keith.demo.script;

import javax.script.*;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-08
 */
public class Test {

    public static void main(String[] args) throws ScriptException, NoSuchMethodException, FileNotFoundException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        FileReader fileReader = new FileReader("/Users/gemi/Desktop/script.js");
        engine.eval(fileReader);

        Invocable invocable = (Invocable) engine;

        Object result = invocable.invokeFunction("fun1", "Peter Parker");
        System.out.println(result);
        System.out.println(result.getClass());
    }
}
