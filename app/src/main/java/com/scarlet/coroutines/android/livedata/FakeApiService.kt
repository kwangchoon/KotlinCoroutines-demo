package com.scarlet.coroutines.android.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.scarlet.model.Article
import com.scarlet.model.Recipe
import com.scarlet.util.Resource

class FakeApiService : ApiService {

    override suspend fun getArticles(): Resource<List<Article>> {
        return Resource.Success(Recipe.mRecipes) as Resource<List<Article>>
    }

    override suspend fun getTopArticle(): Resource<Article> {
        val random: Int = (0..Article.articleSamples.size).random()
        return Resource.Success(Article.articleSamples[random]) as Resource<Article>
    }

    override fun getArticlesByAuthorName(name: String): LiveData<Resource<List<Article>>> =
        liveData {
            emit(Resource.Success(Article.articleSamples.filter { it.author == name }))
        }
}