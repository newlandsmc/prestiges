package me.cookie.prestiges

import co.aikar.commands.BukkitCommandManager
import me.cookie.prestiges.commands.Prestige
import org.bukkit.plugin.java.JavaPlugin

class Prestiges: JavaPlugin() {
    override fun onEnable() {
        val manager = BukkitCommandManager(this)

        manager.registerCommand(Prestige(this))

        saveDefaultConfig()
    }
}