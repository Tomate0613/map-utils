package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.command.argument.PathArgumentType;
import dev.doublekekse.map_utils.curve.SplineControlPoint;
import dev.doublekekse.map_utils.curve.SplinePath;
import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PathCommand {
    static class PathData {
        int pathTicks = 0;

        Entity entity;
        int pathDuration;
        SplinePath path;
    }

    static List<PathData> list = new ArrayList<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("path").requires(source -> source.hasPermission(2))
                .then(literal("apply").then(argument("entity", EntityArgument.entity()).then(argument("duration", TimeArgument.time(1)).then(argument("path", PathArgumentType.path()).executes(ctx -> {
                    var entity = EntityArgument.getEntity(ctx, "entity");
                    var path = PathArgumentType.getPath(ctx, "path");
                    var duration = IntegerArgumentType.getInteger(ctx, "duration");

                    if (path == null) {
                        ctx.getSource().sendFailure(Component.translatable("commands.map_utils.path.not_found"));
                        return -1;
                    }

                    var data = new PathData();

                    data.entity = entity;
                    data.path = path;
                    data.pathDuration = duration;

                    list.add(data);

                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.path.apply.success", duration), false);

                    return 1;
                }))))).then(literal("cancel").then(argument("entity", EntityArgument.entity()).executes(ctx -> {
                    var entity = EntityArgument.getEntity(ctx, "entity");

                    list.removeIf(data -> data.entity == entity);

                    return 1;
                }))).then(literal("list").executes(ctx -> {
                    var source = ctx.getSource();
                    var savedData = MapUtilsSavedData.getServerData(source.getServer());
                    var ids = savedData.paths.keySet();

                    source.sendSuccess(() -> Component.literal(String.join(", ", ids)), false);

                    return 1;
                })).then(literal("create").then(argument("id", StringArgumentType.string()).executes(ctx -> {
                    var source = ctx.getSource();
                    var server = source.getServer();
                    var id = StringArgumentType.getString(ctx, "id");
                    var entity = source.getEntity();

                    if (entity == null) {
                        ctx.getSource().sendFailure(Component.translatable("commands.map_utils.no_entity"));
                        return -1;
                    }

                    var pos = entity.getEyePosition();
                    var rotation = source.getRotation();

                    var controlPoints = new ArrayList<SplineControlPoint>();
                    controlPoints.add(new SplineControlPoint(pos, new Vec2(rotation.y, rotation.x)));

                    var savedData = MapUtilsSavedData.getServerData(server);

                    if (savedData.paths.containsKey(id)) {
                        ctx.getSource().sendFailure(Component.translatable("commands.map_utils.path.create.already_exists"));
                        return -1;
                    }

                    savedData.paths.put(id, new SplinePath(controlPoints));

                    MapUtils.invalidateData(server);

                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.path.create.success"), false);

                    return 1;
                }))).then(literal("delete").then(argument("id", PathArgumentType.path()).executes(ctx -> {
                    var source = ctx.getSource();
                    var server = source.getServer();
                    var path = PathArgumentType.getPath(ctx, "id");
                    var pathId = PathArgumentType.getPathId(ctx, "id");

                    if (path == null) {
                        ctx.getSource().sendFailure(Component.translatable("commands.map_utils.path.not_found"));
                        return -1;
                    }

                    var savedData = MapUtilsSavedData.getServerData(server);
                    savedData.paths.remove(pathId);
                    MapUtils.invalidateData(server);

                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.path.delete.success", pathId), true);

                    return 1;
                }))).then(literal("edit")
                    .then(argument("path", PathArgumentType.path()).then(literal("add").executes(ctx -> {
                        var source = ctx.getSource();
                        var entity = source.getEntity();

                        if (entity == null) {
                            ctx.getSource().sendFailure(Component.translatable("commands.map_utils.no_entity"));
                            return -1;
                        }

                        var pos = entity.getEyePosition();
                        var rotation = source.getRotation();
                        var path = PathArgumentType.getPath(ctx, "path");

                        if (path == null) {
                            ctx.getSource().sendFailure(Component.translatable("commands.map_utils.path.not_found"));
                            return -1;
                        }

                        var controlPoints = path.controlPoints();
                        controlPoints.add(new SplineControlPoint(pos, new Vec2(rotation.y, rotation.x)));
                        MapUtils.invalidateData(source.getServer());

                        ctx.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.path.edit.add.success"), false);

                        return 1;
                    })).then(literal("insert").then(argument("index", IntegerArgumentType.integer(0)).executes(ctx -> {
                        var source = ctx.getSource();
                        var entity = source.getEntity();

                        if (entity == null) {
                            ctx.getSource().sendFailure(Component.translatable("commands.map_utils.no_entity"));
                            return -1;
                        }

                        var pos = entity.getEyePosition();
                        var rotation = source.getRotation();
                        var path = PathArgumentType.getPath(ctx, "path");

                        if (path == null) {
                            ctx.getSource().sendFailure(Component.translatable("commands.map_utils.path.not_found"));
                            return -1;
                        }

                        var controlPoints = path.controlPoints();
                        var index = IntegerArgumentType.getInteger(ctx, "index");

                        if (index < 0 || index > controlPoints.size()) {
                            ctx.getSource().sendFailure(Component.translatable("commands.map_utils.path.edit.out_of_bounds"));
                            return -1;
                        }

                        controlPoints.add(index, new SplineControlPoint(pos, new Vec2(rotation.y, rotation.x)));
                        MapUtils.invalidateData(source.getServer());

                        ctx.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.path.edit.add.success"), false);

                        return 1;
                    }))).then(literal("remove").then(argument("index", IntegerArgumentType.integer(0)).executes(ctx -> {
                        var source = ctx.getSource();
                        var path = PathArgumentType.getPath(ctx, "path");

                        if (path == null) {
                            ctx.getSource().sendFailure(Component.translatable("commands.map_utils.path.not_found"));
                            return -1;
                        }

                        var controlPoints = path.controlPoints();
                        var index = IntegerArgumentType.getInteger(ctx, "index");

                        if (controlPoints.size() <= 1) {
                            ctx.getSource().sendFailure(Component.translatable("commands.map_utils.path.edit.remove.to_few_control_points"));
                            return -1;
                        }

                        if (index < 0 || index >= controlPoints.size()) {
                            ctx.getSource().sendFailure(Component.translatable("commands.map_utils.path.edit.out_of_bounds"));
                            return -1;
                        }

                        controlPoints.remove(index);
                        MapUtils.invalidateData(source.getServer());

                        ctx.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.path.edit.remove.success"), false);

                        return 1;
                    }))))
                )
        );
    }

    public static void registerTickListener() {
        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            for (int i = list.size() - 1; i >= 0; i--) {
                var data = list.get(i);
                var progress = ((float) data.pathTicks) / data.pathDuration;

                if (progress > 1 || data.entity == null) {
                    list.remove(i);
                } else {
                    data.pathTicks++;

                    var pos = data.path.getPosition(progress);
                    var rot = data.path.getRotation(progress);

                    data.entity.setPos(pos);
                    data.entity.absRotateTo(rot.x, rot.y);
                }
            }
        });
    }
}
