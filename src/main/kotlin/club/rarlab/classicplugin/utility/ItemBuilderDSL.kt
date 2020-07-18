package club.rarlab.classicplugin.utility

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
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
    infix fun meta(handle: T.() -> Unit) {
        val itemMeta = completedItem.itemMeta
        handle(itemMeta as T)
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
     * Apply an owner to the [SkullMeta].
     */
    fun owner(name: String) = meta { this.owner = name }

    /**
     * Apply textures to the [SkullMeta].
     */
    fun texture(base: String) {
        val profile = GameProfile(UUID.randomUUID(), null)
        val properties = profile.properties

        val data = Base64.getEncoder().encode("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}".format(base).toByteArray())
        properties.put("textures", Property("textures", String(data)))

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