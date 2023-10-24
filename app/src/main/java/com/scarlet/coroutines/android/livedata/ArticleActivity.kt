package com.scarlet.coroutines.android.livedata

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.scarlet.R

class ArticleActivity : AppCompatActivity() {
    private val viewModel by lazy { ArticleViewModel(FakeApiService()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.topArticle.observe(this) {
            Log.v(TAG, "topArticle: $it")
        }

        viewModel.articlesByTopAuthor.observe(this) {
            Log.w(TAG, "articlesByTopAuthor: $it")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy:")
    }

    companion object {
        private const val TAG = "Article"
    }
}