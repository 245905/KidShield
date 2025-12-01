package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.remote.datasource.TestRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface TestRepository {
    suspend fun open(): Result<Unit>
    suspend fun closed(): Result<Unit>
    suspend fun restricted(): Result<Unit>
}

class TestRepositoryImpl @Inject constructor(
    private val remote: TestRemoteDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TestRepository {

    override suspend fun open(): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                remote.open()
            }
        }

    override suspend fun closed(): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                remote.closed()
            }
        }

    override suspend fun restricted(): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                remote.restricted()
            }
        }

}