package com.example.icestom_plugin;

import io.gitlab.icestom.icestom.plugins.IceStomPlugin;
import net.kyori.adventure.text.Component;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamplePlugin  implements IceStomPlugin {

    private static final Logger log = LoggerFactory.getLogger(ExamplePlugin.class);

    private static ExamplePlugin instance;

    ExamplePlugin() {
        instance = this;
    }

    @Override
    public void onEnable(EventNode<Event> eventNode) {
        log.info("ExamplePlugin Enabled!");

        eventNode.addListener(PlayerSpawnEvent.class, event -> {
            event.getPlayer().sendMessage(Component.text("Hello " + event.getPlayer().getUsername()));
        });
    }

    @Override
    public void onStart() {

    }

    public static ExamplePlugin getInstance() {
        return instance;
    }
}
