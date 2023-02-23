package com.example.testgps

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(internal: Long): Flow<Location>

    class LocationException(message: String): Exception()
}