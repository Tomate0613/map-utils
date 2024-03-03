package dev.doublekekse.festlyutils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.doublekekse.festlyutils.block.VariableRedstoneBlock;
import dev.doublekekse.festlyutils.timer.CommandCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;

import static dev.doublekekse.festlyutils.FestlyUtils.VARIABLE_REDSTONE_BLOCK;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RedstoneCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("redstone").requires(source -> source.hasPermission(2))
                .then(argument("blockPos", BlockPosArgument.blockPos()).then(argument("power", IntegerArgumentType.integer(0, 15)).then(argument("time", TimeArgument.time()).executes(context -> {
                    var source = context.getSource();
                    var level = source.getLevel();
                    var blockPos = BlockPosArgument.getBlockPos(context, "blockPos");
                    var timeOffset = IntegerArgumentType.getInteger(context, "time");
                    var gameTime = source.getLevel().getGameTime() + timeOffset;
                    var timerQueue = source.getServer().getWorldData().overworldData().getScheduledEvents();
                    var power = IntegerArgumentType.getInteger(context, "power");

                    if (!level.getBlockState(blockPos).is(VARIABLE_REDSTONE_BLOCK)) {
                        source.sendFailure(Component.translatable("commands.redstone.failed", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                        return -1;
                    }

                    var command = "setblock ~ ~ ~ festly-utils:variable_redstone_block";
                    timerQueue.schedule(command, gameTime, new CommandCallback(command, blockPos.getCenter(), Vec2.ZERO, 2));

                    level.setBlockAndUpdate(blockPos, VARIABLE_REDSTONE_BLOCK.defaultBlockState().setValue(VariableRedstoneBlock.POWER, power));
                    source.sendSuccess(() -> Component.translatable("commands.redstone.timed", blockPos.getX(), blockPos.getY(), blockPos.getZ(), power, timeOffset), true);

                    return 1;
                })).then(literal("infinite").executes((context -> {
                    var source = context.getSource();
                    var level = source.getLevel();
                    var blockPos = BlockPosArgument.getBlockPos(context, "blockPos");
                    var power = IntegerArgumentType.getInteger(context, "power");

                    if (!level.getBlockState(blockPos).is(VARIABLE_REDSTONE_BLOCK)) {
                        source.sendFailure(Component.translatable("commands.redstone.failed", blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                        return -1;
                    }

                    level.setBlockAndUpdate(blockPos, VARIABLE_REDSTONE_BLOCK.defaultBlockState().setValue(VariableRedstoneBlock.POWER, power));
                    source.sendSuccess(() -> Component.translatable("commands.redstone.infinite", blockPos.getX(), blockPos.getY(), blockPos.getZ(), power), true);
                    return 1;
                }
                )))))
        );
    }
}
