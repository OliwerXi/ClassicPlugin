package club.rarlab.classicplugin.nms.global

import club.rarlab.classicplugin.nms.GlobalReflection.FetchType.CLASS
import club.rarlab.classicplugin.nms.GlobalReflection.FetchType.METHOD
import club.rarlab.classicplugin.nms.GlobalReflection.VERSION_NUMBER
import club.rarlab.classicplugin.nms.GlobalReflection.get
import club.rarlab.classicplugin.nms.ReflectionHelper.createPacket
import club.rarlab.classicplugin.nms.ReflectionHelper.sendPacket
import club.rarlab.classicplugin.nms.wrappers.TitleWrapper
import org.bukkit.entity.Player
import java.lang.reflect.Method
import club.rarlab.classicplugin.nms.wrappers.TitleWrapper.Options as TitleOptions

/**
 * Class to handle global implementation of [TitleWrapper].
 */
class TitleWrapperGlobal internal constructor() : TitleWrapper {
    override val version: String = "Global"

    /**
     * Send an actionbar to an array of players.
     */
    override fun bar(options: TitleOptions, message: String, vararg players: Player) {
        if (VERSION_NUMBER >= 111) {
            provide("ACTIONBAR", options, message, *players)
            return
        }

        sendPacket(createPacket("PacketPlayOutChat", serialize(message), 2.toByte()), *players)
    }

    /**
     * Send a title to an array of players.
     */
    override fun title(options: TitleOptions, message: String, vararg players: Player) {
        val (fadeIn, stay, fadeOut) = options

        if (VERSION_NUMBER >= 132) {
            players.forEach { player -> player.sendTitle(message, null, fadeIn, stay, fadeOut) }
            return
        }

        provide("TITLE", options, message, *players)
    }

    /**
     * Send a subtitle to an array of players.
     */
    override fun subTitle(options: TitleOptions, message: String, vararg players: Player) {
        val (fadeIn, stay, fadeOut) = options

        if (VERSION_NUMBER >= 132) {
            players.forEach { player -> player.sendTitle(null, message, fadeIn, stay, fadeOut) }
            return
        }

        provide("SUBTITLE", options, message, *players)
    }

    /**
     * Send a specific title to an array of [Player] with specific options.
     */
    private fun provide(type: String, options: TitleOptions, message: String, vararg players: Player) {
        val (fadeIn, stay, fadeOut) = options
        val chatSerializer = serialize(message)
        val packet = createPacket("PacketPlayOutTitle", getAction(type), chatSerializer, fadeIn, stay, fadeOut)
        sendPacket(packet, *players)
    }

    /**
     * Get title action from corresponding [Enum] by case-sensitive name.
     *
     * @param var1   name of the action to fetch.
     * @return [Any] corresponding action.
     */
    private fun getAction(var1: String): Any {
        return get<Class<*>>(CLASS, "EnumTitleAction").getDeclaredField(var1).get(null)
    }

    /**
     * Serialize format for the IChatBaseComponent (Chat Serializer).
     *
     * @param var1   text to be sent.
     * @return [Any] corresponding IChatBaseComponent object.
     */
    private fun serialize(var1: String): Any {
        val base = "{\"text\": \"%s\"}".format(var1)
        return get<Method>(METHOD, "ChatSerializer_a").invoke(null, base)
    }
}