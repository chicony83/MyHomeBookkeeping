package com.chico.myhomebookkeeping.icons

/**
 * The app-owned keys for category icons. Their values intentionally match Material Symbol
 * ligature names so the catalog can grow without adding one drawable XML per icon.
 */
object CategoryIconCatalog {
    const val DEFAULT_KEY = "category"

    val keys = listOf(
        "account_balance", "account_balance_wallet", "account_circle", "add_card", "apparel",
        "add_shopping_cart", "all_inclusive", "apartment", "apps", "arrow_drop_down", "arrow_drop_up",
        "attach_money", "auto_stories", "bakery_dining", "badge", "bed", "biotech", "bolt", "book",
        "bookmark", "brush", "business_center", "cake", "calendar_month", "campaign", "car_repair", "category",
        "celebration", "chair", "checkroom", "child_care", "child_friendly", "cleaning_services",
        "chat", "check_circle", "church", "cloud", "coffee", "commute", "computer", "connecting_airports", "construction",
        "confirmation_number", "content_cut", "credit_card", "credit_card_clock", "currency_exchange", "delivery_dining", "dentistry",
        "description", "devices", "directions_bike", "directions_boat", "directions_bus", "directions_walk",
        "directions_car", "directions_railway", "eco", "edit", "electric_car", "electrical_services",
        "euro", "event", "edit_note", "face", "face_3", "family_restroom", "family_star", "favorite", "fitness_center", "flight", "footprint",
        "folder", "format_paint", "fastfood", "gavel", "g_translate", "grocery", "group", "handyman", "headphones", "hiking", "home", "home_repair_service", "home_work",
        "hotel", "id_card", "inventory", "inventory_2", "jewelry", "key", "kitchen", "language", "laptop_mac", "lightbulb", "local_bar",
        "local_cafe", "local_car_wash", "local_drink", "local_florist", "local_gas_station", "local_grocery_store",
        "local_hospital", "local_laundry_service", "local_mall", "local_parking", "local_pharmacy", "local_shipping", "loyalty",
        "local_taxi", "luggage", "lunch_dining", "mail", "map", "medication", "medical_services",
        "more_horiz", "movie", "music_note", "paid", "palette", "passport", "payments", "percent", "person",
        "person_add", "pet_supplies", "pets", "phone", "phone_android", "phone_iphone", "photo_camera", "plumbing",
        "policy", "price_check", "public", "receipt_long", "redeem", "recycling", "receipt", "restaurant", "restaurant_menu",
        "router", "sanitizer", "savings", "school", "security", "self_improvement", "sell", "shield", "shower", "shopping_bag",
        "shopping_basket", "shopping_cart", "smartphone", "spa", "sports_esports", "sports_soccer", "stethoscope", "storefront",
        "styler", "table_restaurant", "science", "star", "subscriptions", "swap_horiz", "theater_comedy", "tire_repair", "toll", "toys", "traffic", "train", "travel_explore",
        "trending_down", "trending_up", "tv", "two_wheeler", "videocam",
        "videogame_asset", "volunteer_activism", "water_drop", "weekend", "wifi", "work"
    )

    private val legacyKeys = mapOf(
        "Apartment" to "apartment",
        "Airplane" to "flight",
        "ArrowsHorizontal" to "swap_horiz",
        "ArrowDropDown" to "arrow_drop_down",
        "ArrowDropUp" to "arrow_drop_up",
        "Bank" to "account_balance",
        "Build" to "construction",
        "Bus" to "directions_bus",
        "Cake" to "cake",
        "CardGiftcard" to "redeem",
        "Car" to "directions_car",
        "Celebration" to "celebration",
        "ChildFriendly" to "child_friendly",
        "Checkroom" to "checkroom",
        "CleaningServices" to "cleaning_services",
        "Cloud" to "cloud",
        "Coffee" to "coffee",
        "Computer" to "computer",
        "Description" to "description",
        "GasStation" to "local_gas_station",
        "Gavel" to "gavel",
        "House" to "home",
        "Kitchen" to "restaurant_menu",
        "LocalParking" to "local_parking",
        "LocalTaxi" to "local_taxi",
        "Medical" to "medical_services",
        "MusicNote" to "music_note",
        "Park" to "eco",
        "PedalBike" to "directions_bike",
        "People" to "group",
        "Person" to "person",
        "Pets" to "pets",
        "Phone" to "phone",
        "PhoneAndroid" to "phone_android",
        "PhoneIphone" to "phone_iphone",
        "Policy" to "policy",
        "Receipt" to "receipt",
        "Restaurant" to "restaurant",
        "Salon" to "content_cut",
        "School" to "school",
        "ShoppingCart" to "shopping_cart",
        "ShoppingCartAdd" to "add_shopping_cart",
        "Sports" to "sports_soccer",
        "Store" to "storefront",
        "Subscriptions" to "subscriptions",
        "Subway" to "commute",
        "Train" to "train",
        "TwoWheeler" to "two_wheeler",
        "Apps" to "apps",
        "Videocam" to "videocam",
        "VideogameAsset" to "videogame_asset",
        "VolunteerActivism" to "volunteer_activism",
        "Wallet" to "account_balance_wallet",
        "Book" to "book",
        "BusinessCenter" to "business_center"
    )

    fun canonicalKey(key: String?): String? = when {
        key == null -> null
        key in keys -> key
        else -> legacyKeys[key]
    }

    fun legacyCanonicalKey(key: String): String? = legacyKeys[key]
}
