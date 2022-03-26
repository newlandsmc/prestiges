package me.cookie.prestiges.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.archyx.aureliumskills.AureliumSkills
import com.archyx.aureliumskills.api.AureliumAPI
import com.archyx.aureliumskills.skills.Skills
import me.cookie.cookiecore.formatMinimessage
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.Node
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@CommandAlias("prestige")
class Prestige(private val plugin: JavaPlugin, private val aureliumSkills: AureliumSkills): BaseCommand() {

    private val luckPerms: LuckPerms = LuckPermsProvider.get()

    @Default
    @Syntax("prestige")
    @Description("Command used to reset your skills for an XP boost.")
    private fun prestige(
        sender: Player
    ) {
        var totalLevel = 0
        Skills.values().forEach {
            totalLevel += AureliumAPI.getSkillLevel(sender, it)
        }

        if(totalLevel < plugin.config.getInt("min-level")) {
            sender.sendMessage("<red>You do not have enough levels to prestige.".formatMinimessage())
            return
        }

        var boost = totalLevel / 100.0

        sender.sendMessage(totalLevel.toString())

        Skills.values().forEach {
            aureliumSkills.playerManager.getPlayerData(sender)!!.setSkillLevel(it, 1)
        }

        aureliumSkills.leveler.updatePermissions(sender)
        aureliumSkills.leveler.updateStats(sender)

        var currentBoost = 0.0

        sender.effectivePermissions.forEach {
            if(it.permission.startsWith("aureliumskills.multiplier.")) {
                currentBoost = it.permission.split("aureliumskills.multiplier.")[1].toDouble()
                sender.sendMessage("<red>Current boost: $currentBoost".formatMinimessage())
                luckPerms.userManager.modifyUser(sender.uniqueId) { user ->
                    user.data().remove(Node.builder(it.permission).build())
                }
            }
        }

        boost += currentBoost

        luckPerms.userManager.modifyUser(sender.uniqueId) {
            it.data().add(Node.builder("aureliumskills.multiplier.$boost").build())
            sender.sendMessage("<green>You have been given a boost of $boost%.".formatMinimessage())
        }
    }

    @Subcommand("listperms")
    fun listPerms(sender: Player) {
        sender.effectivePermissions.forEach {
            sender.sendMessage("<yellow>${it.permission}".formatMinimessage())
        }
    }
}