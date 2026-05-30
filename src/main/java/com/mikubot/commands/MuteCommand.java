package com.mikubot.commands;

import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class MuteCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();
        String reason = event.getOption("reason") != null
                ? event.getOption("reason").getAsString()
                : "No reason provided";

        int minutes = event.getOption("minutes") != null
                ? event.getOption("minutes").getAsInt()
                : 10;

        Member moderator = event.getMember();
        Member bot = event.getGuild().getSelfMember();

        // --- Checks ---
        if (!moderator.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ You do not have permission to mute members.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ I do not have permission to mute members.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target == null) {
            event.reply("❌ I cannot find that user.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ I cannot mute an administrator.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!moderator.canInteract(target)) {
            event.reply("❌ You cannot mute a user with a higher role than yours.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.canInteract(target)) {
            event.reply("❌ I cannot mute a user with a higher role than mine.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- DM Embed ---
        EmbedBuilder dmEmbed = new EmbedBuilder();
        dmEmbed.setColor(Color.YELLOW);
        dmEmbed.setTitle("🔇 You Have Been Muted!");
        dmEmbed.setDescription("You are receiving this message because you were temporarily muted.");
        dmEmbed.addField("🏰 Server", event.getGuild().getName(), false);
        dmEmbed.addField("⏳ Duration", minutes + " minutes", false);
        dmEmbed.addField("👑 Moderator", moderator.getUser().getAsTag(), false);
        dmEmbed.addField("📝 Reason", reason, false);
        dmEmbed.setThumbnail(moderator.getUser().getAvatarUrl());
        dmEmbed.setTimestamp(Instant.now());

        // --- Send DM + fallback ---
        target.getUser().openPrivateChannel().queue(
                dm -> dm.sendMessageEmbeds(dmEmbed.build()).queue(
                        success -> {},
                        error -> event.reply("⚠️ Failed to send DM — the user may have DMs disabled.")
                                .setEphemeral(true)
                                .queue()
                ),
                error -> event.reply("⚠️ Failed to send DM — the user may have DMs disabled.")
                        .setEphemeral(true)
                        .queue()
        );

        // --- Timeout ---
        target.timeoutFor(Duration.ofMinutes(minutes)).reason(reason).queue();

        // --- Public Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle("🔇 User Muted");

        embed.addField("👤 User", target.getAsMention(), false);
        embed.addField("🆔 ID", target.getId(), false);
        embed.addField("⏳ Duration", minutes + " minutes", false);
        embed.addField("📝 Reason", reason, false);
        embed.addField("👑 Moderator", moderator.getAsMention(), false);

        embed.setTimestamp(Instant.now());
        embed.setFooter("Action performed by " + moderator.getUser().getAsTag(),
                moderator.getUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
        LogUtil.log(event.getGuild(), embed);
    }
}
