package com.linwiyuan.reflection;

import com.linweiyuan.commons.util.ExceptionUtil;
import com.linweiyuan.commons.util.JsonUtil;
import com.linwiyuan.reflection.custom.Loader;
import com.linwiyuan.reflection.model.User;
import javassist.*;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        try {
            User user = new User();
            user.setUsername("张三");
            System.out.println(JsonUtil.toJson(user));
            Stream.of(user.getClass().getDeclaredFields()).forEach(System.out::println);

            String dir = "target/classes";
            String className = "com.linwiyuan.reflection.model.User";

            CtClass c = ClassPool.getDefault().get(className);

            Map<String, Object> map = new HashMap<>();
            map.put("password", "qwerty");
            map.put("flag", true);
            map.put("list", Collections.emptyList());

            map.forEach((k, v) -> {
                try {
                    CtField field = null;
                    if (v instanceof Number) {
                        field = CtField.make("private int " + k + " = " + v + ";", c);
                    } else if (v instanceof String) {
                        field = CtField.make("private String " + k + " = \"" + v + "\";", c);
                    } else if (v instanceof Boolean) {
                        field = CtField.make("public boolean " + k + " = " + v + ";", c);
                    } else {
                        System.out.println("Not supported -> (" + k + ":" + v + ")");
                    }
                    if (field != null) {
                        c.addField(field);
                    }
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            });

            c.addMethod(CtNewMethod.make("public void test(){ }", c));
            c.addMethod(CtNewMethod.make("public int getAge() { return 18;}", c));

            c.writeFile(dir);
            c.detach();

            System.out.println("---");

            Class newClass = new Loader().reload(dir, className);
            Stream.of(newClass.getDeclaredFields()).forEach(System.out::println);

            Field field = newClass.getDeclaredField("username");
            field.setAccessible(true);
            Object newUser = newClass.newInstance();
            field.set(newUser, "李四");
            System.out.println(JsonUtil.toJson(newUser));
        } catch (Exception e) {
            ExceptionUtil.print(e);
        }
    }
}
