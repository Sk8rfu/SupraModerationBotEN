package com.mikubot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class ServerInfoCommand {

    public static void run(SlashCommandInteractionEvent event) {

        var guild = event.getGuild();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setTitle("📌 Server Information");

        embed.addField("Name", guild.getName(), false);
        embed.addField("ID", guild.getId(), false);
        embed.addField("Members", String.valueOf(guild.getMemberCount()), false);
        embed.setThumbnail(guild.getIconUrl());

        event.replyEmbeds(embed.build()).queue();
    }
}
