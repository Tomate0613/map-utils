package dev.doublekekse.festlyutils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.doublekekse.festlyutils.packet.ClickEventPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ClickEventCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var argumentBuilder = argument("player", EntityArgument.player()).then(argument("value", StringArgumentType.greedyString()).executes(context -> handle(context, ClickEvent.Action.OPEN_URL)));

        dispatcher.register(
            literal("clickevent").requires(source -> source.hasPermission(2))
                .then(literal("open_url").then(argumentBuilder))
                .then(literal("open_file").then(argumentBuilder))
                .then(literal("suggest_command").then(argumentBuilder))
                .then(literal("copy_to_clipboard").then(argumentBuilder))
        );
    }

    public static int handle(CommandContext<CommandSourceStack> context, ClickEvent.Action action) throws CommandSyntaxException {
        var value = StringArgumentType.getString(context, "value");
        var player = EntityArgument.getPlayer(context, "player");

        ServerPlayNetworking.send(player, new ClickEventPacket(action, value));
        context.getSource().sendSuccess(() -> Component.translatable("commands.clickevent.success"), true);

        return 1;
    }
}
