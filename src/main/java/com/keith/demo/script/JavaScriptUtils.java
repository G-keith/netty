package com.keith.demo.script;

import com.keith.demo.entity.SensorInfo;

import javax.annotation.PostConstruct;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-08
 */
public class JavaScriptUtils {

    public static final ConcurrentMap<String, ScriptEngine> scriptEngineMap = new ConcurrentHashMap<>();

    private static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByName("nashorn");

    /**
     * 添加/更新ScriptEngine
     * @param key
     * @param path
     */
    public static void put(String key, String path) {
        try {
            ENGINE.eval(new FileReader(path));
            scriptEngineMap.put(key, ENGINE);
        } catch (ScriptException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除传感器对应的脚本
     * @param key 传感器编号
     */
    public static void delete(String key){
        scriptEngineMap.remove(key);
    }

    /**
     * 执行脚本 todo 改进，多线程，并发
     *
     * @param key
     * @param functionName
     * @param args
     * @return
     */
    public static Object execute(String key, String path, String functionName, Object... args) {
        if (!scriptEngineMap.containsKey(key)) {
            JavaScriptUtils.put(key, path);
        }
        Invocable invocable = (Invocable) scriptEngineMap.get(key);
        try {
            return invocable.invokeFunction(functionName, args);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            return "发生异常";
        }
    }
}
