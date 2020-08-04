package club.rarlab.classicplugin.packet

import io.netty.channel.ChannelDuplexHandler
import org.bukkit.entity.Player

/**
 * Class to handle a [Player]'s packets.
 */
open class PacketHandler(internal val player: Player) : ChannelDuplexHandler()