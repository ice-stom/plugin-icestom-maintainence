package io.gitlab.icestom.icestom_maintainence;

import io.gitlab.icestom.icestom.IceStom;
import io.gitlab.icestom.icestom.plugins.IceStomPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class IceStomMaintenance implements IceStomPlugin {

    public static final String NAMESPACE = "maintenance";
    public static final Component KICK_MESSAGE = Component.text("This server is in maintenance mode.", NamedTextColor.RED);
    private static final boolean DEFAULT_STATE = false;

    private static final Logger log = LoggerFactory.getLogger(IceStomMaintenance.class);

    private static IceStomMaintenance instance;

    private final Path stateFile = Path.of("plugins", NAMESPACE, "state.txt");

    private volatile boolean maintenanceActive = DEFAULT_STATE;

    IceStomMaintenance() {
        instance = this;
    }

    @Override
    public void onEnable(EventNode<Event> eventNode) {
        log.info("IceStom Maintenance Enabled!");

        maintenanceActive = loadState();
        log.info("Maintenance mode is currently {}.", maintenanceActive ? "ON" : "OFF");

        MinecraftServer.getCommandManager().register(new MaintenanceCommand());

        eventNode.addListener(AsyncPlayerPreLoginEvent.class, event -> {
            if (!maintenanceActive) return;

            boolean bypass = IceStom.getInstance().getPermissionHandler().test(event.getGameProfile().uuid(), NAMESPACE + ".bypass");

            if (bypass) return;

            event.getConnection().kick(KICK_MESSAGE);
        });
    }

    public void setMaintenanceActive(boolean maintenanceActive) {
        if (this.maintenanceActive == maintenanceActive) return;

        this.maintenanceActive = maintenanceActive;
        saveState(maintenanceActive);
    }

    public boolean isMaintenanceActive() {
        return maintenanceActive;
    }

    @Override
    public void onStart() {}

    public static IceStomMaintenance getInstance() {
        return instance;
    }

    private boolean loadState() {
        if (!Files.exists(stateFile)) {
            saveState(DEFAULT_STATE);
            return DEFAULT_STATE;
        }

        try {
            String raw = Files.readString(stateFile, StandardCharsets.UTF_8).trim();

            if (raw.equalsIgnoreCase("true")) return true;
            if (raw.equalsIgnoreCase("false")) return false;

            log.warn("Invalid maintenance state '{}' in {}, falling back to default ({}).", raw, stateFile, DEFAULT_STATE);
        } catch (IOException e) {
            log.error("Failed to read maintenance state from {}, falling back to default ({}).", stateFile, DEFAULT_STATE, e);
        }

        return DEFAULT_STATE;
    }

    private void saveState(boolean state) {
        try {
            Files.createDirectories(stateFile.getParent());

            Path tmp = stateFile.resolveSibling(stateFile.getFileName() + ".tmp");
            Files.writeString(tmp, Boolean.toString(state), StandardCharsets.UTF_8);

            try {
                Files.move(tmp, stateFile, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(tmp, stateFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            log.error("Failed to save maintenance state to {}", stateFile, e);
        }
    }
}