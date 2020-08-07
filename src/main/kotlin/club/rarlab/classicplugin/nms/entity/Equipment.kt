package club.rarlab.classicplugin.nms.entity

import club.rarlab.classicplugin.nms.GlobalReflection.FetchType.CLASS
import club.rarlab.classicplugin.nms.GlobalReflection.VERSION_NUMBER
import club.rarlab.classicplugin.nms.GlobalReflection.get
import club.rarlab.classicplugin.nms.ReflectionHelper.createPacket
import club.rarlab.classicplugin.nms.ReflectionHelper.getNmsItem
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

/**
 * Data class to build [Equipment] for entities and apply them.
 */
data class Equipment(
        val helmet: ItemStack?,
        val chestPlate: ItemStack?,
        val leggings: ItemStack?,
        val boots: ItemStack?,
        val hand: ItemStack?
) {
    /**
     * Apply the [Equipment] to a [LivingEntity].
     *
     * @param entity the equipment will be applied to.
     */
    fun applyTo(entity: LivingEntity) = with (entity) {
        equipment?.run {
            this.helmet = this@Equipment.helmet
            this.chestplate = this@Equipment.chestPlate
            this.leggings = this@Equipment.leggings
            this.boots = this@Equipment.boots
            this.setItemInHand(this@Equipment.hand)
        }
    }

    /**
     * Generate packets for every [Equipment] piece.
     *
     * @param id of the entity that shall be effected.
     */
    fun packets(id: Int): ArrayList<out Any> = arrayListOf<Any>().apply {
        if (VERSION_NUMBER < 161) {
            if (!isNullOrAir(hand)) this += createEquipmentPacket(id, "MAINHAND", hand!!)
            if (!isNullOrAir(helmet)) this += createEquipmentPacket(id, "HEAD", helmet!!)
            if (!isNullOrAir(chestPlate)) this += createEquipmentPacket(id, "CHEST", chestPlate!!)
            if (!isNullOrAir(leggings)) this += createEquipmentPacket(id, "LEGS", leggings!!)
            if (!isNullOrAir(boots)) this += createEquipmentPacket(id, "FEET", boots!!)
            return@apply
        }

        this += createPacket("PacketPlayOutEntityEquipment", id, mutableListOf<Pair<Any, Any>>().apply {
            if (!isNullOrAir(hand)) this += getEquipmentSlot("MAINHAND", true) to getNmsItem(hand!!)
            if (!isNullOrAir(helmet)) this +=  getEquipmentSlot("HEAD", true) to getNmsItem(helmet!!)
            if (!isNullOrAir(chestPlate)) this += getEquipmentSlot("CHEST", true) to getNmsItem(chestPlate!!)
            if (!isNullOrAir(leggings)) this += getEquipmentSlot("LEGS", true) to getNmsItem(leggings!!)
            if (!isNullOrAir(boots)) this += getEquipmentSlot("FEET", true) to getNmsItem(boots!!)
        }.toList())
    }

    /**
     * Create an equipment packet by type and [ItemStack].
     *
     * @param id        of entity the equipment packet shall effect.
     * @param type      of slot to create for.
     * @param itemStack to be set on the corresponding slot.
     * @return [Any] corresponding packet.
     */
    private fun createEquipmentPacket(id: Int, type: String, itemStack: ItemStack): Any {
        return createPacket("PacketPlayOutEntityEquipment", id, getEquipmentSlot(type, VERSION_NUMBER >= 91), getNmsItem(itemStack))
    }

    /**
     * Get EnumItemSlot field from corresponding [Enum] by case-sensitive name.
     *
     * @param type   name of the slot to fetch.
     * @param isNew  whether or not it's a newer server versions being ran.
     * @return [Any] corresponding slot.
     */
    private fun getEquipmentSlot(type: String, isNew: Boolean): Any {
        if (isNew) return get<Class<*>>(CLASS, "EnumItemSlot").getDeclaredField(type).get(null)
        return when (type) { "HEAD" -> 4; "CHEST" -> 3; "LEGS" -> 2; "FEET" -> 1; else -> 0 }
    }

    /**
     * Get whether or not a passed [ItemStack] object is null or of type [Material.AIR].
     *
     * @param itemStack to check.
     * @return [Boolean] corresponding boolean.
     */
    private fun isNullOrAir(itemStack: ItemStack?): Boolean = itemStack == null || itemStack.type == Material.AIR
}

/**
 * Class to complete a build for [Equipment].
 * <b>NOTE:</b> This class is made for DSL based parameters otherwise useless.
 */
class EquipmentBuilderDSL {
    /**
     * Equipment.
     */
    var helmet: ItemStack? = null
    var chestPlate: ItemStack? = null
    var leggings: ItemStack? = null
    var boots: ItemStack? = null
    var hand: ItemStack? = null

    /**
     * Complete the [Equipment] build.
     *
     * @return [Equipment] built equipment object.
     */
    fun complete(): Equipment = Equipment(helmet, chestPlate, leggings, boots, hand)
}