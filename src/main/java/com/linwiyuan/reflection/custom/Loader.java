package com.linwiyuan.reflection.custom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Loader extends ClassLoader {
    public Class<?> reload(String dir, String className) throws IOException {
        byte[] bytes = Files.readAllBytes(new File(dir + "/" + className.replace(".", "/") + ".class").toPath());
        return defineClass(className, bytes, 0, bytes.length);
    }
}
