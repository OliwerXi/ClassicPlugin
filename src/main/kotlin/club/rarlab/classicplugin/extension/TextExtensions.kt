package club.rarlab.classicplugin.extension

import org.apache.commons.lang.WordUtils
import org.bukkit.ChatColor

/**
 * Process all color codes in a [String].
 */
fun String.colourise(): String = ChatColor.translateAlternateColorCodes('&', this)

/**
 * Capitalise all words in a [String].
 */
fun String.capitalise(): String = WordUtils.capitalizeFully(this)