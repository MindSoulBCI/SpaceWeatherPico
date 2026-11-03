package com.example.spatialweather.p10282026.data

import com.example.spatialweather.p10282026.domain.model.*
interface WeatherRepository { suspend fun fetch(city: City): Weather }
