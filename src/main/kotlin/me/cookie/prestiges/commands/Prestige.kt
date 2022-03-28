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
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

@CommandAlias("prestige|prestiges")
class Prestige(private val plugin: JavaPlugin): BaseCommand() {
    private val luckPerms: LuckPerms = LuckPermsProvider.get()
    private lateinit var aureliumSkills: AureliumSkills

    init {
        object: BukkitRunnable() {
            override fun run() {
                aureliumSkills = AureliumAPI.getPlugin()
            }
        }.runTaskLater(plugin, 100)
    }

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

        val boost: Float = totalLevel / 100f

        sender.sendMessage(totalLevel.toString())

        Skills.values().forEach {
            aureliumSkills.playerManager.getPlayerData(sender)!!.setSkillLevel(it, 1)
        }

        aureliumSkills.leveler.updatePermissions(sender)
        aureliumSkills.leveler.updateStats(sender)

        setBoost(sender, boost, true)

    }

    @Subcommand("admin set")
    @CommandPermission("prestiges.admin")
    @CommandCompletion("@players @range:0-1000")
    fun adminSetBoost(
        sender: Player,
        target: String,
        level: Float,
    ) {
        val player = Bukkit.getPlayer(target)
        setBoost(player!!, level, false)

        aureliumSkills.leveler.updatePermissions(sender)
        aureliumSkills.leveler.updateStats(sender)
    }

    @Subcommand("admin add")
    @CommandPermission("prestiges.admin")
    @CommandCompletion("@players @range:0-1000")
    fun adminAddBoost(
        sender: Player,
        target: String,
        level: Float,
    ) {
        val player = Bukkit.getPlayer(target)
        setBoost(player!!, level, true)

        aureliumSkills.leveler.updatePermissions(sender)
        aureliumSkills.leveler.updateStats(sender)
    }

    private fun setBoost(target: Player, boost: Float, add: Boolean) {
        var currentBoost = 0.0f
        target.effectivePermissions.forEach {
            if(it.permission.startsWith("aureliumskills.multiplier.")) {
                if(add) currentBoost = it.permission.split("aureliumskills.multiplier.")[1].toFloat()

                target.sendMessage("<red>Current boost: $currentBoost".formatMinimessage())
                luckPerms.userManager.modifyUser(target.uniqueId) { user ->
                    user.data().remove(Node.builder(it.permission).build())
                }
            }
        }

        luckPerms.userManager.modifyUser(target.uniqueId) {
            it.data().add(Node.builder("aureliumskills.multiplier.${boost+currentBoost}").build())
            target.sendMessage("<green>You have been given a boost of ${boost+currentBoost}%.".formatMinimessage())
        }
    }
}