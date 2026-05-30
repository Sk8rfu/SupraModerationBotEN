package com.mikubot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class HelpCommand {

    public static void run(SlashCommandInteractionEvent event) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("📘 Help Menu");
        embed.setColor(Color.BLUE);

        embed.setDescription("All commands are organized into categories for easier navigation.");

        // 🟦 Information Commands
        embed.addField("📘 Information",
                """
                **/ping** – Shows latency
                **/help** – Displays this menu
                **/userinfo** – Shows user information
                **/serverinfo** – Shows server information
                **/avatar** – Displays a user's avatar
                **/about** – Information about the bot
                """,
                false);

        // 🟥 Moderation
        embed.addField("🛡 Moderation",
                """
                **/ban** – Bans a user
                **/tempban** – Temporary ban (1s, 1m, 1h, 1d, 1w)
                **/unban** – Removes a ban
                **/banid** – Bans a user by ID
                **/banlist** – Shows the list of banned users
                **/kick** – Kicks a user
                **/mute** – Mutes (timeout)
                **/unmute** – Unmutes (timeout)
                **/muterole** – Mutes using a role
                **/unmuterole** – Unmutes using a role
                **/warn** – Issues a warning
                **/unwarn** – Clears all warnings
                **/warnings** – Shows a user's warnings
                """,
                false);

        // 🟩 Server Management
        embed.addField("⚙ Management",
                """
                **/giverole** – Gives a role
                **/removerole** – Removes a role
                **/createrole** – Creates a role
                **/editrole** – Edits a role
                **/deleterole** – Deletes a role
                **/clear** – Deletes messages
                **/slowmode** – Sets slowmode
                **/lock** – Locks a channel
                **/unlock** – Unlocks a channel
                **/nickname** – Changes a nickname
                **/invite** – Creates an invite (1s, 1m, 1h, 1d, 1w)
                """,
                false);

        // 🟨 Social / Communication
        embed.addField("💬 Social",
                """
                **/report** – Sends a report to the moderators
                **/suggest** – Sends a suggestion
                """,
                false);

        embed.setFooter("SupraModerationBot • Help Menu",
                event.getJDA().getSelfUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
    }
}
