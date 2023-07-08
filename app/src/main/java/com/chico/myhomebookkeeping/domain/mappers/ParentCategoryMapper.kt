package com.chico.myhomebookkeeping.domain.mappers

import com.chico.myhomebookkeeping.db.entity.ParentCategory
import com.chico.myhomebookkeeping.domain.entities.NormalizedCategory

class ParentCategoryMapper {
    fun map(from: ParentCategory): NormalizedCategory {
        return NormalizedCategory(
            nameRes = from.nameRes,
            iconRes = from.iconRes,
            isIncome = from.isIncome,
            categoriesId = from.categoriesId
        )
    }
}