package club.rarlab.classicplugin.extension

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * Listen to a specific [Listener].
 */
fun JavaPlugin.listenTo(listener: Listener) = this.server.pluginManager.registerEvents(listener, this)