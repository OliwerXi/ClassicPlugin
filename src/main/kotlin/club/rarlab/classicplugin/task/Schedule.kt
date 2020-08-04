package club.rarlab.classicplugin.task

import club.rarlab.classicplugin.ClassicPlugin.Companion.INSTANCE
import club.rarlab.classicplugin.task.SchedulerHelper.scheduleAsync
import club.rarlab.classicplugin.task.SchedulerHelper.scheduleSync
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

/**
 * Schedule sync or async delayed task.
 *
 * @param plugin [org.bukkit.plugin.java.JavaPlugin] instance.
 * @param delay  the delay to wait.
 * @param async  whether or not the task should be asynchronous.
 * @param then   action to be triggered.
 */
fun schedule(plugin: Plugin = INSTANCE, delay: Long, async: Boolean = false, then: Runnable): BukkitTask =
        if (!async) scheduleSync(plugin, delay, then) else scheduleAsync(plugin, delay, then)

/**
 * Schedule a synchronous or asynchronous repeating task.
 *
 * @param plugin [org.bukkit.plugin.java.JavaPlugin] instance.
 * @param delay  the delay to wait until start.
 * @param period the period to wait before the action runs again.
 * @param async  whether or not the task should be asynchronous.
 * @param then   action to be triggered periodically.
 */
fun schedule(plugin: Plugin = INSTANCE, delay: Long, period: Long, async: Boolean = false, then: Runnable): BukkitTask =
        if (!async) scheduleSync(plugin, delay, period, then) else scheduleAsync(plugin, delay, period, then)