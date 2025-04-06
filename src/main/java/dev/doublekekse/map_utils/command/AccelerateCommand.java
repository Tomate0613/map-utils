package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class AccelerateCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("accelerate").then(argument("entity", EntityArgument.entity()).then(literal("towards").then(argument("target", EntityArgument.entity()).then(argument("force", DoubleArgumentType.doubleArg(-.9, .9)).executes(ctx -> {
            var entity = EntityArgument.getEntity(ctx, "entity");
            var target = EntityArgument.getEntity(ctx, "target");
            var force = DoubleArgumentType.getDouble(ctx, "force");

            var delta = target.position().subtract(entity.position()).scale(force);

            // Max acceleration just in case
            if (delta.lengthSqr() > 9) {
                delta = delta.scale(3 / delta.length());
            }

            entity.addDeltaMovement(delta);

            return 1;
        }).then(literal("normalized").executes(ctx -> {
            var entity = EntityArgument.getEntity(ctx, "entity");
            var target = EntityArgument.getEntity(ctx, "target");
            var force = DoubleArgumentType.getDouble(ctx, "force");

            var delta = target.position().subtract(entity.position()).normalize().scale(force);

            entity.addDeltaMovement(delta);

            return 1;
        }))))).then(argument("delta", Vec3Argument.vec3(false)).executes(ctx -> {
            var entity = EntityArgument.getEntity(ctx, "entity");
            var delta = Vec3Argument.getVec3(ctx, "delta");

            // Max acceleration just in case
            if (delta.lengthSqr() > 9) {
                delta = delta.scale(3 / delta.length());
            }

            entity.addDeltaMovement(delta);

            return 1;
        }))));
    }
}
