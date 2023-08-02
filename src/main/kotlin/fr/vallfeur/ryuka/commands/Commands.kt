package fr.vallfeur.ryuka.commands

import fr.vallfeur.ryuka.Main
import fr.vallfeur.ryuka.YamlConfig
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.TimeUnit

/*

    -tempmute <id> time unit reason
    -warn <member> <reason>
    -blr

    perm 1 : warn
    perm 2 : warn, mute
    perm + & ban_members : warn, mute, blr

 */

class Commands : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.isFromType(ChannelType.TEXT)) {
            val args = if (event.message.contentRaw.replace(event.message.contentRaw.split(" ")[0], "").trim().isEmpty())  listOf()
                       else event.message.contentRaw.replace(event.message.contentRaw.split(" ")[0], "").trim().split(" ")

            when (event.message.contentRaw.split(" ")[0]) {
                "${Main.config.getPrefix()}help" -> Help(args, event)

                "${Main.config.getPrefix()}rl" -> {
                    if (event.member?.isOwner == true) {
                        Main.config = Yaml().loadAs(FileInputStream(File("config.yaml")), YamlConfig::class.java)

                        event.channel.sendMessage("config reload !").queue() {
                            it.delete().queueAfter(2, TimeUnit.SECONDS)
                            event.message.delete().queueAfter(2, TimeUnit.SECONDS)

                        }

                    }

                }

                "${Main.config.getPrefix()}tempmute" -> if (checkTempMutePerm(event.member!!)) TempMute(args, event)
                "${Main.config.getPrefix()}tempmutelist" -> if (checkTempMutePerm(event.member!!)) TempMuteList(args, event)
                "${Main.config.getPrefix()}untempmute" -> if (checkTempMutePerm(event.member!!)) UnTempMute(args, event)

                "${Main.config.getPrefix()}warn" -> Warn.init(args, event)

                "${Main.config.getPrefix()}blr" -> Blr(args, event)

            }



        }

    }

    private fun checkTempMutePerm(member: Member): Boolean {
        return (member.hasPermission(Permission.valueOf(Main.config.getTempmutePermission())) && member.roles.any { listOf(
            Main.config.getRoles().permii,
            Main.config.getRoles().permiii,
            Main.config.getRoles().permiv,
            Main.config.getRoles().permv
        ).contains(it.id) })

    }

}