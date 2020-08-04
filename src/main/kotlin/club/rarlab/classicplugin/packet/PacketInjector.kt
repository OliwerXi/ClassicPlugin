package club.rarlab.classicplugin.packet

import club.rarlab.classicplugin.nms.ReflectionHelper.getChannel
import org.bukkit.entity.Player

/**
 * Object that contains all packet injector related functions.
 */
object PacketInjector {
    /**
     * Inject a [Player] and their [PacketHandler] object.
     *
     * @param packetHandler to inject.
     */
    fun inject(packetHandler: PacketHandler) {
        val player = packetHandler.player
        val channel = getChannel(player)
        val pipeline = channel.pipeline()

        if (pipeline["PacketInjector"] == null) {
            pipeline.addBefore("packet_handler", "PacketInjector", packetHandler)
        }
    }

    /**
     * Uninject a [Player].
     *
     * @param player whom to uninject.
     */
    fun uninject(player: Player) {
        val channel = getChannel(player)
        val pipeline = channel.pipeline()

        if (pipeline["PacketInjector"] != null) {
            pipeline.remove("PacketInjector")
        }
    }
}