package fr.vallfeur.ryuka.commands

import fr.vallfeur.ryuka.FileManager
import fr.vallfeur.ryuka.Main
import fr.vallfeur.ryuka.Util
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.Calendar

class UnTempMute(args: List<String>, event: MessageReceivedEvent) {

    init {
        if (args.isEmpty()) {
            event.channel.sendMessageEmbeds(Util.errorEmbed("${Main.config.getPrefix()}untempmute <member>")).queue()

        }
        else {
            val memberId = args[0].replace("<@", "").replace(">", "").trim()

            if (!Util.checkIdFormat(memberId)) {
                event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().idFormat)).queue()

            }
            else {
                if (!event.guild.isMember(UserSnowflake.fromId(memberId))) {
                    if (TempMute.mutes.contains(memberId)) {
                        TempMute.mutes.remove(memberId)
                        FileManager.tempMuteUpdate()

                        event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().untempmute.memberUnknownButFound)).queue()

                    }
                    else {
                        event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().untempmute.memberNotFound)).queue()

                    }

                }
                else {
                    if (TempMute.mutes.contains(memberId)) {
                        event.guild.removeRoleFromMember(UserSnowflake.fromId(memberId), event.guild.getRoleById(Main.config.getRoles().mute)!!).queue()

                        TempMute.mutes.remove(memberId)
                        FileManager.tempMuteUpdate()

                        event.channel.sendMessageEmbeds(EmbedBuilder()
                            .setTitle(Util.parseMessage(Main.config.getMessages().untempmute.result, event, memberId))
                            .setColor(Main.color)
                            .setTimestamp(Calendar.getInstance().toInstant())
                            .setFooter(Util.parseMessage(Main.config.getMessages().runnedBy, event), event.jda.selfUser.avatarUrl)
                            .build()
                        ).queue() {
                            event.guild.getMemberById(memberId)!!.user.openPrivateChannel().queue() {
                                it.sendMessageEmbeds(
                                    EmbedBuilder()
                                        .setTitle(Main.config.getMessages().pmEmbed)
                                        .setDescription(Util.parseMessage(Main.config.getMessages().tempmute.privateUnmuted, event, memberId))
                                        .setTimestamp(Calendar.getInstance().toInstant())
                                        .setColor(Main.color)
                                        .setFooter(event.jda.selfUser.name, event.jda.selfUser.avatarUrl)
                                        .build()

                                ).queue()

                            }

                        }



                    }
                    else {
                        event.channel.sendMessageEmbeds(Util.errorEmbed(Main.config.getMessages().untempmute.notMute)).queue()

                    }

                }

            }

        }

    }

}