package club.rarlab.classicplugin.cooldown

import club.rarlab.classicplugin.cooldown.CooldownHandler.CooldownException.Type.ALREADY_EXISTS
import club.rarlab.classicplugin.cooldown.CooldownHandler.CooldownException.Type.NON_EXISTENCE
import club.rarlab.classicplugin.task.schedule
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.time.Instant

/**
 * Singleton to handle all [BasicCooldown] based helper functions.
 */
object CooldownHandler {
    /**
     * [HashMap] of all existing cooldowns.
     */
    private val cooldowns: HashMap<String, BasicCooldown> = hashMapOf()

    /**
     * Monitor all of the available cooldowns.
     */
    fun monitor(): BukkitTask = schedule(0, 20, async = true, then = Runnable {
        val now = Instant.now()
        for (cooldown in cooldowns.values) {
            val entries = cooldown.entries().filter { it.value.time.isBefore(now) }
            if (entries.isEmpty()) continue
            entries.forEach { (uuid, entry) -> cooldown.depart(uuid, entry.end) }
        }
    })

    /**
     * Register a [BasicCooldown] to our map.
     *
     * @param name     of the cooldown to register.
     * @param cooldown object to be referred to.
     * @param then     action to happen when the cooldown is successfully registered.
     * @throws [CooldownException] if a cooldown already exists by the passed name.
     */
    @Throws(CooldownException::class)
    fun register(name: String, cooldown: BasicCooldown, then: (BasicCooldown) -> Unit = {}) {
        then(cooldowns.compute(name) { _, old ->
            if (old != null) {
                throw CooldownException(ALREADY_EXISTS, name)
            }

            return@compute cooldown
        } ?: return)
    }

    /**
     * Unregister a [BasicCooldown] by it's name.
     *
     * @param name of the cooldown to unregister.
     * @param then action to happen when the cooldown is successfully unregistered.
     * @throws [CooldownException] if a cooldown doesn't exist by the passed name.
     */
    @Throws(CooldownException::class)
    fun unregister(name: String, then: (BasicCooldown) -> Unit = {}) {
        cooldowns.remove(name)?.also(then) ?: throw CooldownException(NON_EXISTENCE, name)
    }

    /**
     * Add a [BasicCooldown] to a specific [Player].
     *
     * @param player whom to apply a cooldown to.
     * @param name   of the cooldown to be applied.
     * @param then   action to happen when the cooldown is applied.
     * @param onEnd  action to happen when the player's cooldown expires.
     */
    fun setTo(player: Player, name: String, then: () -> Unit = {}, onEnd: () -> Unit = {}) {
        this.getCooldown(name).supply(player, then, onEnd)
    }

    /**
     * Depart a [Player] from a specific [BasicCooldown].
     *
     * @param player whom to depart from the cooldown.
     * @param then   action to happen at cooldown removal.
     */
    fun depart(player: Player, name: String, then: () -> Unit = {}) {
        this.getCooldown(name).depart(player, then)
    }

    /**
     * Fetch a [Player]'s time left.
     *
     * @param player     whom's time to fetch.
     * @return [Instant] corresponding object, nullable.
     */
    fun fetch(player: Player, name: String): Instant? = this.getCooldown(name).getTime(player)

    /**
     * Get whether or not a [Player] has the corresponding cooldown.
     *
     * @param player     whom to check.
     * @param name       of the cooldown to check for.
     * @return [Boolean] whether or not they're present.
     */
    fun contains(player: Player, name: String): Boolean = this.getCooldown(name).contains(player)

    /**
     * Get a [BasicCooldown] object by it's name.
     *
     * @param name of the cooldown to fetch.
     * @return [BasicCooldown] corresponding object.
     * @throws [CooldownException] if the cooldown is not present.
     */
    @Throws(CooldownException::class)
    private fun getCooldown(name: String): BasicCooldown = cooldowns[name] ?: throw CooldownException(NON_EXISTENCE, name)

    /**
     * Class to handle [BasicCooldown] exceptions.
     *
     * @param type     of message to be thrown with the [Exception].
     * @param cooldown name of the cooldown involved in the event.
     */
    private class CooldownException(type: Type, cooldown: String) : Exception(type.message.format(cooldown)) {
        enum class Type(val message: String) {
            ALREADY_EXISTS("Cooldown with name '%s' already exists!"),
            NON_EXISTENCE("Cooldown with name '%s' does not exist!")
        }
    }
}