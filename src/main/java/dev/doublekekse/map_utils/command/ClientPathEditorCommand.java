package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.doublekekse.map_utils.client.MapUtilsClient;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClientPathEditorCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("path_editor")
            .then(literal("toggle").executes(ctx -> {
                MapUtilsClient.pathEditorEnabled = !MapUtilsClient.pathEditorEnabled;

                ctx.getSource().sendFeedback(Component.translatable("commands.map_utils.path_editor.toggle." + (MapUtilsClient.pathEditorEnabled ? "enable" : "disable")));

                return 1;
            }))
        );
    }
}
