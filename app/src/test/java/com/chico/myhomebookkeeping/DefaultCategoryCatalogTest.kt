package com.chico.myhomebookkeeping

import com.chico.myhomebookkeeping.domain.DefaultCategoryCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultCategoryCatalogTest {

    @Test
    fun allGroupsAreSelectedByDefault() {
        assertTrue(DefaultCategoryCatalog.groups.all { it.isSelectedByDefault })
    }

    @Test
    fun incomeAndProductsAreRequired() {
        val requiredByDefault = DefaultCategoryCatalog.groups
            .filter { it.isRequired }

        assertEquals(DefaultCategoryCatalog.groups.take(2), requiredByDefault)
    }

    @Test
    fun onlyIncomeGroupIsIncome() {
        val incomeGroups = DefaultCategoryCatalog.groups
            .filter { it.isIncome }
            .map { it.parentName }

        assertEquals(listOf("Income"), incomeGroups)
    }

    @Test
    fun sickLeavePaymentsAreIncomeSubcategory() {
        val incomeGroup = DefaultCategoryCatalog.groups.first { it.parentName == "Income" }
        val sickLeaveIndex = incomeGroup.subcategories.indexOf("Sick leave payments")

        assertTrue(sickLeaveIndex >= 0)
        assertEquals("Выплата по больничным листам", incomeGroup.subcategoriesRu[sickLeaveIndex])
        assertEquals("Wypłata za zwolnienie lekarskie", incomeGroup.subcategoriesPl[sickLeaveIndex])
    }

    @Test
    fun otherWithoutCategoryGroupIsNotInCatalog() {
        assertFalse(DefaultCategoryCatalog.groups.any { it.parentName == "Other" })
        assertFalse(
            DefaultCategoryCatalog.groups.any {
                it.parentName == "Other" && "No category" in it.subcategories
            }
        )
        assertTrue(DefaultCategoryCatalog.groups.all { it.subcategories.isNotEmpty() })
    }

    @Test
    fun everyDefaultCategoryHasIcon() {
        DefaultCategoryCatalog.groups.forEach { group ->
            assertEquals(group.subcategories.size, group.subcategoryIcons.size)
        }
    }

    @Test
    fun russianCatalogIsAvailableForRussianLanguage() {
        val groups = DefaultCategoryCatalog.groupsForLanguage("ru")

        assertEquals("Доходы", groups.first().parentName)
        assertTrue(groups.first().subcategories.contains("Другое"))
    }

    @Test
    fun personalCareGroupIsAvailableForFirstLaunch() {
        val group = DefaultCategoryCatalog.groups.first { it.parentName == "Personal Care" }

        assertEquals("Уход за собой", group.parentNameRu)
        assertEquals(
            listOf(
                "Hairdresser",
                "Cosmetics",
                "Body Care",
                "Manicure & Pedicure",
                "Barbershop / Shaving",
                "Perfume & Fragrances",
                "Hygiene Products",
                "Other"
            ),
            group.subcategories
        )
        assertEquals(
            listOf(
                "Парикмахерская",
                "Косметика",
                "Уход за телом",
                "Маникюр и педикюр",
                "Барбершоп / Бритьё",
                "Парфюмерия",
                "Средства гигиены",
                "Другое"
            ),
            group.subcategoriesRu
        )
    }

    @Test
    fun subscriptionsOnlineServicesGroupIsAvailableForFirstLaunch() {
        val group = DefaultCategoryCatalog.groups.first {
            it.parentName == "Subscriptions & Online Services"
        }

        assertEquals("Подписки и онлайн-сервисы", group.parentNameRu)
        assertEquals("Subskrypcje i usługi online", group.parentNamePl)
        assertEquals(
            listOf(
                "Video",
                "Music",
                "Cloud Services",
                "Software & Apps",
                "Games & Gaming Services",
                "Other"
            ),
            group.subcategories
        )
        assertEquals(
            listOf(
                "Видео",
                "Музыка",
                "Облачные сервисы",
                "Программы и приложения",
                "Игры и игровые сервисы",
                "Другие"
            ),
            group.subcategoriesRu
        )
        assertEquals(
            listOf(
                "Wideo",
                "Muzyka",
                "Usługi chmurowe",
                "Programy i aplikacje",
                "Gry i usługi gamingowe",
                "Inne"
            ),
            group.subcategoriesPl
        )
    }
}
