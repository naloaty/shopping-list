package me.naloaty.shoppinglist.domain

class GetShopListUseCase(private val shopListRepository: ShopListRepository) {

    fun getShopList(): List<ShopItem>
        = shopListRepository.getShopList()
}