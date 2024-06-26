package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.doublekekse.map_utils.duck.CommandSourceStackDuck;
import dev.doublekekse.map_utils.timer.CommandCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ScheduleCommandExtension {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("schedule").then(literal("command").then(argument("time", TimeArgument.time()).then(argument("command", StringArgumentType.greedyString()).executes(context -> {
                var source = context.getSource();
                var timeOffset = IntegerArgumentType.getInteger(context, "time");
                var worldTime = source.getLevel().getGameTime() + timeOffset;
                var command = StringArgumentType.getString(context, "command");
                var timerQueue = source.getServer().getWorldData().overworldData().getScheduledEvents();

                timerQueue.schedule(command, worldTime, new CommandCallback(command, source.getPosition(), source.getRotation(), ((CommandSourceStackDuck) source).mapUtils$permissionLevel()));

                source.sendSuccess(() -> Component.translatable("commands.map_utils.schedule.created.command", command, timeOffset, worldTime), true);
                return 1;
            }))))
        );
    }
}
