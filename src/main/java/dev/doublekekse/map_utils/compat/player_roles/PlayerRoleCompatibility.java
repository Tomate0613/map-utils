package dev.doublekekse.map_utils.compat.player_roles;

import dev.gegy.roles.IdentifiableCommandSource;
import net.minecraft.commands.CommandSourceStack;

public class PlayerRoleCompatibility {
    public static void applyCommandIdentityType(CommandSourceStack stack) {
        ((IdentifiableCommandSource) stack).player_roles$setIdentityType(IdentifiableCommandSource.Type.COMMAND_BLOCK);
    }
}
