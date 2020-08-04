package club.rarlab.classicplugin.task

import club.rarlab.classicplugin.task.SchedulerHelper.scheduleAsync
import club.rarlab.classicplugin.task.SchedulerHelper.scheduleSync
import org.bukkit.scheduler.BukkitTask

/**
 * Schedule sync or async delayed task.
 *
 * @param delay the delay to wait.
 * @param async whether or not the task should be asynchronous.
 * @param then  action to be triggered.
 */
fun schedule(delay: Long, async: Boolean = false, then: Runnable): BukkitTask =
        if (!async) scheduleSync(delay, then) else scheduleAsync(delay, then)

/**
 * Schedule a synchronous or asynchronous repeating task.
 *
 * @param delay  the delay to wait until start.
 * @param period the period to wait before the action runs again.
 * @param async  whether or not the task should be asynchronous.
 * @param then   action to be triggered periodically.
 */
fun schedule(delay: Long, period: Long, async: Boolean = false, then: Runnable): BukkitTask =
        if (!async) scheduleSync(delay, period, then) else scheduleAsync(delay, period, then)