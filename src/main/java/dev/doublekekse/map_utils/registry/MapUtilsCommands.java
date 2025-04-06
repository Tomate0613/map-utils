package dev.doublekekse.map_utils.registry;

import dev.doublekekse.map_utils.MapUtils;
import dev.doublekekse.map_utils.command.*;
import dev.doublekekse.map_utils.command.argument.PathArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

public class MapUtilsCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                ScheduleCommandExtension.register(dispatcher);
                ClickEventCommand.register(dispatcher);
                InventoryCommand.register(dispatcher);
                CameraCommand.register(dispatcher);
                RedstoneCommand.register(dispatcher);
                PathCommand.register(dispatcher);
                AccelerateCommand.register(dispatcher);
            }
        );

        PathCommand.registerTickListener();

        ArgumentTypeRegistry.registerArgumentType(MapUtils.id("path"), PathArgumentType.class, SingletonArgumentInfo.contextFree(PathArgumentType::path));
    }
}
