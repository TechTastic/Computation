package io.github.techtastic.computation.machine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public abstract class BaseMachine {
    public BaseMachine() {
        init();
    }

    abstract void init();

    public abstract String getType();

    public Object[] runScript(File script, Consumer<String> printCallback, Consumer<String> errorCallback) throws IOException {
        return runScript(Files.readString(script.toPath()), printCallback, errorCallback);
    }

    public abstract Object[] runScript(String script, Consumer<String> printCallback, Consumer<String> errorCallback);
}
