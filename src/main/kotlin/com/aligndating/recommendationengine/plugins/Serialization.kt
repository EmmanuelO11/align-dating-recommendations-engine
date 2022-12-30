package com.aligndating.plugins

import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.application.*
import io.ktor.jackson.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {  }
        json()
    }
}

//@Serializable
//data class JwtEntry(
//    val key: String,
//    val value: List<Unit>
//)

//fun DataModel.kserialize(): JsonElement {
//    fun Any?.toJsonElement(): JsonElement {
//        return when (this) {
//            is Number -> JsonPrimitive(this)
//            is String -> JsonPrimitive(this)
//            is Boolean -> JsonPrimitive(this)
//            is Enum<*> -> JsonPrimitive(this.name)
//            is JsonElement -> this
//            else -> {
//                if (this!=null) {
//                    System.err.println("The type $this is unknown")
//                    System.err.println("type ${this.javaClass}")
//                }
//                JsonNull
//            }
//        }
//    }
//    fun Map<String, *>.clean(): JsonObject {
//        val map = filterValues {
//            when (it) {
//                is Map<*, *> -> it.isNotEmpty()
//                is Collection<*> -> it.isNotEmpty()
//                else -> it != null
//            }
//        }
//        return JsonObject(map.mapValues { entry -> entry.value.toJsonElement() }.filterNot { it.value == JsonNull })
//    }
//    fun cvt(value: Any?): JsonElement? {
//        return when (value) {
//            is DataModel -> value.kserialize()
//            is Map<*, *> -> value.entries.associate { (key, value) -> Pair(key.toString(), cvt(value)) }.clean()
//            is Iterable<*> -> JsonArray(value.mapNotNull { cvt(it) })
//            is Map.Entry<*, *> -> "jwt".toJsonElement()
//            else -> value.toJsonElement()
//        }
//    }
//    return this::class.memberProperties.associateBy { it.name }.mapValues { (_, prop) ->
//        cvt((prop as KProperty1<DataModel, *>).get(this))
//    }.clean()
//}