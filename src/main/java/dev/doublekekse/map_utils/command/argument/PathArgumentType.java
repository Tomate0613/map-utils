package dev.doublekekse.map_utils.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.doublekekse.map_utils.curve.PositionAndRotation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class PathArgumentType implements ArgumentType<PositionAndRotation[]> {
    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.map_utils.path.incomplete"));
    public static final SimpleCommandExceptionType ERROR_NOT_ENOUGH_NODES = new SimpleCommandExceptionType(Component.translatable("argument.map_utils.path.not_enough_nodes"));
    public static final SimpleCommandExceptionType ERROR_NO_SEMICOLON = new SimpleCommandExceptionType(Component.translatable("argument.map_utils.path.no_semicolon"));
    public static final SimpleCommandExceptionType ERROR_POS_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.pos3d.incomplete")); // Vanilla

    @Override
    public PositionAndRotation[] parse(StringReader reader) throws CommandSyntaxException {
        var whole = reader.getRemaining();
        var entries = whole.split(";");

        var path = new PositionAndRotation[entries.length];

        for (int i = 0; i < entries.length; i++) {
            var position = readPosition(reader);
            var cursor = reader.getCursor();

            if (!(reader.canRead() && reader.read() == ' ')) {
                reader.setCursor(cursor);
                throw ERROR_NOT_COMPLETE.createWithContext(reader);
            }

            var rotation = readRotation(reader);

            cursor = reader.getCursor();
            if (!(reader.canRead() && reader.read() == ';')) {
                reader.setCursor(cursor);
                throw ERROR_NO_SEMICOLON.createWithContext(reader);
            }

            reader.skipWhitespace();

            path[i] = new PositionAndRotation(position, rotation);
        }


        if (entries.length < 4) {
            throw ERROR_NOT_ENOUGH_NODES.createWithContext(reader);
        }

        return path;
    }

    public Vec3 readPosition(StringReader reader) throws CommandSyntaxException {
        var cursor = reader.getCursor();
        var x = reader.readDouble();

        if (!(reader.canRead() && reader.read() == ' ')) {
            reader.setCursor(cursor);
            throw ERROR_POS_NOT_COMPLETE.createWithContext(reader);
        }

        var y = reader.readDouble();

        if (!(reader.canRead() && reader.read() == ' ')) {
            reader.setCursor(cursor);
            throw ERROR_POS_NOT_COMPLETE.createWithContext(reader);
        }

        var z = reader.readDouble();

        return new Vec3(x, y, z);
    }

    public Vec2 readRotation(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();

        float x = reader.readFloat();
        if (reader.canRead() && reader.read() == ' ') {
            float y = reader.readFloat();
            return new Vec2(x, y);
        } else {
            reader.setCursor(i);
            throw ERROR_NOT_COMPLETE.createWithContext(reader);
        }
    }


    public static PositionAndRotation[] getPath(final CommandContext<?> context, final String name) {
        return context.getArgument(name, PositionAndRotation[].class);
    }

    public static PathArgumentType path() {
        return new PathArgumentType();
    }
}
