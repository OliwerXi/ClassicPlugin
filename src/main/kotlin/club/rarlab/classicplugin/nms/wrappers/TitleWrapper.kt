package club.rarlab.classicplugin.nms.wrappers

import club.rarlab.classicplugin.nms.NOT_SUPPORTED_MESSAGE
import org.bukkit.entity.Player

/**
 * Wrapper for all available title forms.
 */
interface TitleWrapper {
    /**
     * [String] implementation version in string.
     */
    val version: String

    /**
     * Send an actionbar to an array of [Player].
     *
     * <b>NOTE:</b> Options will only be applied in 1.11+.
     *
     * @param options of fade in, stay and fade out values.
     * @param message to be sent to the array of [Player]'s actionbars.
     * @param players array of [Player] the actionbar should be sent to.
     */
    fun bar(options: Options, message: String, vararg players: Player) {
        throw NotImplementedError(NOT_SUPPORTED_MESSAGE.format("Actionbars"))
    }

    /**
     * Send a title to an array of [Player].
     *
     * @param options of fade in, stay and fade out values.
     * @param message to be sent to the array of [Player]'s title view.
     * @param players array of [Player] the title should be sent to.
     */
    fun title(options: Options, message: String, vararg players: Player) {
        throw NotImplementedError(NOT_SUPPORTED_MESSAGE.format("Titles"))
    }

    /**
     * Send a subtitle to an array of [Player].
     *
     * @param options of fade in, stay and fade out values.
     * @param message to be sent to the array of [Player]'s subtitle view.
     * @param players array of [Player] the subtitle should be sent to.
     */
    fun subTitle(options: Options, message: String, vararg players: Player) {
        throw NotImplementedError(NOT_SUPPORTED_MESSAGE.format("Subtitles"))
    }

    /**
     * Data class of options for title based messages.
     */
    data class Options(val fadeIn: Int, val stay: Int, val fadeOut: Int)
}