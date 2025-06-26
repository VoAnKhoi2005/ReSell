package com.example.resell.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReactiveStore<T> private constructor() {
    // Single item
    private val _item = MutableStateFlow<T?>(null)
    val item: StateFlow<T?> = _item

    // List of items
    private val _items = MutableStateFlow<List<T>>(emptyList())
    val items: StateFlow<List<T>> = _items

    // Set single item
    fun set(value: T?) {
        _item.value = value
    }

    // Set list of items
    fun setAll(list: List<T>) {
        _items.value = list
    }

    // Add item to list
    fun add(item: T) {
        _items.value = _items.value + item
    }

    // Remove item from list
    fun remove(item: T) {
        _items.value = _items.value - item
    }

    // Clear all
    fun deleteAll() {
        _item.value = null
        _items.value = emptyList()
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
//TODO:Lưu hoặc lấy đối tượng đơn
//ReactiveStore<User>().set(user)
//val user = ReactiveStore<User>().item.value
//TODO:Lưu danh sách:
//ReactiveStore<User>().setAll(listOfUser)
//val userList = ReactiveStore<User>().items.collectAsState()
//TODO:Thêm, xóa từng phần tử:
//ReactiveStore<User>().add(user)
//ReactiveStore<User>().remove(user)
//TODO:Xóa tất cả:
//ReactiveStore<User>().deleteAll()
