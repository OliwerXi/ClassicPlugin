package club.rarlab.classicplugin.utility

import club.rarlab.classicplugin.ClassicPlugin
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin

/**
 * Class to handle listeners.
 */
class EventListener(private val listener: Event.() -> Unit) : EventExecutor, Listener {
    override fun execute(ignored: Listener, event: Event) = listener(event)
}

/**
 * Listen to a specific [Event].
 *
 * @param priority of which the listener should be set to.
 * @param listener consumer to be invoked upon execution.
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> JavaPlugin.listenTo(
        priority: EventPriority = EventPriority.NORMAL,
        noinline listener: T.() -> Unit
) = EventListener(listener as Event.() -> Unit).also {
    Bukkit.getPluginManager().registerEvent(T::class.java, it, priority, it, ClassicPlugin.INSTANCE)
}