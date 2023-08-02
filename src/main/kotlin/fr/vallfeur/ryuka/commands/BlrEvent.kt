package fr.vallfeur.ryuka.commands

import fr.vallfeur.ryuka.Main
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

class BlrEvent : ListenerAdapter() {

    override fun onGuildMemberRoleAdd(event: GuildMemberRoleAddEvent) {
        if (Blr.list.contains(event.member.id) && event.roles.size == 1 && event.roles[0].id != Main.config.getRoles().permi) {
            event.member.roles.forEach {
                if (it.id != Main.config.getRoles().permi) event.guild.removeRoleFromMember(UserSnowflake.fromId(event.member.id), it).queue()

            }

            if (!event.member.roles.contains(event.guild.getRoleById(Main.config.getRoles().permi)))
                event.guild.addRoleToMember(UserSnowflake.fromId(event.member.id), event.guild.getRoleById(Main.config.getRoles().permi)!!).queue()

            event.guild.retrieveAuditLogs().queue { logs ->
                logs.last().user?.openPrivateChannel()?.queue { channel ->
                    channel.sendMessageEmbeds(EmbedBuilder()
                        .setTitle(Main.config.getMessages().blr.pmResult
                            .replace("\$member",
                                event.member.effectiveName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
                            .replace("\$perm", event.guild.getRoleById(Main.config.getRoles().permi)!!.name)
                        )
                        .setColor(Main.color)
                        .setFooter(event.jda.selfUser.name, event.jda.selfUser.avatarUrl)
                        .build()
                    ).queue()

                }

            }

        }

    }

}
