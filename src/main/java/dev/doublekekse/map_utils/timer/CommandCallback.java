package dev.doublekekse.map_utils.timer;

import dev.doublekekse.map_utils.MapUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandCallback implements TimerCallback<MinecraftServer> {
    final String command;
    final Vec3 position;
    final Vec2 rotation;
    final int permissionLevel;

    public CommandCallback(String command, Vec3 position, Vec2 rotation, int permissionLevel) {
        this.command = command;
        this.position = position;
        this.rotation = rotation;
        this.permissionLevel = permissionLevel;
    }

    public void handle(MinecraftServer minecraftServer, TimerQueue<MinecraftServer> timerQueue, long l) {
        minecraftServer.getCommands().performPrefixedCommand(createCommandSourceStack(null, minecraftServer.overworld()), command);
    }


    private CommandSourceStack createCommandSourceStack(@Nullable Entity entity, ServerLevel level) {
        String name = entity == null ? "CommandCallback" : entity.getName().getString() + " (CommandCallback)";
        Component nameComponent = Component.literal(name);

        entityNameComponent:
        if (entity != null) {
            var displayName = entity.getDisplayName();

            if (displayName == null)
                break entityNameComponent;

            displayName.copy().append(" (CommandCallback)");
        }

        // TODO
        return new CommandSourceStack(CommandSource.NULL, position, rotation, level, permissionLevel, name, nameComponent, level.getServer(), entity);
    }

    public static class Serializer extends TimerCallback.Serializer<MinecraftServer, CommandCallback> {
        public Serializer() {
            super(MapUtils.identifier("command"), CommandCallback.class);
        }

        public void serialize(CompoundTag compoundTag, CommandCallback commandCallback) {
            compoundTag.putString("command", commandCallback.command);

            compoundTag.putDouble("posX", commandCallback.position.x);
            compoundTag.putDouble("posY", commandCallback.position.y);
            compoundTag.putDouble("posZ", commandCallback.position.z);

            compoundTag.putFloat("rotX", commandCallback.rotation.x);
            compoundTag.putFloat("rotY", commandCallback.rotation.y);

            compoundTag.putInt("permissionLevel", commandCallback.permissionLevel);
        }

        public @NotNull CommandCallback deserialize(CompoundTag compoundTag) {
            String command = compoundTag.getString("command");

            double posX = compoundTag.getDouble("posX");
            double posY = compoundTag.getDouble("posY");
            double posZ = compoundTag.getDouble("posZ");

            float rotX = compoundTag.getFloat("rotX");
            float rotY = compoundTag.getFloat("rotY");

            int permissionLevel = compoundTag.getInt("permissionLevel");

            return new CommandCallback(command, new Vec3(posX, posY, posZ), new Vec2(rotX, rotY), permissionLevel);
        }
    }
}
