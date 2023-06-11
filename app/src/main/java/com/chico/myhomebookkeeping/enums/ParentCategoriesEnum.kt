package com.chico.myhomebookkeeping.enums

import com.chico.myhomebookkeeping.R


enum class ParentCategoriesEnum(val nameRes: Int, val categoryId: Int) {
    INCOME(R.string.description_income, 1),
    VEHICLE(R.string.vehicle, 2),
    CHILDREN(R.string.children, 3),
    HOME(R.string.home, 4),
    PETS(R.string.pets, 5),
    PUBLIC_TRANSPORT(R.string.public_transport, 6),
    MEDICAL_SERVICE(R.string.medical_service, 7),
    PERSONAL_EXPENSES(R.string.personal_expenses, 8),
    SPORT(R.string.sport, 9),
    FOOD(R.string.food, 10),
    GIFTS(R.string.gifts, 11),
    MOBILE_PHONE(R.string.mobile_phone, 12),
    CREDITS(R.string.credits, 13),
    TAXES(R.string.taxes, 14),
    INSURANCE(R.string.insurance, 15);
}

fun Int.toParentCategoriesEnum(): ParentCategoriesEnum {
    return when (this) {
        1 -> ParentCategoriesEnum.INCOME
        2 -> ParentCategoriesEnum.VEHICLE
        3 -> ParentCategoriesEnum.CHILDREN
        4 -> ParentCategoriesEnum.HOME
        5 -> ParentCategoriesEnum.PETS
        6 -> ParentCategoriesEnum.PUBLIC_TRANSPORT
        7 -> ParentCategoriesEnum.MEDICAL_SERVICE
        8 -> ParentCategoriesEnum.PERSONAL_EXPENSES
        9 -> ParentCategoriesEnum.SPORT
        10 -> ParentCategoriesEnum.FOOD
        11 -> ParentCategoriesEnum.GIFTS
        12 -> ParentCategoriesEnum.MOBILE_PHONE
        13 -> ParentCategoriesEnum.CREDITS
        14 -> ParentCategoriesEnum.TAXES
        15 -> ParentCategoriesEnum.INSURANCE
        else -> ParentCategoriesEnum.INCOME
    }
}

fun Int.fromNameResToParentCategoriesEnum(): ParentCategoriesEnum {
    return when (this) {
        R.string.description_income -> ParentCategoriesEnum.INCOME
        R.string.vehicle -> ParentCategoriesEnum.VEHICLE
        R.string.children -> ParentCategoriesEnum.CHILDREN
        R.string.home-> ParentCategoriesEnum.HOME
        R.string.pets-> ParentCategoriesEnum.PETS
        R.string.public_transport-> ParentCategoriesEnum.PUBLIC_TRANSPORT
        R.string.medical_service-> ParentCategoriesEnum.MEDICAL_SERVICE
        R.string.personal_expenses-> ParentCategoriesEnum.PERSONAL_EXPENSES
        R.string.sport-> ParentCategoriesEnum.SPORT
        R.string.food -> ParentCategoriesEnum.FOOD
        R.string.gifts -> ParentCategoriesEnum.GIFTS
        R.string.mobile_phone -> ParentCategoriesEnum.MOBILE_PHONE
        R.string.credits -> ParentCategoriesEnum.CREDITS
        R.string.taxes -> ParentCategoriesEnum.TAXES
        R.string.insurance -> ParentCategoriesEnum.INSURANCE
        else -> ParentCategoriesEnum.INCOME
    }
}