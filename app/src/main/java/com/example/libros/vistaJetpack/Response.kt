package com.example.libros.vistaJetpack

import androidx.annotation.RestrictTo
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Retrofit

const val bd = "http://iesayala.ddns.net/abigail/"

interface Response {
    @GET("jsonLibros.php")
    fun responseLibros() : Call<Repository>
}

object ResponseRepository{
    val responseInterface : Response

    init{
        val r = Retrofit.Builder()
            .baseUrl(bd)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        responseInterface = r.create(Response::class.java)
    }
}