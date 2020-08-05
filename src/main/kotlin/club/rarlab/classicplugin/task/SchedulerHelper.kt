package club.rarlab.classicplugin.task

import club.rarlab.classicplugin.ClassicPlugin.Companion.INSTANCE
import org.bukkit.Bukkit
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
     * Run a task synchronously.
     *
     * @param then action to be triggered.
     */
    fun scheduleSync(then: Runnable): BukkitTask = scheduler
            .runTask(INSTANCE, then)

    /**
     * Run a delayed task synchronously.
     *
     * @param delay the delay to wait.
     * @param then  action to be triggered.
     */
    fun scheduleSync(delay: Long, then: Runnable): BukkitTask = scheduler
            .runTaskLater(INSTANCE, then, delay)

    /**
     * Run a repeating task synchronously.
     *
     * @param delay  the delay to wait until start.
     * @param period the period to wait before the action runs again.
     * @param then   action to be triggered periodically.
     */
    fun scheduleSync(delay: Long, period: Long, then: Runnable): BukkitTask = scheduler
            .runTaskTimer(INSTANCE, then, delay, period)

    /**
     * Run a task asynchronously.
     *
     * @param then action to be triggered.
     */
    fun scheduleAsync( then: Runnable): BukkitTask = scheduler
            .runTaskAsynchronously(INSTANCE, then)

    /**
     * Run a delayed task asynchronously.
     *
     * @param delay the delay to wait.
     * @param then  action to be triggered.
     */
    fun scheduleAsync(delay: Long, then: Runnable): BukkitTask = scheduler
            .runTaskLaterAsynchronously(INSTANCE, then, delay)

    /**
     * Run a repeating task asynchronously.
     *
     * @param delay  the delay to wait until start.
     * @param period the period to wait before the action runs again.
     * @param then   action to be triggered periodically.
     */
    fun scheduleAsync(delay: Long, period: Long, then: Runnable): BukkitTask = scheduler
            .runTaskTimerAsynchronously(INSTANCE, then, delay, period)
}