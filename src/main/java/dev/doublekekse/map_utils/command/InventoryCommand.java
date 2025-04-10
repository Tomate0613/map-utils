package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class InventoryCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("inv").requires(source -> source.hasPermission(2))
                .then(literal("save").then(argument("player", EntityArgument.player()).then(argument("save", StringArgumentType.string()).then(argument("remove", BoolArgumentType.bool()).executes(context -> {
                        var save = StringArgumentType.getString(context, "save");
                        var isGlobal = save.charAt(0) == '+';
                        var player = EntityArgument.getPlayer(context, "player");
                        var inv = player.getInventory();
                        var data = MapUtilsSavedData.getServerData(context.getSource().getServer());
                        var remove = BoolArgumentType.getBool(context, "remove");

                        if (!isGlobal) {
                            save = player.getStringUUID() + "-" + save;
                        }

                        var invList = new ListTag();
                        inv.save(invList);
                        data.setInventory(save, invList);

                        if (remove) {
                            inv.clearContent();
                        }

                        String finalSave = save;
                        context.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.inventory.saved", finalSave), false);

                        return 1;
                    }
                ))))).then(literal("load").then(argument("player", EntityArgument.player()).then(argument("save", StringArgumentType.string()).then(argument("remove", BoolArgumentType.bool()).executes(context -> {
                    var save = StringArgumentType.getString(context, "save");
                    var isGlobal = save.charAt(0) == '+';
                    var player = EntityArgument.getPlayer(context, "player");
                    var inv = player.getInventory();
                    var data = MapUtilsSavedData.getServerData(context.getSource().getServer());
                    var remove = BoolArgumentType.getBool(context, "remove");

                    if (!isGlobal) {
                        save = player.getStringUUID() + "-" + save;
                    }
                    String finalSave = save;

                    var invList = data.getInventory(save, remove);

                    if (invList == null) {
                        context.getSource().sendFailure(Component.translatable("commands.map_utils.inventory.missing", finalSave));
                        return -1;
                    }

                    inv.load(invList);

                    context.getSource().sendSuccess(() -> Component.translatable("commands.map_utils.inventory.loaded", finalSave), false);

                    return 1;
                }))))).then(literal("list").executes(ctx -> {
                    var source = ctx.getSource();
                    var data = MapUtilsSavedData.getServerData(source.getServer());

                    source.sendSuccess(() -> Component.translatable("commands.map_utils.inventory.list"), false);

                    for (var key : data.inventories.getAllKeys()) {
                        source.sendSuccess(() -> Component.literal(key), false);
                    }

                    return 1;
                }))
        );
    }
}
