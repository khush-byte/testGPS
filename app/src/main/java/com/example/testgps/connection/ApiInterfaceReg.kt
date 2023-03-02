package com.example.testgps.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterfaceReg {
    @POST("meteo/location/reg.php")
    fun sendReq(@Body requestModel: RequestModelReg) : Call<ResponseModel>
}