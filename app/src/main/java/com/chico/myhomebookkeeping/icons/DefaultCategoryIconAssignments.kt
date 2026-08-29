package com.chico.myhomebookkeeping.icons

import com.chico.myhomebookkeeping.domain.DefaultCategoryGroup

object DefaultCategoryIconAssignments {
    private data class GroupIcons(val parent: String, val children: List<String>)

    private val assignments = mapOf(
        "Income" to GroupIcons("account_balance_wallet", listOf("payments", "account_balance", "work", "paid", "medical_services", "redeem", "storefront", "price_check", "trending_up", "more_horiz")),
        "Groceries" to GroupIcons("grocery", listOf("shopping_basket", "storefront", "local_drink", "delivery_dining", "more_horiz")),
        "Cafes and restaurants" to GroupIcons("restaurant", listOf("local_cafe", "table_restaurant", "fastfood", "delivery_dining", "coffee", "more_horiz")),
        "Car" to GroupIcons("directions_car", listOf("credit_card", "local_gas_station", "handyman", "car_repair", "shield", "local_parking", "toll", "more_horiz")),
        "Public transport" to GroupIcons("directions_bus", listOf("confirmation_number", "directions_bus", "directions_railway", "directions_bus", "local_taxi", "more_horiz")),
        "Home" to GroupIcons("home", listOf("home_work", "water_drop", "account_balance", "router", "chair", "format_paint", "home_repair_service", "cleaning_services", "electrical_services", "kitchen", "bed", "more_horiz")),
        "Clothing" to GroupIcons("checkroom", listOf("apparel", "footprint", "jewelry", "more_horiz")),
        "Health" to GroupIcons("medical_services", listOf("local_pharmacy", "stethoscope", "dentistry", "biotech", "shield", "fitness_center", "more_horiz")),
        "Personal Care" to GroupIcons("spa", listOf("content_cut", "face_3", "shower", "styler", "content_cut", "local_florist", "sanitizer", "more_horiz")),
        "Children" to GroupIcons("child_friendly", listOf("child_care", "school", "toys", "checkroom", "sports_soccer", "more_horiz")),
        "Pets" to GroupIcons("pets", listOf("pet_supplies", "medical_services", "loyalty", "more_horiz")),
        "Mobile and internet" to GroupIcons("phone", listOf("smartphone", "credit_card", "phone", "wifi", "subscriptions", "apps", "more_horiz")),
        "Subscriptions & Online Services" to GroupIcons("subscriptions", listOf("movie", "music_note", "cloud", "laptop_mac", "sports_esports", "more_horiz")),
        "Entertainment" to GroupIcons("theater_comedy", listOf("movie", "videogame_asset", "headphones", "book", "brush", "flight", "more_horiz")),
        "Gifts" to GroupIcons("redeem", listOf("family_restroom", "group", "volunteer_activism", "more_horiz")),
        "Work" to GroupIcons("business_center", listOf("handyman", "edit_note", "school", "luggage", "more_horiz")),
        "Documents" to GroupIcons("description", listOf("passport", "id_card", "description", "gavel", "g_translate", "more_horiz")),
        "Finance" to GroupIcons("account_balance", listOf("receipt_long", "credit_card_clock", "percent", "gavel", "swap_horiz", "more_horiz"))
    )

    fun parentKey(group: DefaultCategoryGroup): String =
        assignments[group.parentName]?.parent
            ?: CategoryIconCatalog.canonicalKey(group.parentIcon.name)
            ?: CategoryIconCatalog.DEFAULT_KEY

    fun childKey(group: DefaultCategoryGroup, index: Int): String =
        assignments[group.parentName]?.children?.getOrNull(index)
            ?: group.subcategoryIcons.getOrNull(index)?.let { CategoryIconCatalog.canonicalKey(it.name) }
            ?: CategoryIconCatalog.DEFAULT_KEY

    fun parentKey(parentName: String): String? = assignments[parentName]?.parent

    fun childKey(parentName: String, index: Int): String? =
        assignments[parentName]?.children?.getOrNull(index)
}
