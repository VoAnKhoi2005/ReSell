package com.example.resell.store


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ReactiveStore<T> private constructor() {
    private val _item = MutableStateFlow<T?>(null)
    val item: StateFlow<T?> = _item

    fun set(value: T?) {
        _item.value = value
    }

    companion object {
        private val map = mutableMapOf<Class<*>, ReactiveStore<*>>()

        @Suppress("UNCHECKED_CAST")
        operator fun <T> invoke(clazz: Class<T>): ReactiveStore<T> {
            return map.getOrPut(clazz) { ReactiveStore<T>() } as ReactiveStore<T>
        }

        inline operator fun <reified T> invoke(): ReactiveStore<T> = invoke(T::class.java)
    }
}
