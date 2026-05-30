package com.mikubot.commands;

import com.mikubot.utils.ColorUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class CreateRoleCommand {

    public static void run(SlashCommandInteractionEvent event) {

        String roleName = event.getOption("name").getAsString();
        String colorInput = event.getOption("color").getAsString().toLowerCase();

        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ You do not have permission to create roles.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Check if a role with the same name already exists ---
        Role existing = event.getGuild().getRolesByName(roleName, true)
                .stream().findFirst().orElse(null);

        if (existing != null) {
            event.reply("❌ A role named **" + roleName + "** already exists!")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Attempt to parse the color ---
        Color color = ColorUtils.parseColor(colorInput);

        if (color == null) {
            event.reply("❌ Invalid color! Use one of the following formats:\n" +
                    "• RGB: `255 0 128`\n" +
                    "• Hex: `#ff00aa`\n" +
                    "• Name: `red`, `blue`, `green`, `yellow`, etc.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Create the role ---
        event.getGuild().createRole()
                .setName(roleName)
                .setColor(color)
                .queue(role -> {

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(color);
                    embed.setTitle("🎨 Role Created");
                    embed.addField("Name", role.getAsMention(), false);
                    embed.addField("Color", colorInput, false);

                    event.replyEmbeds(embed.build()).queue();
                });
    }
}
