package com.scarlet.coroutines.android.livedata

import androidx.lifecycle.*
import com.scarlet.model.Article
import com.scarlet.util.Resource
import kotlinx.coroutines.*

class ArticleViewModel(
    private val apiService: ApiService
) : ViewModel() {

    /**
     * Style 1
     */
    private val _articles = MutableLiveData<Resource<List<Article>>>()
    val articles: LiveData<Resource<List<Article>>> = _articles

    init {
        viewModelScope.launch {
            _articles.value = apiService.getArticles()
        }
    }

    /**
     * Style 2
     */
//    val articles: LiveData<Resource<List<Article>>> =
//        MutableLiveData<Resource<List<Article>>>().apply {
//            viewModelScope.launch {
//                value = apiService.getArticles()
//            }
//        }

    /**
     * The block starts executing when the returned LiveData becomes active.
     */
    val topArticle: LiveData<Resource<Article>> = liveData {
        while (true) {
            emit(apiService.getTopArticle())
            delay(FETCH_INTERVAL)
        }
    }

    @Suppress("UNCHECKED_CAST")
    val articlesByTopAuthor: LiveData<Resource<List<Article>>> =
        topArticle
            .switchMap { resource ->
                when (resource) {
                    is Resource.Success<Article> ->
                        liveData {
                            emit(Resource.Loading)
                            emitSource(apiService.getArticlesByAuthorName(resource.data?.author!!))
                        }

                    else ->
                        liveData {
                            emit(resource as Resource<List<Article>>)
                        }
                }
            }

    companion object {
        const val FETCH_INTERVAL = 5_000L
    }
}