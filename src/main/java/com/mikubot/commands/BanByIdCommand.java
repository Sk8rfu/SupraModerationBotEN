package com.mikubot.commands;

import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class BanByIdCommand {

    public static void run(SlashCommandInteractionEvent event) {

        String userId = event.getOption("userid").getAsString();
        String reason = event.getOption("reason") != null
                ? event.getOption("reason").getAsString()
                : "No reason provided";

        Member moderator = event.getMember();
        Member bot = event.getGuild().getSelfMember();

        // --- Permission checks ---
        if (!moderator.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("❌ You do not have permission to ban members.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("❌ I do not have permission to ban members.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!userId.matches("\\d{17,20}")) {
            event.reply("❌ Invalid user ID.")
                    .setEphemeral(true).queue();
            return;
        }

        if (userId.equals(event.getGuild().getOwnerId())) {
            event.reply("❌ You cannot ban the server owner.")
                    .setEphemeral(true).queue();
            return;
        }

        if (userId.equals(moderator.getId())) {
            event.reply("❌ You cannot ban yourself.")
                    .setEphemeral(true).queue();
            return;
        }

        if (userId.equals(bot.getId())) {
            event.reply("❌ I cannot ban myself.")
                    .setEphemeral(true).queue();
            return;
        }

        // Role hierarchy check if the user is in the server
        Member targetMember = event.getGuild().getMemberById(userId);
        if (targetMember != null) {

            if (targetMember.hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("❌ I cannot ban an administrator.")
                        .setEphemeral(true).queue();
                return;
            }

            if (!moderator.canInteract(targetMember)) {
                event.reply("❌ You cannot ban a user with a higher role than yours.")
                        .setEphemeral(true).queue();
                return;
            }

            if (!bot.canInteract(targetMember)) {
                event.reply("❌ I cannot ban a user with a higher role than mine.")
                        .setEphemeral(true).queue();
                return;
            }
        }

        // --- Ban (no DM) ---
        event.getGuild()
                .ban(UserSnowflake.fromId(userId), 0, TimeUnit.SECONDS)
                .reason(reason)
                .queue();

        // --- Public Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("🔨 Ban by ID");

        embed.addField("🆔 User ID", userId, false);
        embed.addField("👑 Moderator", moderator.getAsMention(), false);
        embed.addField("📝 Reason", reason, false);

        embed.setTimestamp(Instant.now());
        embed.setFooter("Action performed by " + moderator.getUser().getAsTag(),
                moderator.getUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
        LogUtil.log(event.getGuild(), embed);
    }
}
