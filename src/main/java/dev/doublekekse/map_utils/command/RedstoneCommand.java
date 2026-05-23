package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.doublekekse.map_utils.block.VariableRedstoneBlock;
import dev.doublekekse.map_utils.timer.DeactivateBlockCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;

import static dev.doublekekse.map_utils.registry.MapUtilsBlocks.TRIGGERABLE_REDSTONE_BLOCK;
import static dev.doublekekse.map_utils.registry.MapUtilsBlocks.VARIABLE_REDSTONE_BLOCK;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RedstoneCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("redstone").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(argument("pos", BlockPosArgument.blockPos()).then(argument("power", IntegerArgumentType.integer(0, 15)).then(argument("time", TimeArgument.time(1)).executes(ctx -> {
                    var source = ctx.getSource();
                    var level = source.getLevel();
                    var pos = BlockPosArgument.getBlockPos(ctx, "pos");
                    var timeOffset = IntegerArgumentType.getInteger(ctx, "time");
                    var gameTime = source.getLevel().getGameTime() + timeOffset;
                    var timerQueue = source.getServer().getScheduledEvents();
                    var power = IntegerArgumentType.getInteger(ctx, "power");

                    if (!level.getBlockState(pos).is(VARIABLE_REDSTONE_BLOCK)) {
                        source.sendFailure(Component.translatable("commands.redstone.variable.failed", pos.getX(), pos.getY(), pos.getZ()));
                        return -1;
                    }

                    timerQueue.schedule("redstone callback", gameTime, new DeactivateBlockCallback(level.dimension(), pos));

                    level.setBlockAndUpdate(pos, VARIABLE_REDSTONE_BLOCK.defaultBlockState().setValue(VariableRedstoneBlock.POWER, power));
                    source.sendSuccess(() -> Component.translatable("commands.redstone.timed", pos.getX(), pos.getY(), pos.getZ(), power, timeOffset), true);

                    return 1;
                })).executes((ctx -> {
                    var source = ctx.getSource();
                    var level = source.getLevel();
                    var pos = BlockPosArgument.getBlockPos(ctx, "pos");
                    var power = IntegerArgumentType.getInteger(ctx, "power");

                    if (!level.getBlockState(pos).is(VARIABLE_REDSTONE_BLOCK)) {
                        source.sendFailure(Component.translatable("commands.redstone.variable.failed", pos.getX(), pos.getY(), pos.getZ()));
                        return -1;
                    }

                    level.setBlockAndUpdate(pos, VARIABLE_REDSTONE_BLOCK.defaultBlockState().setValue(VariableRedstoneBlock.POWER, power));
                    source.sendSuccess(() -> Component.translatable("commands.redstone.infinite", pos.getX(), pos.getY(), pos.getZ(), power), true);
                    return 1;
                }
                ))).then(literal("trigger").executes(ctx -> {
                    var source = ctx.getSource();
                    var level = source.getLevel();
                    var pos = BlockPosArgument.getBlockPos(ctx, "pos");

                    var state = level.getBlockState(pos);
                    if (!state.is(TRIGGERABLE_REDSTONE_BLOCK)) {
                        source.sendFailure(Component.translatable("commands.redstone.trigger.failed", pos.getX(), pos.getY(), pos.getZ()));
                        return -1;
                    }

                    TRIGGERABLE_REDSTONE_BLOCK.turnOn(state, level, pos);
                    source.sendSuccess(() -> Component.translatable("commands.redstone.trigger", pos.getX(), pos.getY(), pos.getZ()), true);
                    return 1;
                })))
        );
    }
}
