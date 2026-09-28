package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.doublekekse.map_utils.client.MapUtilsClient;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class ClientAnnotationsCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("annotations").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(literal("hide").executes(ctx -> setVisibility(ctx, MapUtilsClient.AnnotationVisibility.HIDDEN)))
            .then(literal("always_on_top").executes(ctx -> setVisibility(ctx, MapUtilsClient.AnnotationVisibility.ALWAYS_ON_TOP)))
            .then(literal("hide_behind_walls").executes(ctx -> setVisibility(ctx, MapUtilsClient.AnnotationVisibility.NORMAL)))
        );
    }

    private static int setVisibility(CommandContext<FabricClientCommandSource> ctx, MapUtilsClient.AnnotationVisibility visibility) {
        MapUtilsClient.annotationVisibility = visibility;

        ctx.getSource().sendFeedback(Component.translatable("commands.map_utils.annotations." + visibility.toString().toLowerCase()));

        return 1;
    }
}
