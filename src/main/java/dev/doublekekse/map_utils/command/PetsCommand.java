package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;

import java.util.ArrayList;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PetsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("pets").requires(source -> source.hasPermission(2))
                .then(literal("store").then(argument("player", EntityArgument.player()).then(argument("id", StringArgumentType.string()).executes(ctx -> {
                        var player = EntityArgument.getPlayer(ctx, "player");
                        var id = player.getStringUUID() + "-" + StringArgumentType.getString(ctx, "id");

                        var level = player.level();
                        var pets = level.getEntitiesOfClass(TamableAnimal.class, player.getBoundingBox().inflate(20), (animal) -> animal.getOwner() == player);
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
                )))).then(literal("load").then(argument("player", EntityArgument.player()).then(argument("id", StringArgumentType.string()).executes(ctx -> {
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
}
