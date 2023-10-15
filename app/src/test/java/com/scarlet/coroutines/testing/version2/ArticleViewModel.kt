package com.scarlet.coroutines.testing.version2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scarlet.coroutines.android.livedata.ApiService
import com.scarlet.model.Article
import com.scarlet.util.Resource
import kotlinx.coroutines.*

class ArticleViewModel(
    private val apiService: ApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _articles = MutableLiveData<Resource<List<Article>>>()
    val articles: LiveData<Resource<List<Article>>>
        get() = _articles

    fun onButtonClicked() {
        viewModelScope.launch(dispatcher) {
            loadData()
        }
    }

    private suspend fun loadData() {
        val articles = networkRequest()
        update(articles)
    }

    private suspend fun networkRequest(): Resource<List<Article>> {
        return apiService.getArticles()
    }

    private fun update(articles: Resource<List<Article>>) {
        _articles.value = articles
    }
}
