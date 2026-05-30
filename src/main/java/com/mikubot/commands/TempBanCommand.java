package com.mikubot.commands;

import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TempBanCommand {

    // Scheduler for delayed tasks
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void run(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();
        String timeRaw = event.getOption("time").getAsString();
        String reason = event.getOption("reason") != null
                ? event.getOption("reason").getAsString()
                : "No reason provided";

        Member moderator = event.getMember();
        Member bot = event.getGuild().getSelfMember();

        // --- Checks ---
        if (!moderator.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("❌ You do not have permission to ban users.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("❌ I do not have permission to ban users.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target == null) {
            event.reply("❌ I cannot find that user.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ I cannot ban an administrator.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!moderator.canInteract(target)) {
            event.reply("❌ You cannot ban a user with a higher role than yours.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.canInteract(target)) {
            event.reply("❌ I cannot ban a user with a higher role than mine.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Parse time ---
        long seconds = parseTime(timeRaw);
        if (seconds <= 0) {
            event.reply("❌ Invalid format! Use: `1s`, `1m`, `1h`, `1d`, `1w`")
                    .setEphemeral(true).queue();
            return;
        }

        // --- DM Embed ---
        EmbedBuilder dmEmbed = new EmbedBuilder();
        dmEmbed.setColor(Color.RED);
        dmEmbed.setTitle("⏳ Temporary Ban");
        dmEmbed.setDescription("You are receiving this message because you were temporarily banned from a server.");
        dmEmbed.addField("🏰 Server", event.getGuild().getName(), false);
        dmEmbed.addField("⏳ Duration", formatTime(timeRaw), false);
        dmEmbed.addField("👑 Moderator", moderator.getUser().getAsTag(), false);
        dmEmbed.addField("📝 Reason", reason, false);
        dmEmbed.setThumbnail(moderator.getUser().getAvatarUrl());
        dmEmbed.setTimestamp(Instant.now());

        // --- DM + silent fallback ---
        target.getUser().openPrivateChannel().queue(
                dm -> dm.sendMessageEmbeds(dmEmbed.build()).queue(
                        success -> {},
                        failure -> {}
                ),
                error -> {}
        );

        // --- Ban ---
        event.getGuild()
                .ban(UserSnowflake.fromId(target.getId()), 0, TimeUnit.SECONDS)
                .reason(reason)
                .queue();

        // --- Scheduled unban ---
        scheduler.schedule(() -> {

            // 1) Automatic unban
            event.getGuild().unban(UserSnowflake.fromId(target.getId()))
                    .reason("Tempban duration expired")
                    .queue();

            // 2) DM on unban
            event.getJDA().retrieveUserById(target.getId()).queue(user -> {

                EmbedBuilder unbanDM = new EmbedBuilder();
                unbanDM.setColor(Color.GREEN);
                unbanDM.setTitle("🔓 Tempban Expired");
                unbanDM.setDescription("Your temporary ban in **" + event.getGuild().getName() + "** has ended.");
                unbanDM.addField("⏳ Duration", formatTime(timeRaw), false);
                unbanDM.setTimestamp(Instant.now());

                user.openPrivateChannel().queue(
                        dm -> dm.sendMessageEmbeds(unbanDM.build()).queue(
                                success -> {},
                                failure -> {}
                        ),
                        error -> {}
                );
            });

            // 3) Log entry
            EmbedBuilder logEmbed = new EmbedBuilder();
            logEmbed.setColor(Color.GREEN);
            logEmbed.setTitle("🔓 Tempban Expired");
            logEmbed.setDescription("The user was automatically unbanned after the duration expired.");
            logEmbed.addField("👤 User", target.getUser().getAsTag() + " (`" + target.getId() + "`)", false);
            logEmbed.addField("⏳ Duration", formatTime(timeRaw), false);
            logEmbed.addField("🏰 Server", event.getGuild().getName(), false);
            logEmbed.setTimestamp(Instant.now());

            LogUtil.log(event.getGuild(), logEmbed);

        }, seconds, TimeUnit.SECONDS);

        // --- Public Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("⏳ Temporary Ban");

        embed.addField("👤 User", target.getAsMention(), false);
        embed.addField("🆔 ID", target.getId(), false);
        embed.addField("⏳ Duration", formatTime(timeRaw), false);
        embed.addField("📝 Reason", reason, false);
        embed.addField("👑 Moderator", moderator.getAsMention(), false);

        embed.setTimestamp(Instant.now());
        embed.setFooter("Action performed by " + moderator.getUser().getAsTag(),
                moderator.getUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
        LogUtil.log(event.getGuild(), embed);
    }

    // --- Parse time ---
    private static long parseTime(String input) {
        input = input.toLowerCase();

        try {
            if (input.endsWith("s")) return Long.parseLong(input.replace("s", ""));
            if (input.endsWith("m")) return Long.parseLong(input.replace("m", "")) * 60;
            if (input.endsWith("h")) return Long.parseLong(input.replace("h", "")) * 3600;
            if (input.endsWith("d")) return Long.parseLong(input.replace("d", "")) * 86400;
            if (input.endsWith("w")) return Long.parseLong(input.replace("w", "")) * 604800;
        } catch (Exception ignored) {}

        return -1;
    }

    // --- Human‑readable text ---
    private static String formatTime(String input) {
        input = input.toLowerCase();

        if (input.endsWith("s")) return input.replace("s", "") + " seconds";
        if (input.endsWith("m")) return input.replace("m", "") + " minutes";
        if (input.endsWith("h")) return input.replace("h", "") + " hours";
        if (input.endsWith("d")) return input.replace("d", "") + " days";
        if (input.endsWith("w")) return input.replace("w", "") + " weeks";

        return "Unknown";
    }
}
