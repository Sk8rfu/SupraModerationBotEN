package com.mikubot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class InviteCommand {

    public static void run(SlashCommandInteractionEvent event) {

        String expiresRaw = event.getOption("expires").getAsString(); // 1s, 5m, 2h, 1w
        int uses = event.getOption("uses").getAsInt();

        TextChannel channel = event.getChannel().asTextChannel();

        // --- Conversion ---
        int expires = parseTime(expiresRaw);

        if (expires < 0) {
            event.reply("❌ Invalid format! Use: `1s`, `1m`, `1h`, `1w`")
                    .setEphemeral(true).queue();
            return;
        }

        channel.createInvite()
                .setMaxAge(expires)   // seconds
                .setMaxUses(uses)     // number of uses
                .queue(invite -> {

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.GREEN);
                    embed.setTitle("🔗 Invite Created");
                    embed.addField("Invite", invite.getUrl(), false);
                    embed.addField("Expires In", formatTime(expiresRaw), false);
                    embed.addField("Uses", uses == 0 ? "Unlimited" : String.valueOf(uses), false);

                    event.replyEmbeds(embed.build()).queue();
                });
    }

    // --- Convert 1s / 1m / 1h / 1w to seconds ---
    private static int parseTime(String input) {
        try {
            input = input.toLowerCase();

            if (input.endsWith("s")) {
                return Integer.parseInt(input.replace("s", ""));
            }
            if (input.endsWith("m")) {
                return Integer.parseInt(input.replace("m", "")) * 60;
            }
            if (input.endsWith("h")) {
                return Integer.parseInt(input.replace("h", "")) * 3600;
            }
            if (input.endsWith("w")) {
                return Integer.parseInt(input.replace("w", "")) * 604800;
            }

            return -1; // invalid format

        } catch (Exception e) {
            return -1;
        }
    }

    // --- Human‑readable text for embed ---
    private static String formatTime(String input) {
        input = input.toLowerCase();

        if (input.endsWith("s")) return input.replace("s", "") + " seconds";
        if (input.endsWith("m")) return input.replace("m", "") + " minutes";
        if (input.endsWith("h")) return input.replace("h", "") + " hours";
        if (input.endsWith("d")) return input.replace("d", "") + " days";
        if (input.endsWith("w")) return input.replace("w", "") + " weeks";

        return "Unknown";
    }
}
