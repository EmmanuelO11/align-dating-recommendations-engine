package com.aligndating.recommendationengine.features.accounts.source

import com.aligndating.recommendationengine.extensions.dbQuery
import com.aligndating.recommendationengine.features.accounts.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class AccountsDataSourceImpl(
    database: CoroutineDatabase
) : AccountsDataSource {


    private val usersCollection = database.getCollection<User>().apply {
        CoroutineScope(Dispatchers.IO).launch {
            ensureUniqueIndex(User::phoneNumber)
        }
    }

    override suspend fun getUsers(ids: List<String>): List<User> {
        return usersCollection.find(User::profileID `in` ids).toList()
    }

    override suspend fun getUserById(id: String): User? = dbQuery {
        return@dbQuery usersCollection.findOne(User::profileID eq id)
    }
}