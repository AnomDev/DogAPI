package com.anomdev.dogapi

import com.google.gson.annotations.SerializedName

data class DogsResponse(
    // La etiqueta de @SerializedName debe ser IGUAL al nombre que tenga el elemento en la API. Al ponerlo, ya podemos crear nuestra variable con el nombre que queramos sin depender de la API.
    @SerializedName("status") var status: String,
    @SerializedName("message") var images: List<String>
)
