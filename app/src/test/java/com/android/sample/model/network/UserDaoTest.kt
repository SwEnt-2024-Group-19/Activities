package com.android.sample.model.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.sample.model.activity.database.AppDatabase
import com.android.sample.model.profile.User
import com.android.sample.model.profile.database.UserDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.android.sample.resources.dummydata.testUser


@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveUser() = runBlocking {
        val user = testUser
        userDao.insert(user)

        val retrievedUser = userDao.getUser(testUser.id)
        assertEquals(user, retrievedUser)
    }

    @Test
    fun clearUsers() = runBlocking {
        val user = testUser
        userDao.insert(user)
        userDao.clear()

        val retrievedUser = userDao.getUser("1")
        assertEquals(null, retrievedUser)
    }
}
