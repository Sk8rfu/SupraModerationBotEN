package com.mikubot.commands;

import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class GiveRoleCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member moderator = event.getMember();
        Member bot = event.getGuild().getSelfMember();

        Member target = event.getOption("user").getAsMember();
        Role role = event.getOption("role").getAsRole();

        // --- Prevent giving @everyone ---
        if (role.isPublicRole()) {
            event.reply("❌ You cannot assign the @everyone role.")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Permission checks ---
        if (!moderator.hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ You do not have permission to manage roles.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!moderator.canInteract(role)) {
            event.reply("❌ You cannot assign a role higher than your own.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!bot.canInteract(role)) {
            event.reply("❌ I cannot assign this role (it is higher than my role).")
                    .setEphemeral(true).queue();
            return;
        }

        // --- Assign role ---
        event.getGuild().addRoleToMember(target, role).queue();

        // --- Embed ---
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTitle("🎉 Role Assigned");
        embed.addField("User", target.getAsMention(), false);
        embed.addField("Role", role.getAsMention(), false);
        embed.addField("Moderator", moderator.getAsMention(), false);

        event.replyEmbeds(embed.build()).queue();
        LogUtil.log(event.getGuild(), embed);
    }
}
