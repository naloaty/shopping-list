package me.naloaty.shoppinglist.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.naloaty.shoppinglist.data.SimpleShopListRepositoryImpl
import me.naloaty.shoppinglist.domain.DeleteShopItemUseCase
import me.naloaty.shoppinglist.domain.EditShopItemUseCase
import me.naloaty.shoppinglist.domain.GetShopListUseCase
import me.naloaty.shoppinglist.domain.ShopItem

class MainViewModel : ViewModel() {

    private val repository = SimpleShopListRepositoryImpl

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = getShopListUseCase.getShopList()

    fun deleteShopItem(shopItem: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(shopItem)
    }

    fun toggleEnabledState(shopItem: ShopItem) {
        val newItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(newItem)
    }
}