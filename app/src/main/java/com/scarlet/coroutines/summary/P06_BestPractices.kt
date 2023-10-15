package com.scarlet.coroutines.summary

import com.scarlet.coroutines.basics.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/**
 * **Don’t break structured concurrency**
 *
 * Using an external job or scope breaks structured concurrency, prevents
 * proper cancellation, and leads to memory leaks as a result.
 */

interface UserService {
    suspend fun currentUser(): User
}

interface PostsService {
    suspend fun getAll(): List<Post>
}

// Don't
suspend fun getPosts(
    userService: UserService, postsService: PostsService
) = withContext(Job()) {
    val user = async { userService.currentUser() }
    val posts = async { postsService.getAll() }
    posts.await()
        .filterCanSee(user.await())
}

private fun <E> List<E>.filterCanSee(user: User) {
    TODO("Not yet implemented")
}

