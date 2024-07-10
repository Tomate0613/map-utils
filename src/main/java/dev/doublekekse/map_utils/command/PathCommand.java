package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.doublekekse.map_utils.command.argument.PathArgumentType;
import dev.doublekekse.map_utils.curve.PositionAndRotation;
import dev.doublekekse.map_utils.curve.SplineInterpolation;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PathCommand {
    static class PathData {
        int pathTicks = 0;

        Entity entity;
        int pathDuration;
        PositionAndRotation[] path;
    }

    static List<PathData> list = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("path").requires(source -> source.hasPermission(2))
                .then(literal("apply").then(argument("entity", EntityArgument.entity()).then(argument("duration", TimeArgument.time(1)).then(argument("path", PathArgumentType.path()).executes(context -> {
                    var data = new PathData();

                    data.entity = EntityArgument.getEntity(context, "entity");
                    data.path = PathArgumentType.getPath(context, "path");
                    data.pathDuration = IntegerArgumentType.getInteger(context, "duration");

                    list.add(data);

                    return 1;
                }))))).then(literal("cancel").then(argument("entity", EntityArgument.entity()).executes(context -> {
                    var entity = EntityArgument.getEntity(context, "entity");

                    list.removeIf(data -> data.entity == entity);

                    return 1;
                })))
        );
    }

    public static void registerTickListener() {
        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            for (int i = list.size() - 1; i >= 0; i--) {
                var data = list.get(i);
                var progress =  ((float) data.pathTicks) / data.pathDuration;

                if (progress > 1 || data.entity == null) {
                    list.remove(i);
                } else {
                    data.pathTicks++;

                    var positionAndRotation = SplineInterpolation.generateSmoothPath(data.path, progress);

                    var pos = positionAndRotation.position();
                    var rot = positionAndRotation.rotation();

                    data.entity.setPos(pos);
                    data.entity.absRotateTo(rot.x, rot.y);
                }
            }
        });
    }
}
