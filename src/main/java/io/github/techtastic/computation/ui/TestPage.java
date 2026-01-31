package io.github.techtastic.computation.ui;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.techtastic.computation.ComputationPlugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TestPage extends InteractiveCustomUIPage<TestPage.Data> {
    private final List<String> outputs = new ArrayList<>();

    public TestPage(@NonNull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@NonNull Ref<EntityStore> ref, @NonNull UICommandBuilder uiCommandBuilder, @NonNull UIEventBuilder uiEventBuilder, @NonNull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/TestPage.ui");
        if (!outputs.isEmpty())
            uiCommandBuilder.set("#Output.Text", String.join("\n", outputs));

        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#Run",
                new EventData().append("@Snippet", "#RunCommand.Value")
        );
    }

    @Override
    public void handleDataEvent(@NonNull Ref<EntityStore> ref, @NonNull Store<EntityStore> store, @NonNull Data data) {
        if (data.snippet == null) return;
        AtomicReference<String> print = new AtomicReference<>();
        Object[] results = ComputationPlugin.LUA.runScript(data.snippet, print::set, err -> {});
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;
        TestPage page = (TestPage) player.getPageManager().getCustomPage();
        if (page == null) return;
        page.addOutput(data.snippet, String.join(",", Arrays.stream(results).map(o -> (String) o).toList()));
        String prt = print.get();
        if (prt != null)
            page.addOutput(prt, null);

        UICommandBuilder uiCommandBuilder = new UICommandBuilder();
        uiCommandBuilder.set("#Output.Text", String.join("\n", outputs));
        sendUpdate(uiCommandBuilder, false);
    }

    public void addOutput(String snippet, @Nullable String results) {
        if (outputs.size() > 10) {
            outputs.removeFirst();
            if (results != null)
                outputs.removeFirst();
        }
        outputs.add(ComputationPlugin.LUA.highlightSyntax(snippet));
        if (results != null)
            outputs.add(results);
    }

    public static class Data {
        public String snippet;

        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(
                        new KeyedCodec<>("@Snippet", BuilderCodec.STRING),
                        (obj, val) -> obj.snippet = val,
                        obj -> obj.snippet
                ).add()
                .build();
    }
}
