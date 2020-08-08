package club.rarlab.classicplugin.hologram

import club.rarlab.classicplugin.extension.*
import club.rarlab.classicplugin.nms.GlobalReflection.FetchType.CONSTRUCTOR
import club.rarlab.classicplugin.nms.GlobalReflection.FetchType.METHOD
import club.rarlab.classicplugin.nms.GlobalReflection.VERSION_NUMBER
import club.rarlab.classicplugin.nms.GlobalReflection.get
import club.rarlab.classicplugin.nms.ReflectionHelper
import club.rarlab.classicplugin.nms.ReflectionHelper.createPacket
import club.rarlab.classicplugin.nms.ReflectionHelper.sendPacket
import org.bukkit.Location
import org.bukkit.entity.Player
import java.lang.reflect.Constructor
import java.lang.reflect.Method

class HologramLine(location: Location, line: String) {
    private var entity: Any = generateLine(location, line)

    /**
     * Show the [HologramLine] to an array of players.
     *
     * @param players array of players to show the hologram line.
     */
    fun showTo(vararg players: Player) {
        val packet = this.packet()
        sendPacket(packet, *players)
    }

    /**
     * Hide the [HologramLine] from an array of players.
     *
     * @param players array of players to hide the hologram line for.
     */
    fun hideFrom(vararg players: Player) {
        val packet = createPacket("PacketPlayOutEntityDestroy", intArrayOf(this.getEntityId()))
        sendPacket(packet, *players)
    }

    /**
     * Update the [HologramLine]'s location for an array of players.
     *
     * @param location where the hologram line shall be placed at.
     * @param players  array of players to update the location for.
     */
    fun updateLocation(location: Location, vararg players: Player) {
        val (x, y, z, yaw, pitch) = location
        get<Method>(METHOD, "Entity_setLocation").invoke(this.entity, x, y, z, yaw, pitch)

        val packet = createPacket("PacketPlayOutEntityTeleport", this.entity)
        sendPacket(packet, *players)
    }

    /**
     * Create a new 'PacketPlayOutSpawnEntityLiving' packet for the corresponding entity.
     *
     * @return [Any] corresponding packet.
     */
    private fun packet(): Any = createPacket("PacketPlayOutSpawnEntityLiving", this.entity)

    /**
     * Get the entity's id.
     *
     * @return [Int] corresponding entity id.
     */
    private fun getEntityId(): Int {
        return get<Method>(METHOD, "Entity_getId").invoke(this.entity) as Int
    }

    /**
     * Global stuff.
     */
    companion object {
        /**
         * Generate a line.
         *
         * @param loc  where the line should be placed.
         * @param line to be displayed.
         * @return [Any] corresponding entity.
         */
        internal fun generateLine(loc: Location, line: String): Any = get<Constructor<*>>(CONSTRUCTOR, "EntityArmorStand").newInstance(
                ReflectionHelper.getWorldServer(loc.world!!), loc.x, loc.y, loc.z
        ).apply {
            get<Method>(METHOD, "Entity_setCustomName").invoke(this,
                    if (VERSION_NUMBER < 131) line.colourise()
                    else get<Method>(METHOD, "ChatSerializer_a").invoke(null, line.colourise())
            )
            get<Method>(METHOD, "Entity_setCustomNameVisible").invoke(this, true)
            get<Method>(METHOD, "EntityArmorStand_setInvisible").invoke(this, true)
            get<Method>(METHOD, "EntityArmorStand_setGravity").invoke(this, false)
        }
    }
}