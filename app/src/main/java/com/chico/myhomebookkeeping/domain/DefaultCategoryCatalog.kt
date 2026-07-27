package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.obj.Constants

data class DefaultCategoryGroup(
    val parentName: String,
    val parentNameRu: String,
    val isIncome: Boolean,
    val isSelectedByDefault: Boolean = true,
    val isRequired: Boolean = false,
    val subcategories: List<String>,
    val subcategoriesRu: List<String>
)

object DefaultCategoryCatalog {
    val groups: List<DefaultCategoryGroup> = listOf(
        group(
            parentName = "Income",
            parentNameRu = "Доходы",
            isIncome = true,
            isRequired = true,
            subcategories = listOf("Salary", "Pension", "Side job", "Benefits", "Gifts", "Sales", "Cashback", "Interest and investments", "Other"),
            subcategoriesRu = listOf("Выплата", "Пенсия", "Подработка", "Пособия", "Подарки", "Продажи", "Кэшбэк", "Проценты и инвестиции", "Другое")
        ),
        group(
            parentName = "Groceries",
            parentNameRu = "Продукты",
            isRequired = true,
            subcategories = listOf("Supermarket", "Market", "Drinks", "Grocery delivery", "Other"),
            subcategoriesRu = listOf("Супермаркет", "Рынок", "Напитки", "Доставка продуктов", "Другое")
        ),
        group(
            parentName = "Cafes and restaurants",
            parentNameRu = "Кафе и рестораны",
            subcategories = listOf("Cafe", "Restaurant", "Fast food", "Food delivery", "Coffee", "Other"),
            subcategoriesRu = listOf("Кафе", "Ресторан", "Фастфуд", "Доставка еды", "Кофе", "Другое")
        ),
        group(
            parentName = "Car",
            parentNameRu = "Автомобиль",
            subcategories = listOf("Loan", "Fuel", "Maintenance", "Repair", "Insurance", "Parking", "Toll roads", "Other"),
            subcategoriesRu = listOf("Кредит", "Топливо", "Обслуживание", "Ремонт", "Страховка", "Парковка", "Платные дороги", "Другое")
        ),
        group(
            parentName = "Public transport",
            parentNameRu = "Общественный транспорт",
            subcategories = listOf("Transit pass", "City transport", "Railway", "Intercity bus", "Taxi", "Other"),
            subcategoriesRu = listOf("Проездной", "Городской транспорт", "Железная дорога", "Междугородний автобус", "Такси", "Другое")
        ),
        group(
            parentName = "Home",
            parentNameRu = "Дом",
            subcategories = listOf("Rent", "Utilities", "Loan", "Internet", "Furniture", "Interior and decor", "Repair", "Household chemicals", "Appliances", "Kitchenware", "Home textiles", "Other"),
            subcategoriesRu = listOf("Аренда", "Коммунальные услуги", "Кредит", "Интернет", "Мебель", "Интерьер и декор", "Ремонт", "Бытовая химия", "Техника", "Кухонная утварь", "Домашний текстиль", "Другое")
        ),
        group(
            parentName = "Clothing",
            parentNameRu = "Одежда",
            subcategories = listOf("Clothing", "Shoes", "Accessories", "Other"),
            subcategoriesRu = listOf("Одежда", "Обувь", "Аксессуары", "Другое")
        ),
        group(
            parentName = "Health",
            parentNameRu = "Здоровье",
            subcategories = listOf("Pharmacy", "Doctor", "Dentist", "Lab tests", "Insurance", "Sport", "Other"),
            subcategoriesRu = listOf("Аптека", "Врач", "Стоматология", "Анализы", "Страхование", "Спорт", "Другое")
        ),
        group(
            parentName = "Children",
            parentNameRu = "Дети",
            subcategories = listOf("Kindergarten", "School", "Toys", "Clothing", "Clubs", "Other"),
            subcategoriesRu = listOf("Детский сад", "Школа", "Игрушки", "Одежда", "Кружки", "Другое")
        ),
        group(
            parentName = "Pets",
            parentNameRu = "Животные",
            subcategories = listOf("Food", "Veterinarian", "Accessories", "Other"),
            subcategoriesRu = listOf("Корм", "Ветеринар", "Аксессуары", "Другое")
        ),
        group(
            parentName = "Mobile and internet",
            parentNameRu = "Связь",
            subcategories = listOf("Phone purchase", "Phone loan", "Mobile service", "Internet", "Subscriptions", "Apps", "Other"),
            subcategoriesRu = listOf("Покупка телефона", "Кредит на телефон", "Мобильная связь", "Интернет", "Подписки", "Приложения", "Другое")
        ),
        group(
            parentName = "Entertainment",
            parentNameRu = "Развлечения",
            subcategories = listOf("Movies", "Games", "Music", "Books", "Hobbies", "Travel", "Other"),
            subcategoriesRu = listOf("Кино", "Игры", "Музыка", "Книги", "Хобби", "Путешествия", "Другое")
        ),
        group(
            parentName = "Gifts",
            parentNameRu = "Подарки",
            subcategories = listOf("Family", "Friends", "Charity", "Other"),
            subcategoriesRu = listOf("Семья", "Друзья", "Благотворительность", "Другое")
        ),
        group(
            parentName = "Work",
            parentNameRu = "Работа",
            subcategories = listOf("Tools", "Stationery", "Education", "Business trips", "Other"),
            subcategoriesRu = listOf("Инструменты", "Канцелярия", "Обучение", "Командировки", "Другое")
        ),
        group(
            parentName = "Documents",
            parentNameRu = "Документы",
            subcategories = listOf("Passport", "Visa", "Permits", "Notary", "Document translation", "Other"),
            subcategoriesRu = listOf("Паспорт", "Виза", "Разрешения", "Нотариус", "Переводы документов", "Другое")
        ),
        group(
            parentName = "Finance",
            parentNameRu = "Финансы",
            subcategories = listOf("Bank fees", "Taxes", "Fines", "Transfers", "Other"),
            subcategoriesRu = listOf("Банковские комиссии", "Налоги", "Штрафы", "Переводы", "Другое")
        )
    )

    fun groupsForLanguage(languageTag: String): List<DefaultCategoryGroup> {
        return if (languageTag == Constants.APP_LANGUAGE_RUSSIAN) {
            groups.map {
                it.copy(parentName = it.parentNameRu, subcategories = it.subcategoriesRu)
            }
        } else {
            groups
        }
    }

    private fun group(
        parentName: String,
        parentNameRu: String,
        isIncome: Boolean = false,
        isSelectedByDefault: Boolean = true,
        isRequired: Boolean = false,
        subcategories: List<String>,
        subcategoriesRu: List<String>
    ): DefaultCategoryGroup {
        require(subcategories.size == subcategoriesRu.size)
        return DefaultCategoryGroup(
            parentName = parentName,
            parentNameRu = parentNameRu,
            isIncome = isIncome,
            isSelectedByDefault = isSelectedByDefault,
            isRequired = isRequired,
            subcategories = subcategories,
            subcategoriesRu = subcategoriesRu
        )
    }
}
