package com.mikubot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class NicknameCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member moderator = event.getMember();
        Member bot = event.getGuild().getSelfMember();

        Member target = event.getOption("user").getAsMember();

        // name may be null!
        String name = event.getOption("name") != null
                ? event.getOption("name").getAsString().trim()
                : null;

        // reset may be null!
        boolean reset = event.getOption("reset") != null
                && event.getOption("reset").getAsBoolean();

        // Moderator permission check
        if (!moderator.hasPermission(Permission.NICKNAME_MANAGE)) {
            event.reply("❌ You do not have permission to change nicknames.")
                    .setEphemeral(true).queue();
            return;
        }

        // Bot permission check
        if (!bot.hasPermission(Permission.NICKNAME_MANAGE)) {
            event.reply("❌ I do not have permission to change nicknames.")
                    .setEphemeral(true).queue();
            return;
        }

        // Bot interaction check
        if (!bot.canInteract(target)) {
            event.reply("❌ I cannot change this user's nickname (their role is higher than mine).")
                    .setEphemeral(true).queue();
            return;
        }

        // Moderator interaction check
        if (!moderator.canInteract(target)) {
            event.reply("❌ You cannot change the nickname of a user with a higher role than yours.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- RESET ---
        if (reset) {

            if (target.getNickname() == null) {
                event.reply("ℹ " + target.getAsMention() + " does not have a nickname to reset.")
                        .setEphemeral(true).queue();
                return;
            }

            target.modifyNickname(null).queue();
            event.reply("♻ " + target.getAsMention() + "'s nickname has been reset to their original username.")
                    .queue();
            return;
        }

        // --- If no reset and no name provided ---
        if (name == null || name.isEmpty()) {
            event.reply("❌ You must provide a new nickname or use the reset option.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Change nickname ---
        target.modifyNickname(name).queue();
        event.reply("✏ " + target.getAsMention() + "'s nickname has been changed to **" + name + "**.")
                .queue();
    }
}
