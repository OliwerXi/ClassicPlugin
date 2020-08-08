package club.rarlab.classicplugin.nms

import com.mojang.authlib.GameProfile
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Array.newInstance
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Object that contains global reflection stuff.
 *  |- Classes, Constructors, Methods & Fields.
 */
object GlobalReflection {
    /**
     * [String] precise NMS version in string.
     * <b>EXAMPLE:</b> v1_16_R1
     */
    @JvmField
    val VERSION: String = Bukkit.getServer().javaClass.`package`.name.drop(23)

    /**
     * [Int] precise NMS version in int.
     * <b>EXAMPLE:</b> 16
     */
    @JvmField
    val VERSION_NUMBER: Int = VERSION.drop(3).replace("_R", "").toInt()

    /**
     * [HashMap] of all cached [Class] to later be fetched and used.
     */
    private val classes: HashMap<String, Class<*>> = hashMapOf()

    /**
     * [HashMap] of all cached [Constructor] to later be fetched and used.
     */
    private val constructors: HashMap<String, Constructor<*>> = hashMapOf()

    /**
     * [HashMap] of all cached [Method] to later be fetched and used.
     */
    private val methods: HashMap<String, Method> = hashMapOf()

    /**
     * [HashMap] of all cached [Field] to later be fetched and used.
     */
    private val fields: HashMap<String, Field> = hashMapOf()

    /**
     * Fetch a specific member by name.
     *
     * @param type of the member.
     * @param name to be fetched.
     * @return [Type] corresponding type (nullable).
     */
    @Suppress("UNCHECKED_CAST")
    fun <Type> get(type: FetchType, name: String): Type {
        return when (type) {
            FetchType.CLASS -> classes[name] as? Type ?: throw RuntimeException("Could not find class $name!")
            FetchType.CONSTRUCTOR -> constructors[name] as? Type ?: throw RuntimeException("Could not find constructor $name!")
            FetchType.METHOD -> methods[name] as? Type ?: throw RuntimeException("Could not find method $name!")
            FetchType.FIELD -> fields[name] as? Type ?: throw RuntimeException("Could not find field $name!")
        }
    }

    /**
     * Available fetch types for [GlobalReflection.get].
     */
    enum class FetchType { CLASS, CONSTRUCTOR, METHOD, FIELD }

    /**
     * Get an nms [Class] by name.
     *
     * @param name of the [Class] to fetch.
     * @return [Class] corresponding class.
     * @throws ClassNotFoundException if the class is not present.
     */
    @Throws(ClassNotFoundException::class)
    private fun getNmsClass(name: String): Class<*> {
        return Class.forName("net.minecraft.server.$VERSION.$name")
    }

    /**
     * Get a craftbukkit [Class] by name.
     *
     * @param name of the [Class] to fetch.
     * @return [Class] corresponding class.
     * @throws ClassNotFoundException if the class is not present.
     */
    @Throws(ClassNotFoundException::class)
    private fun getBukkitClass(name: String): Class<*> {
        return Class.forName("org.bukkit.craftbukkit.$VERSION.$name")
    }

