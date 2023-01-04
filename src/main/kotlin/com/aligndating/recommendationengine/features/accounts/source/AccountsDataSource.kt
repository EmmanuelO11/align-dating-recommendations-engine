package com.aligndating.recommendationengine.features.accounts.source

import com.aligndating.recommendationengine.features.accounts.data.User

interface AccountsDataSource {
    suspend fun getUserById(id: String): User?
    suspend fun getUsers(ids: List<String>): List<User>
}