package com.mikubot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class SuggestCommand {

    public static void run(SlashCommandInteractionEvent event) {

        String suggestion = event.getOption("suggestion").getAsString();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.CYAN);
        embed.setTitle("💡 New Suggestion");
        embed.addField("From", event.getUser().getAsMention(), false);
        embed.addField("Suggestion", suggestion, false);

        event.replyEmbeds(embed.build()).queue();
    }
}
