package io.github.techtastic.computation.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.techtastic.computation.ComputationPlugin;
import io.github.techtastic.computation.ui.TestPage;
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

        //ComputationPlugin.LUA.runScript("local count = 0 while true do print(\"Hello World! #\"..tostring(count)) count = count + 1 end");

        Ref<EntityStore> ref = interactionContext.getOwningEntity();
        Store<EntityStore> store = ref.getStore();
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;
        TestPage page = new TestPage(playerRef);
        player.getPageManager().openCustomPage(ref, store, page);
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
