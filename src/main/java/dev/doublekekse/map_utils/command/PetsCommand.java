package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import dev.doublekekse.map_utils.registry.MapUtilsTags;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PetsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("pets").requires(source -> source.hasPermission(2))
                .then(literal("store").then(argument("player", EntityArgument.player()).then(argument("id", StringArgumentType.string())
                        .then(argument("range", DoubleArgumentType.doubleArg(0)).suggests((ctx, builder) -> builder.suggest(20).buildFuture())
                                .executes(ctx -> PetsCommand.executeSave(ctx, Optional.of(DoubleArgumentType.getDouble(ctx, "range")))))
                        .then(literal("all").executes(ctx -> PetsCommand.executeSave(ctx, Optional.empty())))
                ))).then(literal("load").then(argument("player", EntityArgument.player()).then(argument("id", StringArgumentType.string()).executes(ctx -> {
                    var player = EntityArgument.getPlayer(ctx, "player");
                    var id = player.getStringUUID() + "-" + StringArgumentType.getString(ctx, "id");

                    var data = MapUtilsSavedData.getServerData(ctx.getSource().getServer());
                    var pets = data.getPets(id);

                    if (pets == null) {
                        ctx.getSource().sendFailure(Component.translatable("commands.map_utils.pets.missing", id));
                        return 0;
                    }

                    var level = player.level();
                    EntityType.loadEntitiesRecursive(pets, level).forEach(level::addFreshEntity);

                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.pets.load", id), false);

                    return 1;
                })))).then(literal("list").executes(ctx -> {
                    var source = ctx.getSource();
                    var data = MapUtilsSavedData.getServerData(source.getServer());

                    source.sendSuccess(() -> Component.translatable("commands.map_utils.pets.list"), false);

                    for (var key : data.pets.keySet()) {
                        source.sendSuccess(() -> Component.literal(key), false);
                    }

                    return 1;
                }))
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static int executeSave(CommandContext<CommandSourceStack> ctx, Optional<Double> optional) throws CommandSyntaxException {
        var player = EntityArgument.getPlayer(ctx, "player");
        var id = player.getStringUUID() + "-" + StringArgumentType.getString(ctx, "id");

        Predicate<Entity> canSave = entity -> {
            if (entity == null || !entity.isAlive()) {
                return false;
            }
            if (!entity.getType().is(MapUtilsTags.SAVABLE_ENTITIES)) {
                return false;
            }
            if (entity instanceof TraceableEntity traceable && player.equals(traceable.getOwner())) {
                return true;
            }
            if (entity instanceof OwnableEntity ownable && player.equals(ownable.getOwner())) {
                return true;
            }
            return false;
        };

        var level = ctx.getSource().getLevel();
        List<Entity> pets;
        if (optional.isEmpty()) {
            pets = StreamSupport.stream(level.getAllEntities().spliterator(), false)
                    .filter(canSave).toList();
        } else {
            double range = optional.get();
            pets = level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(range), entity -> canSave.test(entity) && entity.distanceTo(player) <= range);
        }
        var data = MapUtilsSavedData.getServerData(ctx.getSource().getServer());

        var list = new ArrayList<CompoundTag>(pets.size());

        for (var pet : pets) {
            var tag = new CompoundTag();
            if (pet.save(tag)) {
                list.add(tag);
                pet.discard();
            }
        }

        data.setPets(id, list);

        ctx.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.pets.store", id), false);

        return 1;
    }
}
