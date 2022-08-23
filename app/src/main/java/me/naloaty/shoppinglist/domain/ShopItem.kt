package me.naloaty.shoppinglist.domain

data class ShopItem(
    val id: Int,
    val title: String,
    val count: Int,
    val enabled: Boolean
)