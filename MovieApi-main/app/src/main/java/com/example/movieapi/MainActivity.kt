package com.example.movieapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapi.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MovieAdapter
    private lateinit var recyclerView: RecyclerView
    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        GlobalScope.launch(Dispatchers.IO) {
            val response = fetchMovies()
            launch(Dispatchers.Main) {

                if (response != null) {
                    parseMovies(response)
                    println(response)
                } else {
                    println("fallo.")
                }
            }
        }
    }

    private fun fetchMovies() : String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.themoviedb.org/3/trending/movie/day?language=en-US")
            .get()
            .addHeader("accept", "application/json")
            .addHeader(
                "Authorization",
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3MzU0NTkzNjFlZjY0ZTdmNThlMjA5NTk4NzQ3ZmMyOCIsInN1YiI6IjY1OWVkMThiMWQzNTYzMDI1YzU0ZjFiNSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.rhBEJwfiZsAFArRgzEstg-_VFGrw0Nmcs3qBXQ4qd-4"
            )
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        return responseBody

    }

    private fun parseMovies(response: String) {
        try {
            val gson = Gson()
            val movieResponse = gson.fromJson(response, MovieResponse::class.java)

            if(movieResponse.results.isNotEmpty()) {
                movies.addAll(movieResponse.results)
                println("Movie" + movies[0].toString())
                initRecyclerView()
            } else {
                println("No archivos encontrados")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error.")
        }
    }

        private fun initRecyclerView() {
            adapter = MovieAdapter(this, movies)
            val layoutManager = GridLayoutManager(this, 2)
            binding.recyclerView.layoutManager = layoutManager
            binding.recyclerView.adapter = adapter
        }
    }


