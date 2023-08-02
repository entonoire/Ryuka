package fr.vallfeur.ryuka.commands

import fr.vallfeur.ryuka.Main
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Help(args: List<String>, event: MessageReceivedEvent) {

    init {
        event.channel.sendMessageEmbeds(EmbedBuilder()
            .setTitle(Main.config.getMessages().help.title)
            .setColor(Main.color)
            .setDescription("**Tempmute**\n")
            .appendDescription("${Main.config.getPrefix()}tempmute `<utilisateur>` `<durée>` `<s | m | h | j>` `<raison>`\n")
            .appendDescription("${Main.config.getPrefix()}tempmutelist `[utilisateur]`\n")
            .appendDescription("${Main.config.getPrefix()}untempmute `<utilisateur>`\n")
            .appendDescription("    \n")
            .appendDescription("**Test**\n")
            .setFooter("${event.jda.selfUser.name} - Made by le_vallfeur", event.jda.selfUser.avatarUrl)
            .build()
        ).queue()

    }

}