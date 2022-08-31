package me.naloaty.shoppinglist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.naloaty.shoppinglist.data.SimpleShopListRepositoryImpl
import me.naloaty.shoppinglist.domain.AddShopItemUseCase
import me.naloaty.shoppinglist.domain.EditShopItemUseCase
import me.naloaty.shoppinglist.domain.GetShopItemUseCase
import me.naloaty.shoppinglist.domain.ShopItem
import java.lang.NumberFormatException

class ShopItemViewModel: ViewModel() {

    private val repository = SimpleShopListRepositoryImpl

    private val getShopItemUseCase = GetShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    private val _errorInputTitle = MutableLiveData<Boolean>()
    val errorInputTitle: LiveData<Boolean>
        get() = _errorInputTitle

    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    private val _closeScreen = MutableLiveData<Unit>()
    val closeScreen: LiveData<Unit>
        get() = _closeScreen

    fun getShopItem(shopItemId: Int) {
        val item = getShopItemUseCase.getShopItem(shopItemId)
        _shopItem.value = item
    }

    fun addShopItem(inputTitle: String?, inputCount: String?) {
        val title = parseTitle(inputTitle)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(title, count)

        if (fieldsValid) {
            val shopItem = ShopItem(title, count, true)
            addShopItemUseCase.addShopItem(shopItem)
            finishWork()
        }
    }

    fun editShopItem(inputTitle: String?, inputCount: String?) {
        val title = parseTitle(inputTitle)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(title, count)

        if (fieldsValid) {
            _shopItem.value?.let {
                val shopItem = it.copy(title = title, count = count)
                editShopItemUseCase.editShopItem(shopItem)
                finishWork()
            }
        }
    }

    private fun parseTitle(inputTitle: String?): String {
        return inputTitle?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (nfe: NumberFormatException) {
            0
        }
    }

    private fun validateInput(title: String, count: Int): Boolean {
        var result = true

        if (title.isBlank()) {
            _errorInputTitle.value = true
            result = false
        }

        if (count <= 0) {
            _errorInputCount.value = true
            result = false
        }

        return result
    }

    public fun resetErrorInputTitle() {
        _errorInputTitle.value = false
    }

    public fun resetErrorInputCount() {
        _errorInputCount.value = false
    }

    private fun finishWork() {
        _closeScreen.value = Unit
    }

}