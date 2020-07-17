package club.rarlab.classicplugin

import org.bukkit.plugin.java.JavaPlugin

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
    override fun onLoad() = context.load.run()

    /**
     * Base onEnable.
     */
    override fun onEnable() = context.enable.run()

    /**
     * Base onDisable.
     */
    override fun onDisable() = context.disable.run()

    /**
     * Build a [LoadContext].
     */
    internal fun buildContext(handle: LoadContextBuilder.() -> LoadContext): LoadContext = handle(LoadContextBuilder())

    /**
     * Class to hold loading context data.
     */
    data class LoadContext internal constructor(val load: Runnable, val enable: Runnable, val disable: Runnable)

    /**
     * Class to build the [LoadContext].
     */
    inner class LoadContextBuilder internal constructor() {
        private var load = Runnable {}
        private var enable = Runnable {}
        private var disable = Runnable {}

        /**
         * Apply a runnable for the load logic.
         */
        infix fun load(handle: Runnable.() -> Unit) {
            load = Runnable {}.also(handle)
        }

        /**
         * Apply a runnable for the enable logic.
         */
        infix fun enable(handle: Runnable.() -> Unit) {
            enable = Runnable {}.also(handle)
        }

        /**
         * Apply a runnable for the disable logic.
         */
        infix fun disable(handle: Runnable.() -> Unit) {
            disable = Runnable {}.also(handle)
        }

        /**
         * Finish and build the final [LoadContext].
         */
        fun finish(): LoadContext = LoadContext(load, enable, disable)
    }
}