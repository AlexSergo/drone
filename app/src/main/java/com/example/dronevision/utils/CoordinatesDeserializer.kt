package com.example.dronevision.utils

import com.example.dronevision.domain.model.Coordinates
import com.google.gson.*
import java.lang.reflect.Type

class CoordinatesDeserializer: JsonDeserializer<Coordinates>
{
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Coordinates {
        val jsonObject = json?.asJsonObject
        for(entry in jsonObject?.entrySet()!!) {
            //val r = context?.deserialize<>(entry.value, Coordinates.class)
            println(entry.value)
        }
        return Coordinates(x= 0.0 , y = 0.0)
    }
}
