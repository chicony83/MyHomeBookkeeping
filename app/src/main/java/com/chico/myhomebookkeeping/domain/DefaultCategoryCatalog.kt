package com.chico.myhomebookkeeping.domain

data class DefaultCategoryGroup(
    val parentName: String,
    val isIncome: Boolean,
    val isSelectedByDefault: Boolean = true,
    val isRequired: Boolean = false,
    val subcategories: List<String>
)

object DefaultCategoryCatalog {
    val groups: List<DefaultCategoryGroup> = listOf(
        DefaultCategoryGroup(
            parentName = "Доходы",
            isIncome = true,
            isSelectedByDefault = true,
            isRequired = true,
            subcategories = listOf(
                "Выплата",
                "Пенсия",
                "Подработка",
                "Пособия",
                "Подарки",
                "Продажи",
                "Кэшбэк",
                "Проценты и инвестиции",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Продукты",
            isIncome = false,
            isSelectedByDefault = true,
            isRequired = true,
            subcategories = listOf(
                "Супермаркет",
                "Рынок",
                "Напитки",
                "Доставка продуктов",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Кафе и рестораны",
            isIncome = false,
            subcategories = listOf(
                "Кафе",
                "Ресторан",
                "Фастфуд",
                "Доставка еды",
                "Кофе",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Автомобиль",
            isIncome = false,
            subcategories = listOf(
                "Кредит",
                "Топливо",
                "Обслуживание",
                "Ремонт",
                "Страховка",
                "Парковка",
                "Платные дороги",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Общественный транспорт",
            isIncome = false,
            subcategories = listOf(
                "Проездной",
                "Городской транспорт",
                "Железная дорога",
                "Междугородний автобус",
                "Такси",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Дом",
            isIncome = false,
            subcategories = listOf(
                "Аренда",
                "Коммунальные услуги",
                "Кредит",
                "Интернет",
                "Мебель",
                "Интерьер и декор",
                "Ремонт",
                "Бытовая химия",
                "Техника",
                "Кухонная утварь",
                "Домашний текстиль",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Одежда",
            isIncome = false,
            subcategories = listOf(
                "Одежда",
                "Обувь",
                "Аксессуары",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Здоровье",
            isIncome = false,
            subcategories = listOf(
                "Аптека",
                "Врач",
                "Стоматология",
                "Анализы",
                "Страхование",
                "Спорт",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Дети",
            isIncome = false,
            subcategories = listOf(
                "Детский сад",
                "Школа",
                "Игрушки",
                "Одежда",
                "Кружки",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Животные",
            isIncome = false,
            subcategories = listOf(
                "Корм",
                "Ветеринар",
                "Аксессуары",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Связь",
            isIncome = false,
            subcategories = listOf(
                "Покупка телефона",
                "Кредит на телефон",
                "Мобильная связь",
                "Интернет",
                "Подписки",
                "Приложения",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Развлечения",
            isIncome = false,
            subcategories = listOf(
                "Кино",
                "Игры",
                "Музыка",
                "Книги",
                "Хобби",
                "Путешествия",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Подарки",
            isIncome = false,
            subcategories = listOf(
                "Семья",
                "Друзья",
                "Благотворительность",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Работа",
            isIncome = false,
            subcategories = listOf(
                "Инструменты",
                "Канцелярия",
                "Обучение",
                "Командировки",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Документы",
            isIncome = false,
            subcategories = listOf(
                "Паспорт",
                "Виза",
                "Разрешения",
                "Нотариус",
                "Переводы документов",
                "Другое"
            )
        ),
        DefaultCategoryGroup(
            parentName = "Финансы",
            isIncome = false,
            subcategories = listOf(
                "Банковские комиссии",
                "Налоги",
                "Штрафы",
                "Переводы",
                "Другое"
            )
        )
    )
}
