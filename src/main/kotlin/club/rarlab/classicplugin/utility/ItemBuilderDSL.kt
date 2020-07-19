package club.rarlab.classicplugin.utility

import com.google.common.collect.Multimap
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

/**
 * Class to handle the Item Builder.
 */
class ItemBuilderDSL(var material: XMaterial = XMaterial.AIR) {
    /**
     * [ItemStack] to be built.
     */
    private val completedItem: ItemStack = ItemStack(Material.AIR)

    /**
     * [Int] amount to be applied.
     */
    var amount: Int = 1

    /**
     * [HashMap] of [Enchantment] to be applied.
     */
    var enchantments: HashMap<Enchantment, Int> = hashMapOf()

    /**
     * [ItemMeta] to be applied to the [ItemStack].
     */
    @Suppress("UNCHECKED_CAST")
    infix fun meta(handle: ItemMeta?.() -> Unit) {
        val itemMeta = completedItem.itemMeta
        handle(itemMeta)
        completedItem.itemMeta = itemMeta
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
class SkullBuilderDSL internal constructor() {
    /**
     * [ItemStack] to be built.
     */
    private val completedItem = XMaterial.PLAYER_HEAD.toItem()!!

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

        private val propertyClazz: Class<*> by lazy {
            Class.forName("com.mojang.authlib.properties.Property")
        }

        private val property: Constructor<*> by lazy {
            propertyClazz.getConstructor(String::class.java, String::class.java)
        }

        private val properties: Field by lazy {
            gameProfileClazz.getDeclaredField("properties")
        }

        private val mapProperties: Field by lazy {
            val clazz = Class.forName("com.mojang.authlib.properties.PropertyMap")
            clazz.getDeclaredField("properties")
        }
    }

    /**
     * Apply an owner to the [SkullMeta].
     */
    fun owner(name: String) {
        val skullMeta = completedItem.itemMeta as SkullMeta
        skullMeta.owner = name
        completedItem.itemMeta = skullMeta
    }

    /**
     * Apply textures to the [SkullMeta].
     */
    @Suppress("UNCHECKED_CAST")
    fun texture(base: String) {
        val profile = gameProfile.newInstance(UUID.randomUUID(), null)
        val properties = properties.run { isAccessible = true; get(profile) }

        val data = Base64.getEncoder().encode("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}".format(base).toByteArray())
        mapProperties.run {
            isAccessible = true
            (this[properties] as Multimap<Any, Any>).put("textures", property.newInstance("textures", String(data)))
        }

        with (completedItem) {
            val newMeta = itemMeta
            val profileField = newMeta?.javaClass?.getDeclaredField("profile") ?: return@with
            profileField.isAccessible = true
            profileField.set(newMeta, profile)
            itemMeta = newMeta
        }
    }

    /**
     * [ItemMeta] to be applied to the [ItemStack].
     */
    @Suppress("UNCHECKED_CAST")
    infix fun meta(handle: ItemMeta?.() -> Unit) {
        val itemMeta = completedItem.itemMeta
        handle(itemMeta)
        completedItem.itemMeta = itemMeta
    }

    /**
     * Fetch the [ItemStack].
     */
    fun complete(): ItemStack = this.completedItem
}

/**
 * Build an [ItemStack].
 */
fun buildItem(handle: ItemBuilderDSL.() -> Unit): ItemStack {
    val builder = ItemBuilderDSL()
    builder.handle()
    return builder.complete()
}

/**
 * Build a skull.
 */
fun buildSkull(handle: SkullBuilderDSL.() -> Unit): ItemStack {
    val builder = SkullBuilderDSL()
    builder.handle()
    return builder.complete()
}