package club.rarlab.classicplugin.extension

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin

/**
 * Class to handle corresponding [Listener] in terms of [Event]s.
 */
class ClassicListener(private val listener: Event.() -> Unit) : EventExecutor, Listener {
    /**
     * Execute an [Event] in the [Listener].
     */
    override fun execute(ignored: Listener, event: Event) = listener(event)
}

/**
 * Listen to a specific [Event].
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T: Event> JavaPlugin.listenTo(
        priority: EventPriority = EventPriority.NORMAL,
        noinline listener: T.() -> Unit
) = ClassicListener(listener as Event.() -> Unit).also {
    this.server.pluginManager.registerEvent(T::class.java, it, priority, it, this)
}