package com.applitools.eyes.selenium.introspection;

import java.lang.StackWalker.Option;

public class Introspect {
    
    public static String thisMethod() {
        StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
        return walker.walk(frames -> frames
            .skip(1)
            .findFirst()
            .map((f) -> String.format("%s.%s", f.getClassName(), f.getMethodName())))
            .orElse(null);
    }

    public static String thisClass() {
        StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
        return walker.walk(frames -> frames
            .skip(1)
            .findFirst()
            .map((f) -> f.getClassName())
            .orElse(""));
    }

    public static long getThreadId() {
        return Thread.currentThread().getId();
    }

}
