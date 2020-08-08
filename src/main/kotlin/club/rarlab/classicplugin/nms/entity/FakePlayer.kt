package club.rarlab.classicplugin.nms.entity

import club.rarlab.classicplugin.extension.*
import club.rarlab.classicplugin.hologram.HologramLine
import club.rarlab.classicplugin.nms.GlobalReflection.FetchType.*
import club.rarlab.classicplugin.nms.GlobalReflection.VERSION_NUMBER
import club.rarlab.classicplugin.nms.GlobalReflection.get
import club.rarlab.classicplugin.nms.ReflectionHelper
import club.rarlab.classicplugin.nms.ReflectionHelper.createPacket
import club.rarlab.classicplugin.nms.ReflectionHelper.sendPacket
import club.rarlab.classicplugin.task.schedule
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.apache.commons.lang.RandomStringUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.lang.reflect.Array
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.*

/**
 * [FakePlayer] used to create fake [Player]s.
 */
class FakePlayer private constructor(val owner: UUID, nameTag: String) {
    /**
     * [Any] built EntityPlayer object.
     */
    private val entity: Any

    /**
     * [Array] of the single entity.
     */
    private val entityArray = Array.newInstance(get<Class<*>>(CLASS, "EntityPlayer"), 1)

    /**
     * [String] of the actual NPC name.
     */
    private val preciseName: String

    /**
     * [HologramLine] of the [FakePlayer]'s nametag.
     */
    private val hologram: HologramLine

    /**
     * [Equipment] of the [FakePlayer].
     */
    private var equipment: Equipment? = null

    /**
     * Apply an [Equipment] to the [FakePlayer].
     *
     * @param handle receiver to be handled.
     */
    infix fun equipment(handle: EquipmentBuilderDSL.() -> Unit) {
        val builder = EquipmentBuilderDSL()
        handle(builder)
        this.equipment = builder.complete()
    }

    /**
     * Apply a skin by [Property] to the [FakePlayer].
     *
     * @param texturesProperty [Property] of textures to be applied to the [FakePlayer].
     */
    fun applySkin(texturesProperty: Property) {
        val entityProfile = get<Method>(METHOD, "EntityHuman_getProfile").invoke(this.entity) as? GameProfile ?: return
        entityProfile.properties.put("textures", texturesProperty)
    }

    /**
     * Apply a [Player]'s skin to the [FakePlayer].
     *
     * @param player [Player]'s textures to fetch and then apply to the [FakePlayer].
     */
    fun applySkin(player: Player) {
        val texturesProperty = ReflectionHelper.getProfile(player).properties["textures"].find { it.name == "textures" } ?: return
        this.applySkin(texturesProperty)
    }

    /**
     * Set the [FakePlayer]'s [Location].
     *
     * @param location the [FakePlayer] should be 'teleported' to.
     */
    fun setLocation(location: Location) {
        val (x, y, z, yaw, pitch) = location
        get<Method>(METHOD, "Entity_setLocation").invoke(this.entity, x, y, z, yaw, pitch)
        hologram.updateLocation(location.clone().subtract(0.0, 0.1, 0.0)) // PERHAPS WE NEED TO PASS PLAYERS??!?!
    }

    /**
     * Despawn the entity.
     */
    fun despawn() = hideFrom(*Bukkit.getOnlinePlayers().toTypedArray())

    /**
     * Show the entity to an array of [Player].
     *
     * @param players array of [Player] to show to.
     */
    fun showTo(vararg players: Player) {
        sendPacket(createPacket("PacketPlayOutPlayerInfo", getInfoAction("ADD_PLAYER"), this.entityArray), *players)
        sendPacket(createPacket("PacketPlayOutNamedEntitySpawn", this.entity), *players)
        equipment?.packets(getEntityId() ?: return)?.forEach { packet -> sendPacket(packet, *players) }

        players.forEach { player -> player.scoreboard = SCOREBOARD }
        sendPacket(createPacket("PacketPlayOutScoreboardTeam", TEAM, listOf(this.preciseName), 3), *players)
        hologram.showTo(*players)

        schedule(40, false, Runnable {
            sendPacket(createPacket("PacketPlayOutPlayerInfo", getInfoAction("REMOVE_PLAYER"), this.entityArray), *players)
        })
    }

