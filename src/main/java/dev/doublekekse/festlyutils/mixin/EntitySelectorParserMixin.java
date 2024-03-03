package dev.doublekekse.festlyutils.mixin;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.doublekekse.festlyutils.duck.EntitySelectorDuck;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static net.minecraft.commands.arguments.selector.EntitySelectorParser.*;

@Mixin(EntitySelectorParser.class)
public abstract class EntitySelectorParserMixin {
    @Shadow
    private boolean usesSelectors;

    @Shadow
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;

    @Shadow
    protected abstract CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer);

    @Shadow
    @Final
    private StringReader reader;

    @Shadow
    private int maxResults;

    @Shadow
    private boolean includesEntities;

    @Shadow
    private boolean currentEntity;

    @Shadow
    protected abstract CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer);

    @Shadow
    private boolean worldLimited;
    @Shadow
    private MinMaxBounds.Doubles distance;
    @Shadow
    @Nullable
    private UUID entityUUID;
    @Unique
    private String userSelectorName = null;

    @Inject(method = "parseSelector", at = @At("HEAD"), cancellable = true)
    void parseSelector(CallbackInfo ci) throws CommandSyntaxException {
        suggestions = this::suggestSelector;
        if (!reader.canRead()) {
            return;
        }

        char selector = this.reader.peek();
        if (selector == 'u') {
            this.reader.skip();

            usesSelectors = true;
            userSelectorName = null;

            maxResults = 1;
            includesEntities = true;
            currentEntity = false;

            this.suggestions = this::suggestOpenOptions;

            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.reader.skip();
                this.suggestions = SUGGEST_NOTHING;
                parseSelector();
            }
            ci.cancel();
        }
    }

    @Inject(method = "getSelector", at = @At("HEAD"), cancellable = true)
    void getSelector(CallbackInfoReturnable<EntitySelector> cir) {
        if (userSelectorName != null && usesSelectors && !userSelectorName.isEmpty()) {
            var selector = new EntitySelector(1, true, worldLimited, null, distance, (vec3 -> vec3), null, EntitySelector.ORDER_ARBITRARY, false, null, entityUUID, null, true);
            ((EntitySelectorDuck) selector).festlyUtils$userSelectorName(userSelectorName);

            cir.setReturnValue(selector);
        }
    }

    @Unique
    private void parseSelector() throws CommandSyntaxException {
        this.suggestions = this::suggestDefaultName;

        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.suggestions = SUGGEST_NOTHING;
            this.reader.skipWhitespace();

            String string = this.reader.readString();
            if (string.isEmpty()) {
                throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
            }

            userSelectorName = string;
            this.suggestions = this::suggestClose;
        }

        if (this.reader.canRead()) {
            if (this.reader.read() != ']') {
                throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
            } else {
                this.suggestions = SUGGEST_NOTHING;
            }
        } else {
            throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
        }
    }

    @Unique
    private CompletableFuture<Suggestions> suggestDefaultName(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        // TODO There might be a way to get some predictions working here?

        return suggestionsBuilder.buildFuture();
    }

    @Unique
    private CompletableFuture<Suggestions> suggestClose(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
        suggestionsBuilder.suggest("]");

        return suggestionsBuilder.buildFuture();
    }
}
