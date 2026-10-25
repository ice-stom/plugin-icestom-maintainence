package io.gitlab.icestom.icestom_maintainence;

import io.gitlab.icestom.icestom.entity.IceStomPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import static io.gitlab.icestom.icestom_maintainence.IceStomMaintenance.KICK_MESSAGE;
import static io.gitlab.icestom.icestom_maintainence.IceStomMaintenance.NAMESPACE;

public class MaintenanceCommand extends Command {
    public MaintenanceCommand() {
        super("maintenance");

        setCondition((sender, _) -> {
            if (sender instanceof Player player) {
                return ((IceStomPlayer) player).hasPermission(NAMESPACE + ".bypass");
            }

            return true;
        });

        var bool = ArgumentType.Boolean("enabled");

        addSyntax((sender, context) -> {
            final boolean enabled = context.get(bool);

            IceStomMaintenance.getInstance().setMaintenanceActive(enabled);

            if (enabled) {
                for (Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                    onlinePlayer.kick(KICK_MESSAGE);
                }
            }
        }, bool);
    }
}
