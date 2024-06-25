package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.doublekekse.map_utils.packet.ClickEventPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ClickEventCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var openUrl = argument("player", EntityArgument.player()).then(argument("value", StringArgumentType.greedyString()).executes(context -> handle(context, ClickEvent.Action.OPEN_URL)));
        var openFile = argument("player", EntityArgument.player()).then(argument("value", StringArgumentType.greedyString()).executes(context -> handle(context, ClickEvent.Action.OPEN_FILE)));
        var suggestCommand = argument("player", EntityArgument.player()).then(argument("value", StringArgumentType.greedyString()).executes(context -> handle(context, ClickEvent.Action.SUGGEST_COMMAND)));
        var copy = argument("player", EntityArgument.player()).then(argument("value", StringArgumentType.greedyString()).executes(context -> handle(context, ClickEvent.Action.COPY_TO_CLIPBOARD)));

        dispatcher.register(
            literal("clickevent").requires(source -> source.hasPermission(2))
                .then(literal("open_url").then(openUrl))
                .then(literal("open_file").then(openFile))
                .then(literal("suggest_command").then(suggestCommand))
                .then(literal("copy_to_clipboard").then(copy))
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
