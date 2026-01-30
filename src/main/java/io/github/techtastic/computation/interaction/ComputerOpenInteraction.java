package io.github.techtastic.computation.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
import io.github.techtastic.computation.ComputationPlugin;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jspecify.annotations.NonNull;

public class ComputerOpenInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<ComputerOpenInteraction> CODEC = BuilderCodec.builder(
            ComputerOpenInteraction.class, ComputerOpenInteraction::new, SimpleInstantInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@NonNull InteractionType interactionType, @NonNull InteractionContext interactionContext, @NonNull CooldownHandler cooldownHandler) {
        ComputationPlugin.getComputationLogger().atInfo().log("You interacted!");

        ComputationPlugin.LUA.runScript("print(\"Hello World!\")");
    }

    @Override
    public Int2ObjectMap<IntSet> getTags() {
        return super.getTags();
    }

    @Override
    public Operation getInnerOperation() {
        return super.getInnerOperation();
    }
}
