package com.chico.myhomebookkeeping.enums

import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.entity.ParentCategory

enum class ChildCategoriesEnum(val nameRes: Int, val parentCategory: ParentCategoriesEnum) {
    EARNINGS(R.string.earnings, ParentCategoriesEnum.INCOME),
    INTEREST(R.string.interest, ParentCategoriesEnum.INCOME),
    DEBT_REPAYMENT(R.string.debt_repayment, ParentCategoriesEnum.INCOME),
    PREPAID_EXPENSE(R.string.prepaid_expense, ParentCategoriesEnum.INCOME),
    PENSION_PAYMENT(R.string.pension_payment, ParentCategoriesEnum.INCOME),
    GRANTS(R.string.grants, ParentCategoriesEnum.INCOME),

    FUEL(R.string.fuel, ParentCategoriesEnum.VEHICLE),
    REPAIR(R.string.repair, ParentCategoriesEnum.VEHICLE),
    SERVICE(R.string.service, ParentCategoriesEnum.VEHICLE),
    WASHING_DRY_CLEANING(R.string.washing_dry_cleaning, ParentCategoriesEnum.VEHICLE),
    FINE(R.string.fine, ParentCategoriesEnum.VEHICLE),
    PARKING(R.string.parking, ParentCategoriesEnum.VEHICLE),
    INSURANCE_VEHICLE(R.string.insurance, ParentCategoriesEnum.VEHICLE),

    CLOTH(R.string.cloth, ParentCategoriesEnum.CHILDREN),
    SHOES(R.string.shoes, ParentCategoriesEnum.CHILDREN),
    HOBBY(R.string.hobby, ParentCategoriesEnum.CHILDREN),
    EDUCATION(R.string.education, ParentCategoriesEnum.CHILDREN),
    SCHOOL_MEALS(R.string.school_meals, ParentCategoriesEnum.CHILDREN),
    NANNY(R.string.nanny, ParentCategoriesEnum.CHILDREN),
    ALIMONY(R.string.alimony, ParentCategoriesEnum.CHILDREN),

    UTILITY_BILLS(R.string.utility_bills, ParentCategoriesEnum.HOME),
    FURNITURE(R.string.furniture, ParentCategoriesEnum.HOME),
    DETERGENTS(R.string.detergents, ParentCategoriesEnum.HOME),
    HOUSEHOLD_GOODS(R.string.household_goods, ParentCategoriesEnum.HOME),
    TOOLS(R.string.tools, ParentCategoriesEnum.HOME),
    BED_DRESS(R.string.bed_dress, ParentCategoriesEnum.HOME),
    DISHES(R.string.dishes, ParentCategoriesEnum.HOME),
    KITCHENWARE(R.string.kitchenware, ParentCategoriesEnum.HOME),
    REPAIR_HOME(R.string.repair, ParentCategoriesEnum.HOME),

    CARE(R.string.care, ParentCategoriesEnum.PETS),
    TOYS(R.string.toys, ParentCategoriesEnum.PETS),
    FOOD_PETS(R.string.food, ParentCategoriesEnum.PETS),
    DISHES_PETS(R.string.dishes, ParentCategoriesEnum.PETS),
    MEDICAL_SERVICE_PETS(R.string.medical_service, ParentCategoriesEnum.PETS),

    BUS(R.string.bus, ParentCategoriesEnum.PUBLIC_TRANSPORT),
    TROLLEYBUS(R.string.trolleybus, ParentCategoriesEnum.PUBLIC_TRANSPORT),
    TRAM(R.string.tram, ParentCategoriesEnum.PUBLIC_TRANSPORT),
    TRAIN(R.string.train, ParentCategoriesEnum.PUBLIC_TRANSPORT),
    AIRPLANE(R.string.airplane, ParentCategoriesEnum.PUBLIC_TRANSPORT),
    METRO(R.string.metro, ParentCategoriesEnum.PUBLIC_TRANSPORT),

    tests(R.string.tests, ParentCategoriesEnum.MEDICAL_SERVICE),
    polyclinic(R.string.polyclinic, ParentCategoriesEnum.MEDICAL_SERVICE),
    hospital(R.string.hospital, ParentCategoriesEnum.MEDICAL_SERVICE),

    CLOTH_PERSONAL(R.string.cloth, ParentCategoriesEnum.PERSONAL_EXPENSES),
    SHOES_PERSONAL(R.string.shoes, ParentCategoriesEnum.PERSONAL_EXPENSES),
    GARMENTS(R.string.garments, ParentCategoriesEnum.PERSONAL_EXPENSES),
    DECORATIONS(R.string.decorations, ParentCategoriesEnum.PERSONAL_EXPENSES),
    SALON(R.string.salon, ParentCategoriesEnum.PERSONAL_EXPENSES),
    BEAUTY_SALOON(R.string.beauty_saloon, ParentCategoriesEnum.PERSONAL_EXPENSES),
    CINEMA_THEATER(R.string.cinema_theater, ParentCategoriesEnum.PERSONAL_EXPENSES),
    MASSAGE(R.string.massage, ParentCategoriesEnum.PERSONAL_EXPENSES),

    SPORTS_EQUIPMENT(R.string.sports_equipment, ParentCategoriesEnum.SPORT),
    FITNESS_CLUB(R.string.fitness_club, ParentCategoriesEnum.SPORT),
    GYM(R.string.gym, ParentCategoriesEnum.SPORT),

    ALCOHOL(R.string.alcohol, ParentCategoriesEnum.FOOD),
    FOOD_IN_THE_STORE(R.string.food_in_the_store, ParentCategoriesEnum.FOOD),
    BAR(R.string.bar, ParentCategoriesEnum.FOOD),
    RESTAURANT(R.string.restaurant, ParentCategoriesEnum.FOOD),
    CANTEEN(R.string.canteen, ParentCategoriesEnum.FOOD),

    FOR_FRIENDS(R.string.for_friends, ParentCategoriesEnum.GIFTS),
    FOR_CHILDREN(R.string.for_children, ParentCategoriesEnum.GIFTS),

    REPAIR_PHONE(R.string.repair, ParentCategoriesEnum.MOBILE_PHONE),
    SERVICE_PHONE(R.string.service, ParentCategoriesEnum.MOBILE_PHONE),
    PAYMENT(R.string.payment, ParentCategoriesEnum.MOBILE_PHONE),

    CREDIT(R.string.credit, ParentCategoriesEnum.CREDITS),

    TAX(R.string.tax, ParentCategoriesEnum.TAXES),

    MEDICAL_INSURANCE(R.string.medical_insurance, ParentCategoriesEnum.INSURANCE),
    AUTO_INSURANCE(R.string.auto_insurance, ParentCategoriesEnum.INSURANCE);
}