package club.rarlab.classicplugin.extension

/**
 * Run an operation after or before one is happening with specific type.
 *
 * This function is pretty much useless to a point where we don't
 * want it at all, but it makes it nicer in terms of running operations
 * after said function has been ran.
 *
 * EXAMPLE:
 * <code>
 *     T#callFunction().then { println("Quite clean...") }
 * </code>
 */
fun <T> T.then(then: (T) -> Unit): T {
    then(this)
    return this
}