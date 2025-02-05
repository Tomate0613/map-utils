package dev.doublekekse.map_utils.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.doublekekse.map_utils.client.MapUtilsClient;
import dev.doublekekse.map_utils.curve.SplinePath;
import dev.doublekekse.map_utils.data.MapUtilsSavedData;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PathArgumentType implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    public static @Nullable String getPathId(final CommandContext<CommandSourceStack> context, final String name) {
        return context.getArgument(name, String.class);
    }

    public static @Nullable SplinePath getPath(final CommandContext<CommandSourceStack> context, final String name) {
        var id = context.getArgument(name, String.class);
        var server = context.getSource().getServer();
        var savedData = MapUtilsSavedData.getServerData(server);

        return savedData.paths.get(id);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof ClientSuggestionProvider) {
            var savedData = MapUtilsClient.clientSavedData;

            return SharedSuggestionProvider.suggest(savedData.paths.keySet(), builder);
        }

        return Suggestions.empty();
    }

    public static PathArgumentType path() {
        return new PathArgumentType();
    }
}
