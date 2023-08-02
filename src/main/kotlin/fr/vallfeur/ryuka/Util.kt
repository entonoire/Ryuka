package fr.vallfeur.ryuka

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color

class Util {

    companion object {
        fun errorEmbed(description: String): MessageEmbed {
            val embed = EmbedBuilder()

            embed.setTitle(Main.config.getMessages().errorTitle)
            embed.setColor(Color(243, 45, 49))
            embed.setDescription(description)
            embed.setFooter(Main.jda.selfUser.name, Main.jda.selfUser.avatarUrl)

            return embed.build()
        }


        fun checkIdFormat(memberId: String): Boolean {
            return try { memberId.toLong(); true } catch (_: NumberFormatException) { false }

        }

        fun parseId(str: String): String {
            return str.replace("<@", "").replace(">", "").trim()

        }

        fun parseMessage(str: String, event: MessageReceivedEvent, vararg memberId: String): String {
            var content = str

            if (memberId.isNotEmpty()) {
                content = if (memberId.contains("notMention")) content.replace("\$member", event.guild.getMemberById(memberId[0])!!.effectiveName)
                          else                                 content.replace("\$member", event.guild.getMemberById(memberId[0])!!.asMention)

            }
            else if (str.contains("\$member")) System.err.println("a \$member declaration has been used on the sentence -> $str \nbut nothing ")

            return content.replace("\$authorName", event.author.name).replace("\$guildName", event.guild.name)

        }

    }

}