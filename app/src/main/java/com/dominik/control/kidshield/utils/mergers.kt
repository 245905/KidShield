package com.dominik.control.kidshield.utils

import com.dominik.control.kidshield.data.model.domain.AppInfoDiffEntity
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity

data class AppUiModel(
    val info: AppInfoEntity?,
    val diff: AppInfoDiffEntity? = null
)

fun mergeAppInfoLists(apps: List<AppInfoEntity>, diffs: List<AppInfoDiffEntity>): List<AppUiModel> {
    val byPackage = apps.associateBy { it.packageName }.toMutableMap()
    val results = mutableListOf<AppUiModel>()

    // apps with optional diff
    diffs.forEach { diff ->
        val info = byPackage[diff.packageName]
        results += AppUiModel(info = info, diff = diff)
        // remove from map so we don't duplicate below
        byPackage.remove(diff.packageName)
    }

    // remaining apps without diff
    byPackage.values.forEach { info ->
        results += AppUiModel(info = info, diff = null)
    }

    return results
}
