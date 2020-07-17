package club.rarlab.classicplugin.extensions

import org.apache.commons.lang.WordUtils
import org.bukkit.ChatColor

/**
 * Process all '&' color codes in a [String].
 */
fun String.color(): String = ChatColor.translateAlternateColorCodes('&', this)

/**
 * Capitalise all words in a [String].
 */
fun String.capitalise(): String = WordUtils.capitalizeFully(this)