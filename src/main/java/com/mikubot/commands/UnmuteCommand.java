package com.mikubot.commands;

import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;

public class UnmuteCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();
        Member moderator = event.getMember();
        Member bot = event.getGuild().getSelfMember();

        // --- Checks ---
        if (!moderator.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ You do not have permission to unmute users.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ I do not have permission to unmute users.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target == null) {
            event.reply("❌ I cannot find that user.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("ℹ️ Administrators cannot be muted, so there is nothing to unmute.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!moderator.canInteract(target)) {
            event.reply("❌ You cannot unmute a user with a higher role than yours.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.canInteract(target)) {
            event.reply("❌ I cannot unmute a user with a higher role than mine.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- DM Embed ---
        EmbedBuilder dmEmbed = new EmbedBuilder();
        dmEmbed.setColor(Color.GREEN);
        dmEmbed.setTitle("🔊 You Have Been Unmuted!");
        dmEmbed.setDescription("You can now speak again in the server.");
        dmEmbed.addField("🏰 Server", event.getGuild().getName(), false);
        dmEmbed.addField("👑 Moderator", moderator.getUser().getAsTag(), false);
        dmEmbed.setThumbnail(moderator.getUser().getAvatarUrl());
        dmEmbed.setTimestamp(Instant.now());

        target.getUser().openPrivateChannel().queue(
                dm -> dm.sendMessageEmbeds(dmEmbed.build()).queue(
                        success -> {},
                        error -> {}
                ),
                error -> {}
        );

        // --- Remove timeout ---
        target.removeTimeout().queue();

        // --- Public Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTitle("🔊 User Unmuted");

        embed.addField("👤 User", target.getAsMention(), false);
        embed.addField("🆔 ID", target.getId(), false);
        embed.addField("👑 Moderator", moderator.getAsMention(), false);

        embed.setTimestamp(Instant.now());
        embed.setFooter("Performed by " + moderator.getUser().getAsTag(),
                moderator.getUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
        LogUtil.log(event.getGuild(), embed);
    }
}
