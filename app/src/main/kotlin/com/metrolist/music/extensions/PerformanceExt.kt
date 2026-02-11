/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Performance optimization utilities for memory-efficient operations
 */

/**
 * Asynchronously loads and processes a list using IO dispatcher
 * Prevents blocking the main thread for large data operations
 */
suspend inline fun <T, R> List<T>.mapAsync(crossinline transform: suspend (T) -> R): List<R> {
    return withContext(Dispatchers.Default) {
        map { transform(it) }
    }
}

/**
 * Filters and maps in a single pass for better memory efficiency
 * Reduces the number of intermediate collections created
 */
inline fun <T, R> Iterable<T>.filterMapNotNull(transform: (T) -> R?): List<R> {
    val result = mutableListOf<R>()
    for (element in this) {
        transform(element)?.let { result.add(it) }
    }
    return result
}

/**
 * Takes elements up to a limit with lazy evaluation
 * Prevents loading unnecessary elements
 */
fun <T> Sequence<T>.limitTo(limit: Int): Sequence<T> {
    return this.take(limit)
}

/**
 * Chunks a list into smaller sublists for batch processing
 * Useful for managing memory usage with large datasets
 */
inline fun <T> List<T>.chunked(size: Int, action: (List<T>) -> Unit) {
    for (i in indices step size) {
        action(subList(i, minOf(i + size, this.size)))
    }
}
