package io.github.techtastic.computation.machine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class BaseMachine {
    public BaseMachine() {
        init();
    }

    abstract void init();

    public abstract String getType();

    public Object[] runScript(File script) throws IOException {
        return runScript(Files.readString(script.toPath()));
    }

    public abstract Object[] runScript(String script);
}
