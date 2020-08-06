package club.rarlab.classicplugin.nms

import club.rarlab.classicplugin.nms.GlobalReflection.FetchType.*
import club.rarlab.classicplugin.nms.GlobalReflection.VERSION_NUMBER
import club.rarlab.classicplugin.nms.GlobalReflection.get
import com.mojang.authlib.GameProfile
import io.netty.channel.Channel
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Object that contains reflection helper functions.
 *  |- getCraftPlayer
 *  |- getPlayerConnection
 *  |- sendPacket
 *  |- createPacket
 *  |- ...
 */
object ReflectionHelper {
    /**
     * Get a [Player]'s CraftPlayer object.
     *
     * @param player whom's CraftPlayer object to fetch.
     * @return [Any] corresponding CraftPlayer object.
     */
    fun getCraftPlayer(player: Player): Any {
        return get<Class<*>>(CLASS, "CraftPlayer").cast(player)
    }

    /**
     * Get a [Player]'s EntityPlayer object.
     *
     * @param player whom's EntityPlayer object to fetch.
     * @return [Any] corresponding EntityPlayer object.
     */
    fun getEntityPlayer(player: Player): Any {
        return get<Method>(METHOD, "CraftPlayer_Handle").invoke(player)
    }

    /**
     * Get a [Player]'s PlayerConnection object.
     *
     * @param player whom's PlayerConnection object to fetch.
     * @return [Any] corresponding PlayerConnection object.
     */
    fun getPlayerConnection(player: Player): Any {
        return get<Field>(FIELD, "EntityPlayer_playerConnection").get(getEntityPlayer(player))
    }

    /**
     * Get a [Player]'s NetworkManager object.
     *
     * @param player whom's NetworkManager object to fetch.
     * @return [Any] corresponding NetworkManager object.
     */
    fun getNetworkManager(player: Player): Any {
        return get<Field>(FIELD, "PlayerConnection_networkManager").get(getPlayerConnection(player))
    }

    /**
     * Get a [Player]'s netty [Channel].
     *
     * @param player whom's [Channel] to fetch.
     * @return [Channel] corresponding channel.
     */
    fun getChannel(player: Player): Channel {
        val channelField = get<Field>(FIELD, "NetworkManager_channel")
        if (VERSION_NUMBER <= 82) channelField.isAccessible = true
        return channelField.get(getNetworkManager(player)) as Channel
    }

    /**
     * Get a WorldServer object by a [Player]'s [org.bukkit.World].
     *
     * @param player whom's [org.bukkit.World] to fetch from.
     * @return [Any] corresponding WorldServer object.
     */
    fun getWorldServer(player: Player): Any {
        val craftWorld = get<Class<*>>(CLASS, "CraftWorld").cast(player.world)
        return get<Method>(METHOD, "CraftWorld_Handle").invoke(craftWorld)
    }

    /**
     * Get a [Player]'s [GameProfile] object.
     *
     * @param player whom's [GameProfile] to fetch.
     * @return [GameProfile] corresponding profile.
     */
    fun getProfile(player: Player): GameProfile {
        return get<Method>(METHOD, "CraftPlayer_getProfile").invoke(getCraftPlayer(player)) as GameProfile
    }

    /**
     * Get an NMS ItemStack by original.
     *
     * @param original to convert.
     * @return [Any] corresponding object.
     */
    fun getNmsItem(original: ItemStack): Any {
        return get<Method>(METHOD, "CraftItemStack_asNMSCopy").invoke(null, original)
    }

    /**
     * Send a specific Packet to an array of [Player].
     *
     * @param packet  to be sent to the players.
     * @param players array of [Player] the packet shall be sent to.
     */
    fun sendPacket(packet: Any, vararg players: Player) = players.forEach { player ->
        get<Method>(METHOD, "PlayerConnection_sendPacket").invoke(this.getPlayerConnection(player), packet)
    }

    /**
     * Create a specific packet by name and arguments.
     *
     * @param name      of the packet to be created.
     * @param arguments to be applied upon creating a new instance of the packet.
     * @return [Any]    newly created packet.
     */
    fun createPacket(name: String, vararg arguments: Any): Any {
        return get<Constructor<*>>(CONSTRUCTOR, name).newInstance(*arguments)
    }
}