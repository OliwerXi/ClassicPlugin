package club.rarlab.classicplugin.utility

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.HashMap

/**
 * Base class to handle item builders.
 */
open class ItemBuilderDSL<T: ItemMeta>(var material: XMaterial = XMaterial.AIR) {
    /**
     * [ItemStack] to be built.
     */
    internal val completedItem: ItemStack = ItemStack(Material.AIR)

    /**
     * [Int] amount to be applied.
     */
    var amount: Int = 1

    /**
     * [HashMap] of [Enchantment] to be applied.
     */
    var enchantments: HashMap<Enchantment, Int> = hashMapOf()

    /**
     * [T] to be applied to the [ItemStack].
     */
    @Suppress("UNCHECKED_CAST")
    infix fun meta(handle: T?.() -> Unit) {
        val itemMeta = completedItem.itemMeta
        handle(itemMeta as T?)
        completedItem.itemMeta = itemMeta
    }

    /**
     * Apply skull modifications to the item.
     */
    infix fun skull(handle: SkullBuilderDSL.() -> Unit) = with (completedItem) {
        val skullBuilder = SkullBuilderDSL()
        skullBuilder.applyMeta(itemMeta)
        skullBuilder.handle()
        itemMeta = skullBuilder.finish()
    }

    /**
     * [ItemStack] complete the build.
     */
    fun complete(): ItemStack = completedItem.also {
        it.type = material.toMaterial() ?: Material.AIR
        it.amount = amount
        it.addEnchantments(enchantments)
    }
}

/**
 * Class to handle the skull builder.
 */
class SkullBuilderDSL : ItemBuilderDSL<SkullMeta>(XMaterial.PLAYER_HEAD) {
    /**
     * Static stuff.
     */
    companion object {
        private val gameProfileClazz: Class<*> by lazy {
            Class.forName("com.mojang.authlib.GameProfile")
        }

        private val gameProfile: Constructor<*> by lazy {
            gameProfileClazz.getConstructor(UUID::class.java, String::class.java)
        }

        private val property: Constructor<*> by lazy {
            val clazz = Class.forName("com.mojang.authlib.properties.Property")
            clazz.getConstructor(String::class.java, String::class.java)
        }

        private val properties: Field by lazy {
            gameProfileClazz.getDeclaredField("properties")
        }
    }

    /**
     * Apply an owner to the [SkullMeta].
     */
    fun owner(name: String) = meta { this?.owner = name }

    /**
     * Apply textures to the [SkullMeta].
     */
    @Suppress("UNCHECKED_CAST")
    fun texture(base: String) {
        val profile = gameProfile.newInstance(UUID.randomUUID(), null)
        val properties = properties.run {
            isAccessible = true
            get(profile) as MutableMap<Any, Any>
        }

        val data = Base64.getEncoder().encode("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}".format(base).toByteArray())
        properties["textures"] = property.newInstance("textures", String(data))

        with (completedItem) {
            val profileField = itemMeta?.javaClass?.getDeclaredField("profile") ?: return@with
            profileField.isAccessible = true
            profileField.set(itemMeta, profile)
        }
    }

    /**
     * Apply an [ItemMeta] to the [ItemStack].
     */
    fun applyMeta(meta: ItemMeta?) {
        completedItem.itemMeta = meta
    }

    /**
     * Finish the build and fetch the [ItemMeta].
     */
    fun finish(): ItemMeta? = this.completedItem.itemMeta
}

/**
 * Build an [ItemStack].
 */
fun buildItem(handle: ItemBuilderDSL<ItemMeta>.() -> Unit): ItemStack {
    val builder = ItemBuilderDSL<ItemMeta>()
    builder.handle()
    return builder.complete()
}