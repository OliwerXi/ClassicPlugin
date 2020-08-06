package club.rarlab.classicplugin.cooldown

import club.rarlab.classicplugin.extension.then
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.Collections.unmodifiableMap
import java.util.concurrent.ConcurrentHashMap

/**
 * Abstract class for all Cooldown implementations.
 */
class BasicCooldown(private val duration: Duration) {
    /**
     * [HashMap] of all entries in the corresponding [BasicCooldown].
     */
    private val entries: ConcurrentHashMap<UUID, Entry> = ConcurrentHashMap()

    /**
     * Supply the entries with a new entry.
     *
     * @param player whom to supply the cooldown with.
     * @param then   action to happen after supplying the cooldown to a [Player].
     * @param onEnd  action to happen when the cooldown ends for a [Player].
     */
    fun supply(player: Player, then: () -> Unit = {}, onEnd: () -> Unit) {
        entries[player.uniqueId] = Entry(Instant.ofEpochMilli(System.currentTimeMillis() + duration.toMillis()), onEnd)
        then()
    }

    /**
     * Depart a [Player] from the entries by their [UUID].
     *
     * @param uuid of whom to depart from the cooldown entries.
     * @param then action to happen at entry removal.
     */
    fun depart(uuid: UUID, then: () -> Unit = {}) = entries.remove(uuid).then { then() }

    /**
     * Depart a [Player] from the entries.
     *
     * @param player whom to depart from the cooldown entries.
     * @param then   action to happen at entry removal.
     */
    fun depart(player: Player, then: () -> Unit = {}) = depart(player.uniqueId, then)

    /**
     * Get a [Player]'s cooldown entry.
     *
     * @param player   whom's entry to fetch.
     * @return [Entry] corresponding object, nullable.
     */
    fun getEntry(player: Player): Entry? = entries[player.uniqueId]

    /**
     * Get a [Player]'s time left.
     *
     * @param player     whom's time to fetch.
     * @return [Instant] corresponding object, nullable.
     */
    fun getTime(player: Player): Instant? = entries[player.uniqueId]?.time

    /**
     * Get whether or not a [Player] has the corresponding cooldown.
     *
     * @param player     whom to check.
     * @return [Boolean] whether or not they're present.
     */
    fun contains(player: Player): Boolean = getTime(player)?.isAfter(Instant.now()) ?: false

    /**
     * Get an immutable [Map] of all entries.
     *
     * @return [Map] of all entries.
     */
    fun entries(): Map<UUID, Entry> = unmodifiableMap(this.entries)

    /**
     * Data class to handle a [Player]'s time instant and end runnable.
     *
     * @param time the time of the day that the player's cooldown ends.
     * @param end  action to happen upon the player's cooldown ending.
     */
    data class Entry internal constructor(val time: Instant, val end: () -> Unit)
}