package com.example.resell.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id") val id: String,
    @Json(name = "parent_category_id") val parentId: String? = null,
    @Json(name = "name") val name: String,
    @Json(name= "image_url") val url: String?=null
)
data class CategoryNode(
    val category: Category,
    val children: List<CategoryNode> = emptyList()
)
fun buildCategoryTree(categories: List<Category>): List<CategoryNode> {
    val validCategories = categories.filter { it.name.isNotBlank() }

    val categoryMap = validCategories.groupBy { it.parentId }

    fun buildChildren(parentId: String?): List<CategoryNode> {
        return categoryMap[parentId].orEmpty().map { cat ->
            CategoryNode(
                category = cat,
                children = buildChildren(cat.id)
            )
        }
    }

    return buildChildren(null) // gốc có parentId = null
}
fun printCategoryTree(nodes: List<CategoryNode>, indent: String = "") {
    for (node in nodes) {
        println("$indent- ${node.category.name} (${node.category.id})")
        if (node.children.isNotEmpty()) {
            printCategoryTree(node.children, indent + "  ")
        }
    }
}
fun findCategoryById(id: String, tree: List<CategoryNode>): CategoryNode? {
    for (node in tree) {
        if (node.category.id == id) return node
        val found = findCategoryById(id, node.children)
        if (found != null) return found
    }
    return null
}
