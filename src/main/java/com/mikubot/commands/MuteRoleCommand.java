package com.mikubot.commands;

import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;

public class MuteRoleCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();
        String reason = event.getOption("reason") != null
                ? event.getOption("reason").getAsString()
                : "No reason provided";

        Member moderator = event.getMember();
        Member bot = event.getGuild().getSelfMember();

        // --- Checks ---
        if (!moderator.hasPermission(Permission.MODERATE_MEMBERS)) {
            event.reply("❌ You do not have permission to mute users.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ I do not have permission to manage roles.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target == null) {
            event.reply("❌ I cannot find that user.")
                    .setEphemeral(true).queue();
            return;
        }

        if (target.hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("❌ I cannot mute an administrator using a role.")
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

        // --- Find or create Muted role ---
        Role muteRole = event.getGuild().getRolesByName("Muted", true)
                .stream().findFirst().orElse(null);

        if (muteRole == null) {
            muteRole = event.getGuild().createRole()
                    .setName("Muted")
                    .setPermissions(Permission.EMPTY_PERMISSIONS)
                    .complete();

            Role finalMuteRole = muteRole;

            event.getGuild().getTextChannels().forEach(channel ->
                    channel.upsertPermissionOverride(finalMuteRole)
                            .deny(Permission.MESSAGE_SEND)
                            .queue()
            );
        }

        // --- DM Embed ---
        EmbedBuilder dmEmbed = new EmbedBuilder();
        dmEmbed.setColor(Color.YELLOW);
        dmEmbed.setTitle("🔇 You Have Been Muted!");
        dmEmbed.setDescription("You are receiving this message because you were given the **Muted** role.");
        dmEmbed.addField("🏰 Server", event.getGuild().getName(), false);
        dmEmbed.addField("👑 Moderator", moderator.getUser().getAsTag(), false);
        dmEmbed.addField("📝 Reason", reason, false);
        dmEmbed.setThumbnail(moderator.getUser().getAvatarUrl());
        dmEmbed.setTimestamp(Instant.now());

        target.getUser().openPrivateChannel().queue(
                dm -> dm.sendMessageEmbeds(dmEmbed.build()).queue(
                        success -> {},
                        error -> {}
                ),
                error -> {}
        );

        // --- Add the role ---
        event.getGuild().addRoleToMember(target, muteRole).reason(reason).queue();

        // --- Public Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle("🔇 User Muted (Role)");

        embed.addField("👤 User", target.getAsMention(), false);
        embed.addField("🆔 ID", target.getId(), false);
        embed.addField("📝 Reason", reason, false);
        embed.addField("👑 Moderator", moderator.getAsMention(), false);

        embed.setTimestamp(Instant.now());
        embed.setFooter("Action performed by " + moderator.getUser().getAsTag(),
                moderator.getUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
        LogUtil.log(event.getGuild(), embed);
    }
}
