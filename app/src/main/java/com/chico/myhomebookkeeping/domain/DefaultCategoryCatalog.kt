package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.enums.icon.names.CategoryIconNames
import com.chico.myhomebookkeeping.obj.Constants

data class DefaultCategoryGroup(
    val parentName: String,
    val parentNameRu: String,
    val parentIcon: CategoryIconNames,
    val isIncome: Boolean,
    val isSelectedByDefault: Boolean = true,
    val isRequired: Boolean = false,
    val subcategories: List<String>,
    val subcategoriesRu: List<String>,
    val subcategoryIcons: List<CategoryIconNames>
) {
    val parentNamePl: String
        get() = DefaultPolishNames.parentCategoryName(parentName)

    val subcategoriesPl: List<String>
        get() = subcategories.map(DefaultPolishNames::categoryName)
}

object DefaultCategoryCatalog {
    val groups: List<DefaultCategoryGroup> = listOf(
        group(
            parentName = "Income",
            parentNameRu = "Доходы",
            parentIcon = CategoryIconNames.Wallet,
            isIncome = true,
            isRequired = true,
            subcategories = listOf("Salary", "Pension", "Side job", "Benefits", "Sick leave payments", "Gifts", "Sales", "Cashback", "Interest and investments", "Other"),
            subcategoriesRu = listOf("Выплата", "Пенсия", "Подработка", "Пособия", "Выплата по больничным листам", "Подарки", "Продажи", "Кэшбэк", "Проценты и инвестиции", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Wallet, CategoryIconNames.Wallet, CategoryIconNames.Person, CategoryIconNames.Wallet, CategoryIconNames.Medical, CategoryIconNames.CardGiftcard, CategoryIconNames.Store, CategoryIconNames.Receipt, CategoryIconNames.Bank, CategoryIconNames.Wallet)
        ),
        group(
            parentName = "Groceries",
            parentNameRu = "Продукты",
            parentIcon = CategoryIconNames.ShoppingCart,
            isRequired = true,
            subcategories = listOf("Supermarket", "Market", "Drinks", "Grocery delivery", "Other"),
            subcategoriesRu = listOf("Супермаркет", "Рынок", "Напитки", "Доставка продуктов", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.ShoppingCart, CategoryIconNames.Store, CategoryIconNames.ShoppingCart, CategoryIconNames.ShoppingCartAdd, CategoryIconNames.ShoppingCart)
        ),
        group(
            parentName = "Cafes and restaurants",
            parentNameRu = "Кафе и рестораны",
            parentIcon = CategoryIconNames.Restaurant,
            subcategories = listOf("Cafe", "Restaurant", "Fast food", "Food delivery", "Coffee", "Other"),
            subcategoriesRu = listOf("Кафе", "Ресторан", "Фастфуд", "Доставка еды", "Кофе", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Coffee, CategoryIconNames.Restaurant, CategoryIconNames.Restaurant, CategoryIconNames.ShoppingCartAdd, CategoryIconNames.Coffee, CategoryIconNames.Restaurant)
        ),
        group(
            parentName = "Car",
            parentNameRu = "Автомобиль",
            parentIcon = CategoryIconNames.Car,
            subcategories = listOf("Loan", "Fuel", "Maintenance", "Repair", "Insurance", "Parking", "Toll roads", "Other"),
            subcategoriesRu = listOf("Кредит", "Топливо", "Обслуживание", "Ремонт", "Страховка", "Парковка", "Платные дороги", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Bank, CategoryIconNames.GasStation, CategoryIconNames.Build, CategoryIconNames.Build, CategoryIconNames.Policy, CategoryIconNames.LocalParking, CategoryIconNames.Receipt, CategoryIconNames.Car)
        ),
        group(
            parentName = "Public transport",
            parentNameRu = "Общественный транспорт",
            parentIcon = CategoryIconNames.Bus,
            subcategories = listOf("Transit pass", "City transport", "Railway", "Intercity bus", "Taxi", "Other"),
            subcategoriesRu = listOf("Проездной", "Городской транспорт", "Железная дорога", "Междугородний автобус", "Такси", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Receipt, CategoryIconNames.Bus, CategoryIconNames.Train, CategoryIconNames.Bus, CategoryIconNames.LocalTaxi, CategoryIconNames.Bus)
        ),
        group(
            parentName = "Home",
            parentNameRu = "Дом",
            parentIcon = CategoryIconNames.House,
            subcategories = listOf("Rent", "Utilities", "Loan", "Internet", "Furniture", "Interior and decor", "Repair", "Household chemicals", "Appliances", "Kitchenware", "Home textiles", "Other"),
            subcategoriesRu = listOf("Аренда", "Коммунальные услуги", "Кредит", "Интернет", "Мебель", "Интерьер и декор", "Ремонт", "Бытовая химия", "Техника", "Кухонная утварь", "Домашний текстиль", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.House, CategoryIconNames.Receipt, CategoryIconNames.Bank, CategoryIconNames.Computer, CategoryIconNames.House, CategoryIconNames.Apartment, CategoryIconNames.Build, CategoryIconNames.CleaningServices, CategoryIconNames.Kitchen, CategoryIconNames.Kitchen, CategoryIconNames.Checkroom, CategoryIconNames.House)
        ),
        group(
            parentName = "Clothing",
            parentNameRu = "Одежда",
            parentIcon = CategoryIconNames.Checkroom,
            subcategories = listOf("Clothing", "Shoes", "Accessories", "Other"),
            subcategoriesRu = listOf("Одежда", "Обувь", "Аксессуары", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Checkroom, CategoryIconNames.Checkroom, CategoryIconNames.Checkroom, CategoryIconNames.Checkroom)
        ),
        group(
            parentName = "Health",
            parentNameRu = "Здоровье",
            parentIcon = CategoryIconNames.Medical,
            subcategories = listOf("Pharmacy", "Doctor", "Dentist", "Lab tests", "Insurance", "Sport", "Other"),
            subcategoriesRu = listOf("Аптека", "Врач", "Стоматология", "Анализы", "Страхование", "Спорт", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Medical, CategoryIconNames.Medical, CategoryIconNames.Medical, CategoryIconNames.Medical, CategoryIconNames.Policy, CategoryIconNames.Sports, CategoryIconNames.Medical)
        ),
        group(
            parentName = "Personal Care",
            parentNameRu = "Уход за собой",
            parentIcon = CategoryIconNames.Salon,
            subcategories = listOf("Hairdresser", "Cosmetics", "Body Care", "Manicure & Pedicure", "Barbershop / Shaving", "Perfume & Fragrances", "Hygiene Products", "Other"),
            subcategoriesRu = listOf("Парикмахерская", "Косметика", "Уход за телом", "Маникюр и педикюр", "Барбершоп / Бритьё", "Парфюмерия", "Средства гигиены", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Salon, CategoryIconNames.Salon, CategoryIconNames.Person, CategoryIconNames.Salon, CategoryIconNames.Salon, CategoryIconNames.Salon, CategoryIconNames.CleaningServices, CategoryIconNames.Salon)
        ),
        group(
            parentName = "Children",
            parentNameRu = "Дети",
            parentIcon = CategoryIconNames.ChildFriendly,
            subcategories = listOf("Kindergarten", "School", "Toys", "Clothing", "Clubs", "Other"),
            subcategoriesRu = listOf("Детский сад", "Школа", "Игрушки", "Одежда", "Кружки", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.ChildFriendly, CategoryIconNames.School, CategoryIconNames.ChildFriendly, CategoryIconNames.Checkroom, CategoryIconNames.Sports, CategoryIconNames.ChildFriendly)
        ),
        group(
            parentName = "Pets",
            parentNameRu = "Животные",
            parentIcon = CategoryIconNames.Pets,
            subcategories = listOf("Food", "Veterinarian", "Accessories", "Other"),
            subcategoriesRu = listOf("Корм", "Ветеринар", "Аксессуары", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Pets, CategoryIconNames.Medical, CategoryIconNames.Pets, CategoryIconNames.Pets)
        ),
        group(
            parentName = "Mobile and internet",
            parentNameRu = "Связь",
            parentIcon = CategoryIconNames.Phone,
            subcategories = listOf("Phone purchase", "Phone loan", "Mobile service", "Internet", "Subscriptions", "Apps", "Other"),
            subcategoriesRu = listOf("Покупка телефона", "Кредит на телефон", "Мобильная связь", "Интернет", "Подписки", "Приложения", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.PhoneIphone, CategoryIconNames.Bank, CategoryIconNames.PhoneAndroid, CategoryIconNames.Computer, CategoryIconNames.Subscriptions, CategoryIconNames.Phone, CategoryIconNames.Phone)
        ),
        group(
            parentName = "Entertainment",
            parentNameRu = "Развлечения",
            parentIcon = CategoryIconNames.Celebration,
            subcategories = listOf("Movies", "Games", "Music", "Books", "Hobbies", "Travel", "Other"),
            subcategoriesRu = listOf("Кино", "Игры", "Музыка", "Книги", "Хобби", "Путешествия", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Subscriptions, CategoryIconNames.Computer, CategoryIconNames.MusicNote, CategoryIconNames.Book, CategoryIconNames.Celebration, CategoryIconNames.Airplane, CategoryIconNames.Celebration)
        ),
        group(
            parentName = "Gifts",
            parentNameRu = "Подарки",
            parentIcon = CategoryIconNames.CardGiftcard,
            subcategories = listOf("Family", "Friends", "Charity", "Other"),
            subcategoriesRu = listOf("Семья", "Друзья", "Благотворительность", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.People, CategoryIconNames.People, CategoryIconNames.VolunteerActivism, CategoryIconNames.CardGiftcard)
        ),
        group(
            parentName = "Work",
            parentNameRu = "Работа",
            parentIcon = CategoryIconNames.BusinessCenter,
            subcategories = listOf("Tools", "Stationery", "Education", "Business trips", "Other"),
            subcategoriesRu = listOf("Инструменты", "Канцелярия", "Обучение", "Командировки", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Build, CategoryIconNames.Description, CategoryIconNames.School, CategoryIconNames.Airplane, CategoryIconNames.BusinessCenter)
        ),
        group(
            parentName = "Documents",
            parentNameRu = "Документы",
            parentIcon = CategoryIconNames.Description,
            subcategories = listOf("Passport", "Visa", "Permits", "Notary", "Document translation", "Other"),
            subcategoriesRu = listOf("Паспорт", "Виза", "Разрешения", "Нотариус", "Переводы документов", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Description, CategoryIconNames.Description, CategoryIconNames.Policy, CategoryIconNames.Gavel, CategoryIconNames.Description, CategoryIconNames.Description)
        ),
        group(
            parentName = "Finance",
            parentNameRu = "Финансы",
            parentIcon = CategoryIconNames.Bank,
            subcategories = listOf("Bank fees", "Transfer fee", "Taxes", "Fines", "Transfers", "Other"),
            subcategoriesRu = listOf("Банковские комиссии", "Комиссия за перевод", "Налоги", "Штрафы", "Переводы", "Другое"),
            subcategoryIcons = listOf(CategoryIconNames.Receipt, CategoryIconNames.Receipt, CategoryIconNames.Receipt, CategoryIconNames.Policy, CategoryIconNames.ArrowsHorizontal, CategoryIconNames.Bank)
        )
    )

