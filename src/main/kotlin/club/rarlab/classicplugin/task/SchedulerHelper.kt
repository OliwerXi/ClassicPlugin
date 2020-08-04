package club.rarlab.classicplugin.task

import club.rarlab.classicplugin.ClassicPlugin.Companion.INSTANCE
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

/**
 * Object that contains helper methods for [BukkitScheduler].
 */
object SchedulerHelper {
    /**
     * [BukkitScheduler] instance.
     */
    private val scheduler: BukkitScheduler = Bukkit.getScheduler()

    /**
     * Run a delayed task synchronously.
     *
     * @param plugin [org.bukkit.plugin.java.JavaPlugin] instance.
     * @param delay  the delay to wait.
     * @param then   action to be triggered.
     */
    fun scheduleSync(plugin: Plugin = INSTANCE, delay: Long, then: Runnable): BukkitTask = scheduler
            .runTaskLater(plugin, then, delay)

    /**
     * Run a repeating task synchronously.
     *
     * @param plugin [org.bukkit.plugin.java.JavaPlugin] instance.
     * @param delay  the delay to wait until start.
     * @param period the period to wait before the action runs again.
     * @param then   action to be triggered periodically.
     */
    fun scheduleSync(plugin: Plugin = INSTANCE, delay: Long, period: Long, then: Runnable): BukkitTask = scheduler
            .runTaskTimer(plugin, then, delay, period)

    /**
     * Run a delayed task asynchronously.
     *
     * @param plugin [org.bukkit.plugin.java.JavaPlugin] instance.
     * @param delay  the delay to wait.
     * @param then   action to be triggered.
     */
    fun scheduleAsync(plugin: Plugin = INSTANCE, delay: Long, then: Runnable): BukkitTask = scheduler
            .runTaskLaterAsynchronously(plugin, then, delay)

    /**
     * Run a repeating task asynchronously.
     *
     * @param plugin [org.bukkit.plugin.java.JavaPlugin] instance.
     * @param delay  the delay to wait until start.
     * @param period the period to wait before the action runs again.
     * @param then   action to be triggered periodically.
     */
    fun scheduleAsync(plugin: Plugin = INSTANCE, delay: Long, period: Long, then: Runnable): BukkitTask = scheduler
            .runTaskTimerAsynchronously(plugin, then, delay, period)
}