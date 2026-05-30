package com.mikubot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class DeleteRoleCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Role role = event.getOption("role").getAsRole();
        var moderator = event.getMember();
        var bot = event.getGuild().getSelfMember();

        // --- Checks ---
        if (role.isPublicRole()) {
            event.reply("❌ You cannot delete the @everyone role.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!moderator.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ You do not have permission to delete roles.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ I do not have permission to delete roles.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!moderator.canInteract(role)) {
            event.reply("❌ You cannot delete a role higher than your own.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.canInteract(role)) {
            event.reply("❌ I cannot delete a role higher than mine.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Prepare embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("🗑 Role Deleted");
        embed.addField("Role", role.getName(), false);

        // --- Correct order: reply first, then delete ---
        event.replyEmbeds(embed.build()).queue(success -> {
            role.delete().queue();
        });
    }
}
