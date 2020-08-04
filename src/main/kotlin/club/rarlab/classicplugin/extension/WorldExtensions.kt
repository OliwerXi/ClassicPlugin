package club.rarlab.classicplugin.extension

import org.bukkit.Location

/**
 * Operator components for [Location] destructuring.
 */
operator fun Location.component1() = this.x
operator fun Location.component2() = this.y
operator fun Location.component3() = this.z
operator fun Location.component4() = this.yaw
operator fun Location.component5() = this.pitch
operator fun Location.component6() = this.world