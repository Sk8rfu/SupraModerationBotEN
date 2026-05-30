package com.mikubot.commands;

import com.mikubot.util.WarnUtil;
import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class WarnCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();
        String reason = event.getOption("reason") != null
                ? event.getOption("reason").getAsString()
                : "No reason provided";

        Member moderator = event.getMember();
        Member bot = event.getGuild().getSelfMember();

        // --- Checks ---
        if (!moderator.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ You do not have permission to issue warnings.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ I do not have permission to warn users.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target == null) {
            event.reply("❌ I cannot find that user.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!moderator.canInteract(target)) {
            event.reply("❌ You cannot warn a user with a higher role than yours.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.canInteract(target)) {
            event.reply("❌ I cannot warn a user with a higher role than mine.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Save to JSON ---
        WarnUtil.addWarn(target.getId(), reason);

        // --- Automatic punishments ---
        int warnCount = WarnUtil.getWarns(target.getId()).size();
        String autoPunishment = "No automatic punishment.";

        if (warnCount == 3) {
            target.timeoutFor(Duration.ofMinutes(10))
                    .reason("Automatic mute at 3 warnings")
                    .queue();
            autoPunishment = "🔇 Automatic **mute** (3 warnings)";
        }

        if (warnCount == 5) {
            target.kick().reason("Automatic kick at 5 warnings").queue();
            autoPunishment = "👢 Automatic **kick** (5 warnings)";
        }

        if (warnCount == 7) {
            event.getGuild().ban(target, 0, TimeUnit.SECONDS)
                    .reason("Automatic ban at 7 warnings")
                    .queue();
            autoPunishment = "🔨 Automatic **ban** (7 warnings)";
        }

        // --- DM to the user ---
        target.getUser().openPrivateChannel().queue(
                dm -> dm.sendMessage(
                        "⚠ You received a warning in **" + event.getGuild().getName() + "**.\nReason: " + reason
                ).queue(
                        success -> {},
                        error -> {}
                ),
                error -> {}
        );

        // --- Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("⚠ Warning Issued");

        embed.addField("👤 User", target.getAsMention(), false);
        embed.addField("🆔 ID", target.getId(), false);
        embed.addField("📝 Reason", reason, false);
        embed.addField("📄 Total Warnings", String.valueOf(warnCount), false);
        embed.addField("⚙ Automatic Punishment", autoPunishment, false);
        embed.addField("👑 Moderator", moderator.getAsMention(), false);

        embed.setTimestamp(Instant.now());
        embed.setFooter("Issued by " + moderator.getUser().getAsTag(),
                moderator.getUser().getAvatarUrl());

        // --- Send to moderator ---
        event.replyEmbeds(embed.build()).queue();

        // --- Log in mod-logs ---
        LogUtil.log(event.getGuild(), embed);
    }
}
