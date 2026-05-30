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

public class BanCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();
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

        if (target.getId().equals(event.getGuild().getOwnerId())) {
            event.reply("❌ You cannot ban the server owner.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target.getId().equals(moderator.getId())) {
            event.reply("❌ You cannot ban yourself.")
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

        // --- DM Embed ---
        EmbedBuilder dmEmbed = new EmbedBuilder();
        dmEmbed.setColor(Color.RED);
        dmEmbed.setTitle("🔨 You Have Been Banned!");
        dmEmbed.setDescription("You are receiving this message because you were banned from a server.");
        dmEmbed.addField("🏰 Server", event.getGuild().getName(), false);
        dmEmbed.addField("👑 Moderator", moderator.getUser().getAsTag(), false);
        dmEmbed.addField("📝 Reason", reason, false);
        dmEmbed.setThumbnail(moderator.getUser().getAvatarUrl());
        dmEmbed.setTimestamp(Instant.now());

        // --- Send DM (ignore failures silently) ---
        target.getUser().openPrivateChannel().queue(
                dm -> dm.sendMessageEmbeds(dmEmbed.build()).queue(
                        success -> {},
                        failure -> {} // ignore DM failure
                ),
                error -> {} // ignore DM open failure
        );

        // --- Ban ---
        event.getGuild()
                .ban(UserSnowflake.fromId(target.getId()), 0, TimeUnit.SECONDS)
                .reason(reason)
                .queue();

        // --- Public Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("🔨 User Banned");

        embed.addField("👤 User", target.getAsMention(), false);
        embed.addField("🆔 ID", target.getId(), false);
        embed.addField("👑 Moderator", moderator.getAsMention(), false);
        embed.addField("📝 Reason", reason, false);

        embed.setTimestamp(Instant.now());
        embed.setFooter("Action performed by " + moderator.getUser().getAsTag(),
                moderator.getUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
        LogUtil.log(event.getGuild(), embed);
    }
}
