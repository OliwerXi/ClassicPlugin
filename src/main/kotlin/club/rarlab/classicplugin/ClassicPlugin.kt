package club.rarlab.classicplugin

import club.rarlab.classicplugin.nms.wrappers.TitleWrapper
import club.rarlab.classicplugin.utility.buildTitleWrapper
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

/**
 * Abstract class to handle main classes.
 */
abstract class ClassicPlugin : JavaPlugin() {
    /**
     * [LoadContext] instance.
     */
    private val context: LoadContext by lazy { load() }

    /**
     * Abstract function to load enabling & disabling logic.
     */
    abstract fun load(): LoadContext

    /**
     * Base onLoad.
     */
    override fun onLoad() = context.load(Runnable {})

    /**
     * Base onEnable.
     */
    override fun onEnable() {
        PLUGIN.setInstance(this)
        PLUGIN.titleWrapper = buildTitleWrapper()
        context.enable(Runnable {})
    }

    /**
     * Base onDisable.
     */
    override fun onDisable() = context.disable(Runnable {})

    /**
     * Build a [LoadContext].
     */
    protected fun buildContext(handle: LoadContextBuilder.() -> Unit): LoadContext {
        val builder = LoadContextBuilder()
        handle(builder)
        return builder.finish()
    }

    /**
     * Class to hold loading context data.
     */
    data class LoadContext internal constructor(val load: (Runnable) -> Unit, val enable: (Runnable) -> Unit, val disable: (Runnable) -> Unit)

    /**
     * Class to build the [LoadContext].
     */
    protected inner class LoadContextBuilder internal constructor() {
        private var load: (Runnable) -> Unit = {}
        private var enable: (Runnable) -> Unit = {}
        private var disable: (Runnable) -> Unit = {}

        /**
         * Apply a runnable for the load logic.
         */
        infix fun load(handle: Runnable.() -> Unit) {
            this.load = handle
        }

        /**
         * Apply a runnable for the enable logic.
         */
        infix fun enable(handle: Runnable.() -> Unit) {
            this.enable = handle
        }

        /**
         * Apply a runnable for the disable logic.
         */
        infix fun disable(handle: Runnable.() -> Unit) {
            this.disable = handle
        }

        /**
         * Finish and build the final [LoadContext].
         */
        fun finish(): LoadContext = LoadContext(load, enable, disable)
    }

    /**
     * Mutable [ClassicPlugin] properties (private).
     */
    object PLUGIN {
        internal lateinit var instance: Plugin
        internal lateinit var titleWrapper: TitleWrapper

        fun setInstance(instance: Plugin) { this.instance = instance }
    }

    /**
     * Immutable [ClassicPlugin] properties (public).
     */
    companion object {
        val INSTANCE: Plugin by lazy { PLUGIN.instance }
        val TITLE_WRAPPER: TitleWrapper by lazy { PLUGIN.titleWrapper }
    }
}