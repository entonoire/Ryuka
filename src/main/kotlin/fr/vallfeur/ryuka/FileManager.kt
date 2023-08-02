package fr.vallfeur.ryuka

import fr.vallfeur.ryuka.commands.Blr
import fr.vallfeur.ryuka.commands.TempMute
import fr.vallfeur.ryuka.commands.Warn
import java.io.File
import java.io.Writer
import java.nio.charset.Charset
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class FileManager {



    companion object {
        /*
            used to generate a random text separator (to avoid any "i found his separator lol" shut up -_-)
         */
        private val MuteRts = rts()
        private val WarnRts = rts()
        private val BlrRts = rts()

        private fun rts(): String {
            val random = Random()
            val buffer = StringBuilder(3)

            repeat(3) {
                val codePoint = random.nextInt(0x10000)
                buffer.appendCodePoint(codePoint)

            }

            return buffer.toString()
        }


        fun tempMuteUpdate() {
            val file = File("tempmutes")
            val writer = file.bufferedWriter()

            writer.write("$MuteRts\n")

            TempMute.mutes.forEach { (memberId, list) ->
                writer.write("$memberId$MuteRts${(list[0] as Calendar).time.toInstant().epochSecond}$MuteRts${list[1]}$MuteRts${list[2]}$MuteRts${list[3]}\n")

            }

            writer.close()

        }
        fun tempMutePush() {
            val file = File("tempmutes")

            if (file.length() != 0L) {
                val oldRts = file.readLines()[0]

                file.readLines().forEach {
                    if (it != oldRts) {
                        val line = it.split(oldRts)

                        val calendar = Calendar.getInstance()
                        calendar.time = Date.from(Instant.ofEpochSecond(line[1].toLong()))

                        TempMute.mutes[line[0]] = listOf(calendar, line[2], line[3], line[4].toLong())

                    }

                }

            }



        }

        fun warnUpdate() {
            val file = File("warns")
            val writer = file.bufferedWriter()

            writer.write("$WarnRts\n")

            Warn.warns.forEach { (memberId, list) ->
                val sb = StringBuilder()

                list.forEach {
                    it.forEach { str ->
                        sb.append("$str$WarnRts")

                    }

                }

                writer.write("$memberId$WarnRts${sb.removeSuffix(WarnRts)}\n")

            }

            writer.close()

        }
        fun warnPush() {
            val file = File("warns")

            if (file.length() != 0L) {
                val oldRts = file.readLines()[0]

                file.readLines().forEach { str ->
                    if (str != oldRts) {
                        val line = str.split(oldRts)
                        val memberId = line[0]

                        var count = 0
                        val memberList: MutableList<List<String>> = ArrayList()
                        val tempList: MutableList<String> = ArrayList()
                        line.forEach {
                            if (it != memberId) {
                                tempList.add(it)

                                if (count == 2) {
                                    memberList.add(tempList.toList())
                                    tempList.clear()
                                    count = 0
                                }
                                else count++

                            }

                        }

                        Warn.warns[memberId] = memberList
                    }
                }
            }
        }

        fun blrUpdate() {
            val file = File("blr")
            val writer = file.bufferedWriter()

            writer.write("$BlrRts\n")

            Blr.list.forEach { (memberId, list) ->
                writer.write("$memberId$BlrRts${list[0]}$BlrRts${list[1]}$BlrRts${list[2]}\n")

            }

            writer.close()

        }
        fun blrPush() {
            val file = File("blr")

            if (file.length() != 0L) {
                val oldRts = file.readLines()[0]

                file.readLines().forEach {
                    if (it != oldRts) {
                        val line = it.split(oldRts)

                        Blr.list[line[0]] = listOf(line[1], line[2], line[3])

                    }

                }

            }



        }

    }

}