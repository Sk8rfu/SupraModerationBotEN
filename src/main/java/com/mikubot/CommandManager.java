package com.mikubot;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        event.getJDA().updateCommands().addCommands(

                // 📘 Information
                Commands.slash("ping", "Shows the bot latency"),
                Commands.slash("help", "Displays all available commands"),
                Commands.slash("userinfo", "Shows information about a user")
                        .addOption(OptionType.USER, "user", "User to check", false),
                Commands.slash("serverinfo", "Shows information about the server"),
                Commands.slash("avatar", "Displays a user's avatar")
                        .addOption(OptionType.USER, "user", "User to check", false),
                Commands.slash("about", "Information about the bot"),

                // 🛡 Moderation
                Commands.slash("ban", "Bans a user")
                        .addOption(OptionType.USER, "user", "User to ban", true)
                        .addOption(OptionType.STRING, "reason", "Reason for the ban", false),

                Commands.slash("tempban", "Temporarily bans a user")
                        .addOption(OptionType.USER, "user", "User to ban", true)
                        .addOption(OptionType.STRING, "time", "Duration (1s, 1m, 1h, 1d, 1w)", true)
                        .addOption(OptionType.STRING, "reason", "Reason for the ban", false),

                Commands.slash("unban", "Removes a ban from a user")
                        .addOption(OptionType.STRING, "userid", "User ID or username", true),

                Commands.slash("banid", "Bans a user by ID")
                        .addOption(OptionType.STRING, "userid", "User ID", true)
                        .addOption(OptionType.STRING, "reason", "Reason for the ban", false),

                Commands.slash("kick", "Kicks a user")
                        .addOption(OptionType.USER, "user", "User to kick", true)
                        .addOption(OptionType.STRING, "reason", "Reason for the kick", false),

                Commands.slash("mute", "Mutes a user (timeout)")
                        .addOption(OptionType.USER, "user", "User to mute", true)
                        .addOption(OptionType.INTEGER, "minutes", "Duration in minutes", false)
                        .addOption(OptionType.STRING, "reason", "Reason for the mute", false),

                Commands.slash("banlist", "Shows a list of banned users"),

                Commands.slash("unmute", "Unmutes a user (timeout)")
                        .addOption(OptionType.USER, "user", "User to unmute", true),

                Commands.slash("muterole", "Mutes a user using a role")
                        .addOption(OptionType.USER, "user", "User to mute", true)
                        .addOption(OptionType.STRING, "reason", "Reason for the mute", false),

                Commands.slash("unmuterole", "Unmutes a user using a role")
                        .addOption(OptionType.USER, "user", "User to unmute", true),

                Commands.slash("warn", "Issues a warning to a user")
                        .addOption(OptionType.USER, "user", "User to warn", true)
                        .addOption(OptionType.STRING, "reason", "Reason for the warning", false),

                Commands.slash("unwarn", "Clears all warnings from a user")
                        .addOption(OptionType.USER, "user", "User to clear warnings from", true),

                Commands.slash("warnings", "Shows all warnings for a user")
                        .addOption(OptionType.USER, "user", "User to check", true),

                // ⚙ Management
                Commands.slash("giverole", "Gives a role to a user")
                        .addOption(OptionType.USER, "user", "User to give the role to", true)
                        .addOption(OptionType.ROLE, "role", "Role to give", true),

                Commands.slash("removerole", "Removes a role from a user")
                        .addOption(OptionType.USER, "user", "User to remove the role from", true)
                        .addOption(OptionType.ROLE, "role", "Role to remove", true),

                Commands.slash("createrole", "Creates a new role")
                        .addOption(OptionType.STRING, "name", "Role name", true)
                        .addOption(OptionType.STRING, "color", "Color (RGB, HEX, or name)", true),

                Commands.slash("editrole", "Edits an existing role")
                        .addOption(OptionType.ROLE, "role", "Role to edit", true)
                        .addOption(OptionType.STRING, "name", "New name", false)
                        .addOption(OptionType.STRING, "color", "New color (RGB, HEX, or name)", false),

                Commands.slash("deleterole", "Deletes a role")
                        .addOption(OptionType.ROLE, "role", "Role to delete", true),

                Commands.slash("clear", "Deletes messages")
                        .addOption(OptionType.INTEGER, "amount", "Number of messages to delete", true),

                Commands.slash("slowmode", "Sets slowmode for the channel")
                        .addOption(OptionType.INTEGER, "seconds", "Seconds (0–21600)", true),

                Commands.slash("lock", "Locks the channel (disables sending messages)"),
                Commands.slash("unlock", "Unlocks the channel (enables sending messages)"),

                // 🟢 Nickname
                Commands.slash("nickname", "Changes a user's nickname")
                        .addOption(OptionType.USER, "user", "User to modify", true)
                        .addOption(OptionType.STRING, "name", "New nickname", false)
                        .addOption(OptionType.BOOLEAN, "reset", "Reset nickname", false),

                // 💬 Social
                Commands.slash("report", "Sends a report to the moderators")
                        .addOption(OptionType.STRING, "message", "Report message", true),

                Commands.slash("suggest", "Sends a suggestion to the server")
                        .addOption(OptionType.STRING, "suggestion", "Your suggestion", true),

                Commands.slash("invite", "Creates a server invite")
                        .addOption(OptionType.STRING, "expires", "Duration (e.g., 10s, 5m, 1h, 1d, 1w)", true)
                        .addOption(OptionType.INTEGER, "uses", "Number of uses (0 = unlimited)", true)

        ).queue();
    }
}