    /**
     * Add all methods, constructors, fields and classes
     * to their corresponding maps.
     */
    init {
        // class preparations
        val craftServer = getBukkitClass("CraftServer")
        val minecraftServer = getNmsClass("MinecraftServer")
        val worldServer = getNmsClass("WorldServer")
        val craftWorld = getBukkitClass("CraftWorld")
        val minecraftWorld = getNmsClass("World")
        val craftPlayer = getBukkitClass("entity.CraftPlayer")
        val minecraftEntity = getNmsClass("Entity")
        val entityPlayer = getNmsClass("EntityPlayer")
        val entityPlayerArray = (newInstance(entityPlayer, 0) as Array<*>)::class.java
        val entityHuman = getNmsClass("EntityHuman")
        val entityArmorStand = getNmsClass("EntityArmorStand")
        val entityLiving = getNmsClass("EntityLiving")
        val playerInteractManager = getNmsClass("PlayerInteractManager")
        val iChatBaseComponent = getNmsClass("IChatBaseComponent")
        val chatSerializer = if (VERSION_NUMBER > 81) getNmsClass("IChatBaseComponent\$ChatSerializer") else getNmsClass("ChatSerializer")
        val craftItemStack = getBukkitClass("inventory.CraftItemStack")
        val minecraftItemStack = getNmsClass("ItemStack")
        val minecraftScoreboard = getNmsClass("Scoreboard")
        val scoreboardTeam = getNmsClass("ScoreboardTeam")
        val packet = getNmsClass("Packet")
        val packetPlayOutChat = getNmsClass("PacketPlayOutChat")
        val packetPlayOutTitle = getNmsClass("PacketPlayOutTitle")
        val packetPlayOutPlayerInfo = getNmsClass("PacketPlayOutPlayerInfo")
        val packetPlayOutNamedEntitySpawn = getNmsClass("PacketPlayOutNamedEntitySpawn")
        val packetPlayOutEntityDestroy = getNmsClass("PacketPlayOutEntityDestroy")
        val packetPlayOutEntityEquipment = getNmsClass("PacketPlayOutEntityEquipment")
        val packetPlayOutScoreboardTeam = getNmsClass("PacketPlayOutScoreboardTeam")
        val packetPlayOutSpawnEntityLiving = getNmsClass("PacketPlayOutSpawnEntityLiving")
        val packetPlayOutEntityTeleport = getNmsClass("PacketPlayOutEntityTeleport")
        val enumTitleAction = if (VERSION_NUMBER > 81) getNmsClass("PacketPlayOutTitle\$EnumTitleAction") else getNmsClass("EnumTitleAction")
        val enumPlayerInfoAction = if (VERSION_NUMBER > 81) getNmsClass("PacketPlayOutPlayerInfo\$EnumPlayerInfoAction") else getNmsClass("EnumPlayerInfoAction")
        val enumItemSlot = if (VERSION_NUMBER >= 91) getNmsClass("EnumItemSlot") else null
        val playerConnection = getNmsClass("PlayerConnection")
        val networkManager = getNmsClass("NetworkManager")

        // classes
        classes.putAll(mapOf(
                /** GENERAL **/
                "CraftServer" to craftServer,
                "MinecraftServer" to minecraftServer,
                "WorldServer" to worldServer,
                "CraftWorld" to craftWorld,
                "World" to minecraftWorld,
                "CraftPlayer" to craftPlayer,
                "Entity" to minecraftEntity,
                "EntityPlayer" to entityPlayer,
                "EntityHuman" to entityHuman,
                "EntityArmorStand" to entityArmorStand,
                "EntityLiving" to entityLiving,
                "PlayerInteractManager" to playerInteractManager,
                "IChatBaseComponent" to iChatBaseComponent,
                "ChatSerializer" to chatSerializer,
                "CraftItemStack" to craftItemStack,
                "ItemStack" to minecraftItemStack,
                "Scoreboard" to minecraftScoreboard,
                "ScoreboardTeam" to scoreboardTeam,
                /** PACKETS **/
                "Packet" to packet,
                "PacketPlayOutChat" to packetPlayOutChat,
                "PacketPlayOutTitle" to packetPlayOutTitle,
                "PacketPlayOutPlayerInfo" to packetPlayOutPlayerInfo,
                "PacketPlayOutNamedEntitySpawn" to packetPlayOutNamedEntitySpawn,
                "PacketPlayOutEntityDestroy" to packetPlayOutEntityDestroy,
                "PacketPlayOutEntityEquipment" to packetPlayOutEntityEquipment,
                "PacketPlayOutScoreboardTeam" to packetPlayOutScoreboardTeam,
                "PacketPlayOutSpawnEntityLiving" to packetPlayOutSpawnEntityLiving,
                "PacketPlayOutEntityTeleport" to packetPlayOutEntityTeleport,
                "EnumTitleAction" to enumTitleAction,
                "EnumPlayerInfoAction" to enumPlayerInfoAction,
                "PlayerConnection" to playerConnection,
                "NetworkManager" to networkManager
        ))

        // nullable classes
        if (enumItemSlot != null) classes["EnumItemSlot"] = enumItemSlot

        // constructors
        if (VERSION_NUMBER < 111) constructors["PacketPlayOutChat"] = packetPlayOutChat.getDeclaredConstructor(iChatBaseComponent, Byte::class.java)
        constructors.putAll(mapOf(
                /** GENERAL **/
                "EntityPlayer" to entityPlayer.getDeclaredConstructor(
                        minecraftServer, worldServer,
                        GameProfile::class.java, playerInteractManager
                ),
                "EntityArmorStand" to entityArmorStand.getDeclaredConstructor(
                        minecraftWorld, Double::class.java, Double::class.java, Double::class.java
                ),
                "PlayerInteractManager" to if (VERSION_NUMBER < 141) {
                    playerInteractManager.getDeclaredConstructor(minecraftWorld)
                } else {
                    playerInteractManager.getDeclaredConstructor(worldServer)
                },
                "Scoreboard" to minecraftScoreboard.getDeclaredConstructor(),
                "ScoreboardTeam" to scoreboardTeam.getDeclaredConstructor(minecraftScoreboard, String::class.java),
                /** PACKETS **/
                "PacketPlayOutTitle" to packetPlayOutTitle.getDeclaredConstructor(
                        enumTitleAction, iChatBaseComponent, Int::class.java,
                        Int::class.java, Int::class.java
                ),
                "PacketPlayOutPlayerInfo" to packetPlayOutPlayerInfo.getDeclaredConstructor(enumPlayerInfoAction, entityPlayerArray),
                "PacketPlayOutNamedEntitySpawn" to packetPlayOutNamedEntitySpawn.getDeclaredConstructor(entityHuman),
                "PacketPlayOutEntityDestroy" to packetPlayOutEntityDestroy.getDeclaredConstructor(IntArray::class.java),
                "PacketPlayOutEntityEquipment" to if (VERSION_NUMBER < 161) {
                    packetPlayOutEntityEquipment.getDeclaredConstructor(
                            Int::class.java, enumItemSlot ?: Int::class.java, minecraftItemStack
                    )
                } else {
                    packetPlayOutEntityEquipment.getDeclaredConstructor(
                            Int::class.java, List::class.java
                    )
                },
                "PacketPlayOutScoreboardTeam" to packetPlayOutScoreboardTeam.getDeclaredConstructor(
                        scoreboardTeam, Collection::class.java, Int::class.java
                ),
                "PacketPlayOutSpawnEntityLiving" to packetPlayOutSpawnEntityLiving.getDeclaredConstructor(entityLiving),
                "PacketPlayOutEntityTeleport" to packetPlayOutEntityTeleport.getDeclaredConstructor(minecraftEntity)
        ))

        // methods
        methods.putAll(mapOf(
                /** GENERAL **/
                "CraftServer_getServer" to craftServer.getDeclaredMethod("getServer"),
                "CraftWorld_Handle" to craftWorld.getDeclaredMethod("getHandle"),
                "CraftPlayer_Handle" to craftPlayer.getDeclaredMethod("getHandle"),
                "CraftPlayer_getProfile" to craftPlayer.getDeclaredMethod("getProfile"),
                "Entity_setLocation" to minecraftEntity.getDeclaredMethod(
                        "setLocation", Double::class.java, Double::class.java,
                        Double::class.java, Float::class.java, Float::class.java
                ),
                "Entity_getId" to minecraftEntity.getDeclaredMethod("getId"),
                "Entity_setCustomName" to if (VERSION_NUMBER < 131) {
                    minecraftEntity.getDeclaredMethod("setCustomName", String::class.java)
                } else {
                    minecraftEntity.getDeclaredMethod("setCustomName", iChatBaseComponent)
                },
                "Entity_setCustomNameVisible" to minecraftEntity.getDeclaredMethod("setCustomNameVisible", Boolean::class.java),
                "EntityArmorStand_setInvisible" to entityArmorStand.getDeclaredMethod("setInvisible", Boolean::class.java),
                "EntityArmorStand_setGravity" to entityArmorStand.getDeclaredMethod("setGravity", Boolean::class.java),
                "EntityHuman_getProfile" to entityHuman.getDeclaredMethod("getProfile"),
                "ChatSerializer_a" to chatSerializer.getDeclaredMethod("a", String::class.java),
                "CraftItemStack_asNMSCopy" to craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack::class.java),
                /** PACKETS **/
                "PlayerConnection_sendPacket" to playerConnection.getDeclaredMethod("sendPacket", packet)
        ))

        // fields
        fields.putAll(mapOf(
                /** GENERAL **/
                "EntityPlayer_playerConnection" to entityPlayer.getDeclaredField("playerConnection"),
                "PlayerConnection_networkManager" to playerConnection.getDeclaredField("networkManager"),
                "NetworkManager_channel" to networkManager.getDeclaredField(when (VERSION_NUMBER) { 81 -> "i"; 82 -> "k"; else -> "channel" }),
                /** PACKETS **/
                "PacketPlayOutEntityTeleport_x" to packetPlayOutEntityTeleport.getDeclaredField("b"),
                "PacketPlayOutEntityTeleport_y" to packetPlayOutEntityTeleport.getDeclaredField("c"),
                "PacketPlayOutEntityTeleport_z" to packetPlayOutEntityTeleport.getDeclaredField("d")
        ))
    }
}