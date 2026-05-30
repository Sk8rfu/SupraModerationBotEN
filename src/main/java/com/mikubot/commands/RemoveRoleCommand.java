package com.mikubot.commands;

import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class RemoveRoleCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();
        Role role = event.getOption("role").getAsRole();

        // --- Prevent removing @everyone ---
        if (role.isPublicRole()) {
            event.reply("❌ You cannot remove the @everyone role.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!event.getMember().hasPermission(Permission.MANAGE_ROLES)) {
            event.reply("❌ You do not have permission to manage roles.")
                    .setEphemeral(true).queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(role)) {
            event.reply("❌ I cannot remove this role (it is higher than my role).")
                    .setEphemeral(true).queue();
            return;
        }

        event.getGuild().removeRoleFromMember(target, role).queue();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle("🗑 Role Removed");
        embed.addField("User", target.getAsMention(), false);
        embed.addField("Role", role.getAsMention(), false);
        embed.addField("Moderator", event.getUser().getAsMention(), false);

        event.replyEmbeds(embed.build()).queue();
        LogUtil.log(event.getGuild(), embed);
    }
}
