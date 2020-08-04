package club.rarlab.classicplugin.nms.entity

import club.rarlab.classicplugin.nms.GlobalReflection.FetchType.CLASS
import club.rarlab.classicplugin.nms.GlobalReflection.FetchType.METHOD
import club.rarlab.classicplugin.nms.GlobalReflection.VERSION_NUMBER
import club.rarlab.classicplugin.nms.GlobalReflection.get
import club.rarlab.classicplugin.nms.ReflectionHelper.createPacket
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Method

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
        if (hand != null) this += createEquipmentPacket(id, "MAINHAND", hand)
        if (helmet != null) this += createEquipmentPacket(id, "HEAD", helmet)
        if (chestPlate != null) this += createEquipmentPacket(id, "CHEST", chestPlate)
        if (leggings != null) this += createEquipmentPacket(id, "LEGS", leggings)
        if (boots != null) this += createEquipmentPacket(id, "FEET", boots)
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
        val craftItem = get<Method>(METHOD, "CraftItemStack_asNMSCopy").invoke(null, itemStack)
        return createPacket("PacketPlayOutEntityEquipment", id, getEquipmentSlot(type, VERSION_NUMBER >= 91), craftItem)
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
        return when (type) { "HEAD" -> 1; "CHEST" -> 2; "LEGS" -> 3; "FEET" -> 4; else -> 0 }
    }
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