    /**
     * Hide the entity from an array of [Player].
     *
     * @param players array of [Player] to hide from.
     */
    fun hideFrom(vararg players: Player) {
        val entityId = getEntityId() ?: return
        sendPacket(createPacket("PacketPlayOutPlayerInfo", getInfoAction("REMOVE_PLAYER"), this.entityArray), *players)
        sendPacket(createPacket("PacketPlayOutEntityDestroy", intArrayOf(entityId)), *players)
        hologram.hideFrom(*players)
    }

    /**
     * Get the [FakePlayer]'s entity id.
     *
     * @return [Int] corresponding ID if not null.
     */
    fun getEntityId(): Int? {
        return get<Method>(METHOD, "Entity_getId").invoke(this.entity) as? Int
    }

    /**
     * Get EnumPlayerInfoAction field from corresponding [Enum] by case-sensitive name.
     *
     * @param var1   name of the action to fetch.
     * @return [Any] corresponding action.
     */
    private fun getInfoAction(var1: String): Any {
        return get<Class<*>>(CLASS, "EnumPlayerInfoAction").getDeclaredField(var1).get(null)
    }

    /**
     * Get a [Player] object by the owner's [UUID].
     *
     * @return [Player] corresponding object.
     */
    private fun getPlayer(): Player? = Bukkit.getPlayer(this.owner)

    /**
     * Pre initialization.
     */
    init {
        val player = getPlayer() ?: throw NullPointerException("Player is offline/invalid!")
        val worldServer = ReflectionHelper.getWorldServer(player)

        preciseName = RandomStringUtils.randomAlphanumeric(16)
        hologram = HologramLine(player.location.clone().add(0.0, 0.15, 0.0), nameTag)

        get<Constructor<*>>(CONSTRUCTOR, "EntityPlayer").newInstance(
                SERVER, worldServer, GameProfile(UUID.randomUUID(), preciseName),
                get<Constructor<*>>(CONSTRUCTOR, "PlayerInteractManager").newInstance(worldServer)
        ).also { entity -> this.entity = entity }

        Array.set(entityArray, 0, this.entity)
    }

    /**
     * Global Stuff.
     */
    companion object {
        /**
         * [Scoreboard] object to hold roaming players.
         */
        internal val SCOREBOARD: Scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard.apply {
            registerNewTeam("ROAM").run {
                if (VERSION_NUMBER >= 91) {
                    this.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
                    return@apply
                }
                this.nameTagVisibility = NameTagVisibility.NEVER
            }
        }

        /**
         * [Any] NMS team to be used when sending corresponding packets.
         */
        internal val TEAM: Any by lazy {
            val nmsScoreboard = get<Constructor<*>>(CONSTRUCTOR, "Scoreboard").newInstance()
            get<Constructor<*>>(CONSTRUCTOR, "ScoreboardTeam").newInstance(nmsScoreboard, "ROAM")
        }

        /**
         * The corresponding MinecraftServer object.
         */
        private val SERVER: Any by lazy {
            val craftServer = get<Class<*>>(CLASS, "CraftServer").cast(Bukkit.getServer())
            get<Method>(METHOD, "CraftServer_getServer").invoke(craftServer)
        }

        /**
         * Generate a new [FakePlayer] by owner unique id and name tag.
         *
         * @param owner   whom to be the owner of this [FakePlayer].
         * @param nameTag to be set for the [FakePlayer].
         * @return [FakePlayer] corresponding fake player.
         */
        fun generate(owner: UUID, nameTag: String): FakePlayer = FakePlayer(owner, nameTag)
    }
}