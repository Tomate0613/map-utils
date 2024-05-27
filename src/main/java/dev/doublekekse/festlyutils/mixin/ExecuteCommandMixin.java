package dev.doublekekse.festlyutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.ExecuteCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin {
    @Unique
    private static LiteralCommandNode<CommandSourceStack> entry;

    @Inject(method = "register", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;register(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;)Lcom/mojang/brigadier/tree/LiteralCommandNode;", ordinal = 1))
    private static void reg(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext commandBuildContext, CallbackInfo ci, @Local(name = "literalCommandNode") LiteralCommandNode<CommandSourceStack> _entry) {
        entry = _entry;
    }
    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;register(Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;)Lcom/mojang/brigadier/tree/LiteralCommandNode;", ordinal = 1))
    private static LiteralCommandNode<CommandSourceStack> register(CommandDispatcher<CommandSourceStack> instance, LiteralArgumentBuilder<CommandSourceStack> command) {
        if(entry == null) {
            // TODO There has to be a better way to get the entry
            throw new RuntimeException("Could not find entry");
        }

        return instance.register(command.then(literal("select").then(argument("name", StringArgumentType.string()).redirect(entry, (commandContext) -> {
            var commandSourceStack = commandContext.getSource();

            var entity = commandSourceStack.getEntity();
            assert entity != null;
            commandSourceStack.festlyUtils$addSelector(StringArgumentType.getString(commandContext, "name"), entity);

            return commandSourceStack;
        }))));
    }
}