    fun groupsForLanguage(languageTag: String): List<DefaultCategoryGroup> {
        return when (languageTag) {
            Constants.APP_LANGUAGE_RUSSIAN -> groups.map {
                it.copy(
                    parentName = it.parentNameRu,
                    subcategories = it.subcategoriesRu
                )
            }
            Constants.APP_LANGUAGE_POLISH -> groups.map {
                it.copy(
                    parentName = it.parentNamePl,
                    subcategories = it.subcategoriesPl
                )
            }
            else -> groups
        }
    }

    private fun group(
        parentName: String,
        parentNameRu: String,
        parentIcon: CategoryIconNames,
        isIncome: Boolean = false,
        isSelectedByDefault: Boolean = true,
        isRequired: Boolean = false,
        subcategories: List<String>,
        subcategoriesRu: List<String>,
        subcategoryIcons: List<CategoryIconNames>
    ): DefaultCategoryGroup {
        require(subcategories.size == subcategoriesRu.size)
        require(subcategories.size == subcategoryIcons.size)
        return DefaultCategoryGroup(
            parentName = parentName,
            parentNameRu = parentNameRu,
            parentIcon = parentIcon,
            isIncome = isIncome,
            isSelectedByDefault = isSelectedByDefault,
            isRequired = isRequired,
            subcategories = subcategories,
            subcategoriesRu = subcategoriesRu,
            subcategoryIcons = subcategoryIcons
        )
    }
}
