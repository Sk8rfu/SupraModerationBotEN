package com.mikubot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class UnlockCommand {

    public static void run(SlashCommandInteractionEvent event) {

        if (!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply("❌ You do not have permission to unlock channels.")
                    .setEphemeral(true).queue();
            return;
        }

        var channel = event.getChannel().asTextChannel();
        var publicRole = event.getGuild().getPublicRole();

        PermissionOverride override = channel.getPermissionOverride(publicRole);

        if (override != null) {
            override.delete().queue(
                    success -> event.reply("🔓 The channel has been unlocked.").queue(),
                    error -> event.reply("❌ I cannot unlock the channel (no access to the override).")
                            .setEphemeral(true).queue()
            );
            return;
        }

        event.reply("🔓 The channel is already unlocked.").queue();
    }
}
