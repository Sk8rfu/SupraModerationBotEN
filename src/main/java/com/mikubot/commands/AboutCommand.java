package com.mikubot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class AboutCommand {

    public static void run(SlashCommandInteractionEvent event) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.MAGENTA);
        embed.setTitle("ℹ️ About SupraModeration");

        embed.setDescription("""
                🤖 **SupraModeration — Moderation & Management**
                A lightweight, fast, and reliable Discord bot built for modern servers.

                🔧 **Features**
                • Moderation (ban, mute, warnings)
                • Channel management (lock, unlock, slowmode)
                • Information commands (userinfo, serverinfo, ping)
                • Roles and nicknames
                • Logging system for all actions

                👑 **Creator:** Sk8rfu
                🌐 **Version:** 1.0.0
                """);

        embed.setFooter("SupraModerationBot • Made with ❤️ by Sk8rfu",
                event.getJDA().getSelfUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
    }
}
