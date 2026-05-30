package com.mikubot.commands;

import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;

public class KickCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();
        String reason = event.getOption("reason") != null
                ? event.getOption("reason").getAsString()
                : "No reason provided";

        Member moderator = event.getMember();
        Member bot = event.getGuild().getSelfMember();

        // --- Checks ---
        if (!moderator.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("❌ You do not have permission to kick members.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.hasPermission(Permission.KICK_MEMBERS)) {
            event.reply("❌ I do not have permission to kick members.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target == null) {
            event.reply("❌ I cannot find that user.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ I cannot kick an administrator.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target.getId().equals(event.getGuild().getOwnerId())) {
            event.reply("❌ You cannot kick the server owner.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target.getId().equals(moderator.getId())) {
            event.reply("❌ You cannot kick yourself.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!moderator.canInteract(target)) {
            event.reply("❌ You cannot kick a user with a higher role than yours.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.canInteract(target)) {
            event.reply("❌ I cannot kick a user with a higher role than mine.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- DM Embed to the user ---
        EmbedBuilder dmEmbed = new EmbedBuilder();
        dmEmbed.setColor(Color.ORANGE);
        dmEmbed.setTitle("👢 You Have Been Kicked!");
        dmEmbed.setDescription("You are receiving this message because you were kicked from a server.");
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

        // --- Kick ---
        target.kick().reason(reason).queue();

        // --- Public Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setTitle("👢 User Kicked");

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
