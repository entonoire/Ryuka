package fr.vallfeur.ryuka

class YamlConfig {
    private var prefix: String = ""
    private var token: String = ""
    private var guild: String = ""
    private var color: List<Int> = emptyList()
    private var roles: Roles = Roles()
    private var tempmutePermission: String = ""
    private var messages: Messages = Messages()

    fun getPrefix(): String { return prefix }
    fun setPrefix(prefix: String) { this.prefix = prefix }

    fun getToken(): String { return token }
    fun setToken(token: String) { this.token = token }

    fun getGuild(): String { return guild }
    fun setGuild(guild: String) { this.guild = guild }

    fun getColor(): List<Int> { return color }
    fun setColor(color: List<Int>) { this.color = color }

    fun getRoles(): Roles { return roles }
    fun setRoles(roles: Roles) { this.roles = roles }

    fun getTempmutePermission(): String { return tempmutePermission }
    fun setTempmutePermission(tempmutePermission: String) { this.tempmutePermission = tempmutePermission }

    fun getMessages(): Messages { return messages }
    fun setMessages(messages: Messages) { this.messages = messages }

}


data class Roles(
    var permi: String = "",
    var permii: String = "",
    var permiii: String = "",
    var permiv: String = "",
    var permv: String = "",
    var mute: String = "",

)

data class Messages(
    var runnedBy: String = "",
    var errorTitle: String = "",
    var idFormat: String = "",
    var memberNotFound: String = "",
    var invalidUnit: String = "",
    var pmEmbed: String = "",
    var tempmute: TempMuteMessages = TempMuteMessages(),
    var tempmutelist: TempMuteListMessages = TempMuteListMessages(),
    var untempmute: UnTempMuteMessages = UnTempMuteMessages(),
    var help: HelpMessages = HelpMessages(),
    var warnAdd: WarnAddMessages = WarnAddMessages(),
    var warnList: WarnListMessages = WarnListMessages(),
    var warnRemove: WarnRemoveMessages = WarnRemoveMessages(),
    var buttons: ButtonsMessages = ButtonsMessages(),
    var blr: BlrMessages = BlrMessages(),

)

data class TempMuteMessages(
    var alreadyMute: String = "",
    var resultTitle: String = "",
    var resultDescription: String = "",
    var invalidDuration: String = "",
    var privateUnmuted: String = "",

)
data class TempMuteListMessages(
    var noMemberTempmute: String = "Aucun membre est temp mute !",
    var memberNotMute: String = "",
    var memberResultTitle: String = "",
    var resultFirstFieldName: String = "",
    var resultSecondFieldName: String = "",
    var resultThirdFieldName: String = "",
    var resultFourthFieldName: String = "",
    var resultTitle: String = "",
    var resultToLong: String = "",

)
data class UnTempMuteMessages(
    var memberUnknownButFound: String = "",
    var memberNotFound: String = "",
    var result: String = "",
    var notMute: String = "",

)

data class HelpMessages(
    var title: String = "",

)

data class WarnAddMessages(
    var resultTitle: String = "",
    var resultFirstField: String = "",
    var resultSecondField: String = "",

)
data class WarnListMessages(
    var resultTitle: String = "",
    var noWarnRegistered: String = "",

)
data class WarnRemoveMessages(
    var resultTitle: String = "",
    var idFormat: String = "",
    var wrongId: String = "",

)

data class ButtonsMessages(
    var notMessageAuthor: String = "",
    var userNotFound: String = "",
    var userWarnsNotFound: String = "",

)

data class BlrMessages(
    var resultTitle: String = "",
    var pmResult: String = "",
    var notFoundInList: String = "",
    var showResultTitle: String = "",
    var showNoOne: String = "",
    var showResultSecondTitle: String = "",
    var removeResultTitle: String = "",

)