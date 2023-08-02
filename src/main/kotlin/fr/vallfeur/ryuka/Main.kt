package fr.vallfeur.ryuka

import fr.vallfeur.ryuka.commands.BlrEvent
import fr.vallfeur.ryuka.commands.Commands
import fr.vallfeur.ryuka.commands.TempMute
import fr.vallfeur.ryuka.commands.Warn
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.yaml.snakeyaml.Yaml
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class Main() : EventListener {

    companion object {
        lateinit var jda: JDA
        var config: YamlConfig = Yaml().loadAs(FileInputStream(File("config.yaml")), YamlConfig::class.java)

        val color = Color(config.getColor()[0], config.getColor()[1], config.getColor()[2])

        fun start() {
            val jda = JDABuilder.createLight(config.getToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build()

            jda.addEventListener(Main(), Commands(), Warn(), BlrEvent())


            /*
                tempmute scheduler
             */
            val executor = Executors.newSingleThreadScheduledExecutor()
            executor.scheduleAtFixedRate({
                val currentTime = Calendar.getInstance()

                val iterator = TempMute.mutes.iterator()
                while (iterator.hasNext()) {
                    val entry = iterator.next()
                    val memberId = entry.key

                    val expirationTime = entry.value[0] as Calendar

                    if (currentTime.after(expirationTime)) {
                        /* has expire */

                        val guild = jda.getGuildById(config.getGuild())
                        jda.getUserById(memberId)?.openPrivateChannel()?.queue { channel ->
                            if (guild != null) {
                                guild.getRoleById(config.getRoles().mute)?.let { guild.removeRoleFromMember(UserSnowflake.fromId(memberId), it).queue() }

                                channel.sendMessageEmbeds(
                                    EmbedBuilder()
                                        .setTitle(config.getMessages().pmEmbed)
                                        .setDescription(config.getMessages().tempmute.privateUnmuted.replace("\$authorName", channel.user!!.name))
                                        .setTimestamp(Calendar.getInstance().toInstant())
                                        .setColor(color)
                                        .setFooter(jda.selfUser.name, jda.selfUser.avatarUrl)
                                        .build()
                                ).queue()

                            }

                        }

                        iterator.remove()
                        FileManager.tempMuteUpdate()

                    }

                }

            }, 0, 500, TimeUnit.MILLISECONDS) // 0.5s

        }

    }

    override fun onEvent(event: GenericEvent) {
        if (event is ReadyEvent) {
            println("[Ryuka] logged as ${event.jda.selfUser.name}")
            jda = event.jda

            FileManager.tempMutePush()
            FileManager.warnPush()
            FileManager.blrPush()

            // delay or else gateway problem
            Thread {
                Thread.sleep(500)

                TempMute.mutes.forEach { (memberId) ->
                    jda.getGuildById(config.getGuild())!!.addRoleToMember(UserSnowflake.fromId(memberId), jda.getRoleById(config.getRoles().mute)!!).queue()

                }

            }.start()

        }

    }

}

fun main() {
    if (File("config.yaml").exists()) {
        Main.start()

    }
    else {
        System.err.println("Error config not found!")
        exitProcess(0)

    }

}