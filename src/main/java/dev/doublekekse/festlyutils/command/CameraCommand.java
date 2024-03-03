package dev.doublekekse.festlyutils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.doublekekse.festlyutils.command.argument.PathArgumentType;
import dev.doublekekse.festlyutils.packet.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CameraCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
            literal("camera")
                .then(literal("fov").requires(source -> source.hasPermission(2))
                    .then(literal("reset").executes(context -> {
                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraFovPacket(-1));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.fov.reset"), false);

                        return 1;
                    })).then(literal("set").then(argument("fov", FloatArgumentType.floatArg()).executes((context -> {
                        var fov = FloatArgumentType.getFloat(context, "fov");

                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraFovPacket(fov));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.fov.set", fov), false);

                        return 1;
                    }))))
                ).then(literal("overlay").requires(source -> source.hasPermission(2))
                    .then(literal("reset").executes(context -> {
                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraOverlayPacket(null, 0));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.overlay.reset"), false);

                        return 1;
                    })).then(literal("set").then(argument("overlayTexture", StringArgumentType.string()).then(argument("overlayOpacity", FloatArgumentType.floatArg(0, 1)).executes(context -> {
                        var overlayLocation = new ResourceLocation(StringArgumentType.getString(context, "overlayTexture"));
                        var overlayOpacity = FloatArgumentType.getFloat(context, "overlayOpacity");

                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraOverlayPacket(overlayLocation, overlayOpacity));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.overlay.set", overlayLocation.toString(), overlayOpacity), false);

                        return 1;
                    }))))
                ).then(literal("position").requires(source -> source.hasPermission(2))
                    .then(literal("reset").executes(context -> {
                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.no-player"), false);
                            return -1;
                        }

                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraPositionPacket(null));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.position.reset"), false);

                        return 1;
                    })).then(literal("set").then(argument("position", Vec3Argument.vec3()).executes(context -> {
                        var position = Vec3Argument.getVec3(context, "position");

                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraPositionPacket(position));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.position.set", position.x, position.y, position.z), false);

                        return 1;
                    })))
                ).then(literal("rotation").requires(source -> source.hasPermission(2))
                    .then(literal("reset").executes(context -> {
                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraRotationPacket(null));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.rotation.reset"), false);

                        return 1;
                    })).then(literal("set").then(argument("rotation", RotationArgument.rotation()).executes(context -> {
                        var rotation = RotationArgument.getRotation(context, "rotation").getRotation(context.getSource());

                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        // y and x are flipped for some reason
                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraRotationPacket(new Vec2(rotation.y, rotation.x)));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.rotation.set", rotation.y, rotation.x), false);

                        return 1;
                    })))
                ).then(literal("path").requires(source -> source.hasPermission(2))
                    .then(literal("reset").executes(context -> {
                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraSplinePacket(null, 1));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.path.reset"), false);

                        return 1;
                    })).then(literal("set").then(argument("cameraSpeed", FloatArgumentType.floatArg(0.0001f, 20)).then(argument("path", PathArgumentType.path()).executes(context -> {
                        var path = PathArgumentType.getPath(context, "path");
                        var cameraSpeed = FloatArgumentType.getFloat(context, "cameraSpeed");

                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        ServerPlayNetworking.send(context.getSource().getPlayer(), new CameraSplinePacket(path, cameraSpeed));
                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.path.set", path.length, cameraSpeed), false);

                        return 1;
                    }))))
                ).then(literal("reset").executes(context -> {
                        if (context.getSource().getPlayer() == null) {
                            context.getSource().sendFailure(Component.translatable("commands.festly-utils.camera.no-player"));
                            return -1;
                        }

                        resetCamera(context.getSource().getPlayer());

                        context.getSource().sendSuccess(() -> Component.translatable("commands.festly-utils.camera.reset-all"), false);
                        return 1;
                    })
                ));
    }

    public static void resetCamera(ServerPlayer player) {
        ServerPlayNetworking.send(player, new CameraPositionPacket(null));
        ServerPlayNetworking.send(player, new CameraRotationPacket(null));
        ServerPlayNetworking.send(player, new CameraSplinePacket(null, 1));
        ServerPlayNetworking.send(player, new CameraOverlayPacket(null, 0));
        ServerPlayNetworking.send(player, new CameraFovPacket(-1));
    }
}