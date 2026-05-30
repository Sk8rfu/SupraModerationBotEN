package com.mikubot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ClearCommand {

    public static void run(SlashCommandInteractionEvent event) {

        int amount = event.getOption("amount").getAsInt();

        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reply("❌ You do not have permission to delete messages.")
                    .setEphemeral(true).queue();
            return;
        }

        if (amount < 1) {
            event.reply("❌ You must delete **at least 1** message.")
                    .setEphemeral(true).queue();
            return;
        }

        if (amount > 100) {
            event.reply("❌ You can delete **up to 100** messages at once.")
                    .setEphemeral(true).queue();
            return;
        }

        event.getChannel().asTextChannel().getHistory().retrievePast(amount).queue(messages -> {

            if (messages.isEmpty()) {
                event.reply("❌ There are no messages to delete.")
                        .setEphemeral(true).queue();
                return;
            }

            if (messages.size() == 1) {
                // If there is only 1 message → delete individually
                event.getChannel().asTextChannel()
                        .deleteMessageById(messages.get(0).getId()).queue();

                event.reply("🧹 **1** message has been deleted.")
                        .setEphemeral(true).queue();
                return;
            }

            // If there are 2–100 messages → bulk delete
            event.getChannel().asTextChannel().deleteMessages(messages).queue(
                    success -> event.reply("🧹 **" + messages.size() + "** messages have been deleted.")
                            .setEphemeral(true).queue(),
                    error -> event.reply("❌ I cannot delete some messages (they may be older than 14 days).")
                            .setEphemeral(true).queue()
            );
        });
    }
}
