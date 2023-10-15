package com.scarlet.coroutines.summary

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

/*
 * "Best practices sometimes need to be violated; they are
 * guidelines for standard situations,
 * not rules for every situation."
 */

/**
 * **Don’t use async with an immediate await**
 *
 * If you need a scope, instead of `async { ... }.await()`, use `coroutineScope`.
 * If you need to set a context, use `withContext`.
 */

data class User(val name: String) {
    fun toUser() = User(name)
}

interface UserRepo {
    suspend fun getUser(): User
}

// Don't
suspend fun getUser_Bad(repo: UserRepo): User = coroutineScope {
    val user = async { repo.getUser() }.await()
    user.toUser()
}

// Do
suspend fun getUser_Good(repo: UserRepo): User {
    val user = repo.getUser()
    return user.toUser()
}

// When you start a few async tasks, all of them except the last one need to use
// `async`. In this case, I suggest using `async` for all of them for readability.
suspend fun showNews() = coroutineScope {
    val config = async { getConfigFromApi() }
    val news = async { getNewsFromApi(config.await()) }
    val user = async { getUserFromApi() } // async not
    // necessary here, but useful for readability
    displayNews(user.await(), news.await())
}

fun displayNews(user: User, news: News) {
    println("User: ${user.name}")
    println("News: ${news.news}")
}

data class Config(val value: String)
data class News(val news: List<String>)

suspend fun getConfigFromApi(): Config {
    delay(1000)
    return Config("")
}

suspend fun getNewsFromApi(config: Config): News {
    delay(1000)
    return News(listOf("News 1", "News 2"))
}

suspend fun getUserFromApi(): User {
    delay(1000)
    return User("Scarlet")
}

/**
 * **Use coroutineScope instead of withContext(EmptyCoroutineContext)**
 *
 * The only difference between `withContext` and `coroutineScope` is that `withContext` can
 * override context, so instead of `withContext(EmptyCoroutineContext)`, use `coroutineScope`.
 */

/**
 * **Use awaitAll**
 *
 * The `awaitAll` function should be preferred over `map { it.await() }` because it stops
 * waiting when the first async task throws an exception, while `map { it.await() }` awaits
 * these coroutines one after another until this process reaches one that fails.
 */