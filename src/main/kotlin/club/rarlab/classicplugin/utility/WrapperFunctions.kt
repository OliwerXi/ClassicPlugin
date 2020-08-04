package club.rarlab.classicplugin.utility

import club.rarlab.classicplugin.nms.GlobalReflection.VERSION_NUMBER
import club.rarlab.classicplugin.nms.global.TitleWrapperGlobal
import club.rarlab.classicplugin.nms.wrappers.TitleWrapper
import club.rarlab.classicplugin.nms.wrappers.TitleWrapper.Options as TitleOptions

/**
 * Build a [TitleOptions] object.
 *
 * @param var1 fade in time value in ticks.
 * @param var2 stay time value in ticks.
 * @param var3 fade out time value in ticks.
 */
fun titleOptions(var1: Int, var2: Int, var3: Int) = TitleOptions(var1, var2, var3)

/**
 * Build the corresponding [TitleWrapper].
 *
 * @return [TitleWrapper] built.
 */
internal fun buildTitleWrapper(): TitleWrapper = when {
    VERSION_NUMBER > 81 -> TitleWrapperGlobal()
    else -> object : TitleWrapper { override val version: String = "Unknown" }
}