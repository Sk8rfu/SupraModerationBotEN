package com.mikubot.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class MuteUtil {

    public static Role getOrCreateMuteRole(Guild guild) {

        Role muteRole = guild.getRolesByName("Muted", true)
                .stream().findFirst().orElse(null);

        if (muteRole != null)
            return muteRole;

        // Create the role
        muteRole = guild.createRole()
                .setName("Muted")
                .setPermissions(Permission.EMPTY_PERMISSIONS)
                .complete();

        // Make it final for lambda usage
        Role finalMuteRole = muteRole;

        // Deny sending messages in all text channels
        guild.getTextChannels().forEach(channel -> {
            channel.upsertPermissionOverride(finalMuteRole)
                    .deny(Permission.MESSAGE_SEND)
                    .queue();
        });

        return muteRole;
    }
}
