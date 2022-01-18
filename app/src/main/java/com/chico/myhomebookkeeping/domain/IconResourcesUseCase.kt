package com.chico.myhomebookkeeping.domain

import com.chico.myhomebookkeeping.db.dao.IconResourcesDao
import com.chico.myhomebookkeeping.db.entity.IconsResource

object IconResourcesUseCase {
    suspend fun addNewIconResource(
        db:IconResourcesDao,
        newIcon:IconsResource
    ):Long{
        return db.addNewIcon(newIcon)
    }

    suspend fun getIconsList(db: IconResourcesDao): List<IconsResource> {
        return db.getListIcons()
    }
}