package club.rarlab.classicplugin.extension

/**
 * Insert a key with a specific value to correspondent predicate.
 *
 * @param expression to check for success & failure.
 * @param success    to be inserted as value if the predicate is true.
 * @param failure    to be inserted as value if the predicate is false.
 * @return           corresponding value that passed.
 */
fun <Key, Value> HashMap<Key, Value>.insertIf(expression: Boolean, key: Key, success: () -> Value, failure: () -> Value): Value? {
    return this.put(key, if (expression) success() else failure())
}