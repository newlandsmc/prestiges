package me.cookie.prestiges

import co.aikar.commands.BukkitCommandManager
import com.archyx.aureliumskills.AureliumSkills
import me.cookie.prestiges.commands.Prestige
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable


class Prestiges: JavaPlugin() {
    override fun onEnable() {
        val manager = BukkitCommandManager(this)
        val plugin = this
        object: BukkitRunnable() {
            override fun run() {
                manager.registerCommand(Prestige(plugin, getPlugin(AureliumSkills::class.java)))
            }
        }.runTaskLater(this, 50)
        saveDefaultConfig()
    }
}