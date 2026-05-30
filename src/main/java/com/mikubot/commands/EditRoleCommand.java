package com.mikubot.commands;

import com.mikubot.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class EditRoleCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Role role = event.getOption("role").getAsRole();
        String newName = event.getOption("name") != null ? event.getOption("name").getAsString() : null;
        String colorInput = event.getOption("color") != null ? event.getOption("color").getAsString().toLowerCase() : null;

        var moderator = event.getMember();
        var bot = event.getGuild().getSelfMember();

        // --- Checks ---
        if (!moderator.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ You do not have permission to edit roles.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ I do not have permission to edit roles.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!moderator.canInteract(role)) {
            event.reply("❌ You cannot edit a role higher than your own.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.canInteract(role)) {
            event.reply("❌ I cannot edit a role higher than mine.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Check for existing role name ---
        if (newName != null) {
            Role existing = event.getGuild().getRolesByName(newName, true)
                    .stream().findFirst().orElse(null);

            if (existing != null && !existing.getId().equals(role.getId())) {
                event.reply("❌ A role named **" + newName + "** already exists.")
                        .setEphemeral(true).queue();
                return;
            }
        }

        // --- Parse color ---
        Color color = null;
        if (colorInput != null) {
            color = ColorUtils.parseColor(colorInput);
            if (color == null) {
                event.reply("❌ Invalid color! Use RGB, HEX, or a color name.")
                        .setEphemeral(true).queue();
                return;
            }
        }

        // --- Apply changes ---
        role.getManager()
                .setName(newName != null ? newName : role.getName())
                .setColor(color != null ? color : role.getColor())
                .queue();

        // --- Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(color != null ? color : role.getColor());
        embed.setTitle("🎨 Role Edited");
        embed.addField("Role", role.getAsMention(), false);
        if (newName != null) embed.addField("New Name", newName, false);
        if (colorInput != null) embed.addField("New Color", colorInput, false);

        event.replyEmbeds(embed.build()).queue();
    }
}
