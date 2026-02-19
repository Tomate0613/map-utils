package dev.doublekekse.map_utils.timer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.doublekekse.map_utils.compat.player_roles.PlayerRoleCompatibility;
import dev.doublekekse.map_utils.utils.AdditionalCodecs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public record CommandCallback(ResourceKey<Level> dimension, @NotNull Optional<UUID> entityUUID,
                              String command, Vec3 position, Vec2 rotation)
    implements TimerCallback<MinecraftServer> {
    public static final MapCodec<CommandCallback> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(CommandCallback::dimension),
            AdditionalCodecs.UUID_CODEC.optionalFieldOf("entity_uuid").forGetter(CommandCallback::entityUUID),
            Codec.STRING.fieldOf("command").forGetter(CommandCallback::command),
            Vec3.CODEC.fieldOf("position").forGetter(CommandCallback::position),
            Vec2.CODEC.fieldOf("rotation").forGetter(CommandCallback::rotation)
        ).apply(instance, CommandCallback::new)
    );

    @Override
    public void handle(MinecraftServer minecraftServer, TimerQueue<MinecraftServer> timerQueue, long l) {
        var level = minecraftServer.getLevel(dimension);
        if (level == null) {
            // TODO
            return;
        }
        var entity = entityUUID.isEmpty() ? null : level.getEntity(entityUUID.get());
        minecraftServer.getCommands().performPrefixedCommand(createCommandSourceStack(entity, level), command);
    }

    private CommandSourceStack createCommandSourceStack(@Nullable Entity entity, ServerLevel level) {
        String name = entity == null ? "CommandCallback" : entity.getName().getString() + " (CommandCallback)";
        Component nameComponent = Component.literal(name);

        entityNameComponent:
        if (entity != null) {
            var displayName = entity.getDisplayName();
            displayName.copy().append(" (CommandCallback)");
        }

        // TODO
        var stack = new CommandSourceStack(CommandSource.NULL, position, rotation, level, LevelBasedPermissionSet.forLevel(PermissionLevel.GAMEMASTERS), name, nameComponent, level.getServer(), entity);

        if (FabricLoader.getInstance().isModLoaded("player_roles")) {
            PlayerRoleCompatibility.applyCommandIdentityType(stack);
        }

        return stack;
    }

    @Override
    public @NotNull MapCodec<CommandCallback> codec() {
        return CODEC;
    }
}
