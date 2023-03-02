package com.example.testgps.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("meteo/location/insert.php")
    fun sendReq(@Body requestModel: RequestModelReg) : Call<ResponseModel>
}