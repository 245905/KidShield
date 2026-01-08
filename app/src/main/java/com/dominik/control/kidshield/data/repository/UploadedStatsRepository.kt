package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.UploadedStatsDao
import com.dominik.control.kidshield.data.model.domain.UploadTableType
import com.dominik.control.kidshield.data.model.domain.UploadedStatsEntity
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.Date

interface UploadedStatsRepository {
    suspend fun getAllUploadedStats(): List<UploadedStatsEntity>
    suspend fun insertUploadedStats(data: List<UploadedStatsEntity>): List<Long>
    suspend fun insertUploadedStats(data: UploadedStatsEntity): Long
    suspend fun deleteUploadedStats(data: List<UploadedStatsEntity>): Int
    suspend fun deleteAllUploadedStats()
    suspend fun getUploadedStatsByDate(date: Date): List<UploadedStatsEntity>
    suspend fun getUploadedStatsByTableType(tableType: UploadTableType): List<UploadedStatsEntity>
    suspend fun getUploadedStatsByDateAndByTableType(date: Date, tableType: UploadTableType): UploadedStatsEntity?
    suspend fun updateUploadedStats(data: List<UploadedStatsEntity>): Int
}

class UploadedStatsRepositoryImpl @Inject constructor(
    private val dao: UploadedStatsDao,              // Room
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UploadedStatsRepository {

    override suspend fun getAllUploadedStats(): List<UploadedStatsEntity> {
        return dao.getAllUploadedStats()
    }

    override suspend fun insertUploadedStats(data: List<UploadedStatsEntity>): List<Long> {
        return dao.insertUploadedStats(data)
    }

    override suspend fun insertUploadedStats(data: UploadedStatsEntity): Long {
        return dao.insertUploadedStats(data)
    }

    override suspend fun deleteUploadedStats(data: List<UploadedStatsEntity>): Int {
        return dao.deleteUploadedStats(data)
    }

    override suspend fun deleteAllUploadedStats() {
        return dao.deleteAll()
    }

    override suspend fun getUploadedStatsByDate(date: Date): List<UploadedStatsEntity> {
        return dao.getUploadedStatsByDate(date)
    }

    override suspend fun getUploadedStatsByTableType(tableType: UploadTableType): List<UploadedStatsEntity> {
        return dao.getUploadedStatsByTableType(tableType)
    }

    override suspend fun getUploadedStatsByDateAndByTableType(date: Date, tableType: UploadTableType): UploadedStatsEntity? {
        return dao.getUploadedStatsByDateAndTableType(date, tableType)
    }

    override suspend fun updateUploadedStats(data: List<UploadedStatsEntity>): Int {
        return dao.updateUploadedStats(data)
    }
}
