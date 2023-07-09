package com.chico.myhomebookkeeping.domain.mappers

import com.chico.myhomebookkeeping.db.entity.UserParentCategory
import com.chico.myhomebookkeeping.domain.entities.NormalizedCategory

class UserParentCategoryMapper {
    fun map(from: UserParentCategory): NormalizedCategory {
        return NormalizedCategory(
            name = from.name,
            iconRes = from.iconRes,
            isIncome = from.isIncome,
            categoriesId = from.categoriesId
        )
    }
}