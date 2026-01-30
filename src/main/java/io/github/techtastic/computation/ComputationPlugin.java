package io.github.techtastic.computation;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import io.github.techtastic.computation.interaction.ComputerOpenInteraction;
import io.github.techtastic.computation.machine.LuaMachine;

public class ComputationPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static ComputationPlugin instance;

    public static final LuaMachine LUA = new LuaMachine();

    public ComputationPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        this.getCodecRegistry(Interaction.CODEC).register("computer_open_interaction", ComputerOpenInteraction.class, ComputerOpenInteraction.CODEC);
    }

    public static ComputationPlugin getInstance() {
        return instance;
    }

    public static HytaleLogger getComputationLogger() {
        return LOGGER;
    }
}
