package com.mikubot.commands;

import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;

public class UnbanCommand {

    public static void run(SlashCommandInteractionEvent event) {

        String rawInput = event.getOption("userid").getAsString();

        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("❌ You do not have permission to remove bans.")
                    .setEphemeral(true).queue();
            return;
        }

        // 1) If it's a mention → extract ID
        String cleanedInput = rawInput;
        if (cleanedInput.startsWith("<@") && cleanedInput.endsWith(">")) {
            cleanedInput = cleanedInput.replaceAll("[^0-9]", "");
        }

        // 2) If it's a raw ID → try directly
        if (cleanedInput.matches("\\d{17,20}")) {
            final String finalId = cleanedInput;
            unban(event, finalId);
            return;
        }

        // 3) If it's a username → search in ban list
        final String finalInput = cleanedInput;

        event.getGuild().retrieveBanList().queue(bans -> {

            User bannedUser = bans.stream()
                    .map(ban -> ban.getUser())
                    .filter(user ->
                            user.getName().equalsIgnoreCase(finalInput) ||
                            user.getAsTag().equalsIgnoreCase(finalInput)
                    )
                    .findFirst()
                    .orElse(null);

            if (bannedUser == null) {
                event.reply("❌ Could not find a banned user with name: **" + finalInput + "**")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            final String finalId = bannedUser.getId();
            unban(event, finalId);
        });
    }

    private static void unban(SlashCommandInteractionEvent event, String userId) {

        String moderatorTag = event.getUser().getAsTag();
        String moderatorAvatar = event.getUser().getAvatarUrl();

        event.getGuild().unban(UserSnowflake.fromId(userId)).queue(
                success -> {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.GREEN);
                    embed.setTitle("♻ Ban Removed");

                    embed.addField("👤 User ID", userId, false);
                    embed.addField("👑 Moderator", moderatorTag, false);
                    embed.setTimestamp(Instant.now());
                    embed.setFooter("Performed by " + moderatorTag, moderatorAvatar);

                    event.replyEmbeds(embed.build()).queue();
                    LogUtil.log(event.getGuild(), embed);
                },
                error -> event.reply("❌ No ban found for this user.")
                        .setEphemeral(true)
                        .queue()
        );
    }
}
