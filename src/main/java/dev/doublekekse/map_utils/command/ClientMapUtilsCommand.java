package dev.doublekekse.map_utils.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.nbt.SnbtPrinterTagVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import org.apache.commons.compress.utils.Lists;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;


public class ClientMapUtilsCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            literal("map_utils")
                .then(literal("copy_summon_command").executes(ctx -> {
                    var minecraft = ctx.getSource().getClient();
                    var level = ctx.getSource().getLevel();
                    var hitResult = minecraft.hitResult;

                    if (hitResult instanceof EntityHitResult entityHitResult) {
                        var entity = entityHitResult.getEntity();

                        var valueOutput = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
                        entity.save(valueOutput);
                        var tag = valueOutput.buildResult();

                        tag.remove("id");
                        tag.remove("UUID");

                        var key = entity.typeHolder().unwrapKey();

                        if (key.isEmpty()) {
                            ctx.getSource().sendError(Component.translatable("commands.map_utils.no_entity_id"));
                            return 0;
                        }

                        var id = key.get().identifier();
                        var snbt = new SnbtPrinterTagVisitor("", 0, Lists.newArrayList()).visit(tag);

                        minecraft.keyboardHandler.setClipboard("/summon " + id + " ~ ~ ~ " + snbt);
                        ctx.getSource().sendFeedback(Component.translatable("commands.map_utils.copy_summon_command"));
                        return 0;
                    }

                    ctx.getSource().sendError(Component.translatable("commands.map_utils.no_entity_hit_result"));
                    return 0;
                }))
        );
    }
}
