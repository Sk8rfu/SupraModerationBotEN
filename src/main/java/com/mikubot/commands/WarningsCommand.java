package com.mikubot.commands;

import com.mikubot.util.WarnUtil;
import com.mikubot.util.LogUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class WarningsCommand {

    public static void run(SlashCommandInteractionEvent event) {

        Member target = event.getOption("user").getAsMember();

        if (target == null) {
            event.reply("❌ I cannot find that user.").setEphemeral(true).queue();
            return;
        }

        List<String> warns = WarnUtil.getWarns(target.getId());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setTitle("📄 Warnings");

        embed.addField("👤 User", target.getAsMention(), false);
        embed.addField("🆔 ID", target.getId(), false);

        if (warns.isEmpty()) {
            embed.addField("⚠ No Warnings", "This user has no recorded warnings.", false);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < warns.size(); i++) {
                sb.append("**").append(i + 1).append(".** ").append(warns.get(i)).append("\n");
            }
            embed.addField("⚠ Warnings", sb.toString(), false);
        }

        embed.setTimestamp(Instant.now());
        embed.setFooter("Warnings Overview");

        event.replyEmbeds(embed.build()).queue();

        // --- Log in mod-logs ---
        LogUtil.log(event.getGuild(), embed);
    }
}
