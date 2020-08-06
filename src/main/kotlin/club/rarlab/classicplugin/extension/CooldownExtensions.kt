package club.rarlab.classicplugin.extension

import club.rarlab.classicplugin.cooldown.BasicCooldown
import club.rarlab.classicplugin.cooldown.CooldownHandler.contains
import club.rarlab.classicplugin.cooldown.CooldownHandler.depart
import club.rarlab.classicplugin.cooldown.CooldownHandler.fetch
import club.rarlab.classicplugin.cooldown.CooldownHandler.setTo
import org.bukkit.entity.Player
import java.time.Instant

/**
 * Apply a [BasicCooldown] to a [Player].
 * @see [club.rarlab.classicplugin.cooldown.CooldownHandler.setTo]
 */
fun Player.setCooldown(name: String, then: () -> Unit = {}, onEnd: () -> Unit = {}) = setTo(this, name, then, onEnd)

/**
 * Remove a [BasicCooldown] from a [Player].
 * @see [club.rarlab.classicplugin.cooldown.CooldownHandler.depart]
 */
fun Player.removeCooldown(name: String, then: () -> Unit = {}) = depart(this, name, then)

/**
 * Get the time left of a [Player]'s [BasicCooldown].
 * @see [club.rarlab.classicplugin.cooldown.CooldownHandler.fetch]
 */
fun Player.getCooldown(name: String): Instant? = fetch(this, name)

/**
 * Get whether or not a [Player] has got a specific [BasicCooldown].
 * @see [club.rarlab.classicplugin.cooldown.CooldownHandler.contains]
 */
fun Player.hasCooldown(name: String): Boolean = contains(this, name)