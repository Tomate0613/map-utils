package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.doublekekse.map_utils.packet.ClientboundCreditsPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;

public class CreditsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("credits").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)).then(argument("player", EntityArgument.player()).then(argument("show_poem", BoolArgumentType.bool()).executes(ctx -> {
            var showPoem = BoolArgumentType.getBool(ctx, "show_poem");
            var player = EntityArgument.getPlayer(ctx, "player");

            ServerPlayNetworking.send(player, new ClientboundCreditsPacket(showPoem));

            ctx.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.credits", player.getDisplayName()), true);

            return 0;
        }))));
    }
}
