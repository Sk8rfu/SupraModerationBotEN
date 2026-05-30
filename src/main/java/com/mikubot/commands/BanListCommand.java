package com.mikubot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class BanListCommand {

    public static void run(SlashCommandInteractionEvent event) {

        // Permission check
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("❌ You do not have permission to view the ban list.")
                    .setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();

        guild.retrieveBanList().queue(bans -> {

            if (bans.isEmpty()) {
                event.reply("✅ There are no banned users in this server.")
                        .setEphemeral(true).queue();
                return;
            }

            // Embed for the list
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("🔨 Banned Users List");
            embed.setTimestamp(Instant.now());
            embed.setFooter("Total banned: " + bans.size());

            StringBuilder description = new StringBuilder();

            for (Guild.Ban ban : bans) {
                User user = ban.getUser();
                String reason = ban.getReason() != null
                        ? ban.getReason()
                        : "❌ No reason provided";

                description.append("**")
                        .append(user.getAsTag())
                        .append("** (`")
                        .append(user.getId())
                        .append("`)\n")
                        .append("📝 Reason: ")
                        .append(reason)
                        .append("\n")
                        .append("🔗 [Profile](https://discord.com/users/")
                        .append(user.getId())
                        .append(")\n\n");
            }

            embed.setDescription(description.toString());

            event.replyEmbeds(embed.build()).queue();
        });
    }
}
