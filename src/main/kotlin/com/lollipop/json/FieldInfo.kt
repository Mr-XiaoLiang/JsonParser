package com.lollipop.json

sealed class FieldInfo(val name: String) {

    fun typedName(
        prefix: String,
        suffix: String
    ): String {
        val keys = name.split("_", "-")
        val builder = StringBuilder()
        builder.append(formatKey(prefix))
        keys.forEach {
            builder.append(formatKey(it))
        }
        builder.append(formatKey(suffix))
        return builder.toString()
    }

    private fun formatKey(key: String): String {
        if (key.isEmpty()) {
            return key
        }
        if (key.length == 1) {
            return key.uppercase()
        }
        val first = key.substring(0, 1).uppercase()
        val next = key.substring(1, key.length).lowercase()
        return first + next
    }

    class BooleanInfo(name: String) : FieldInfo(name) {
        override fun toString(): String {
            return "Boolean: $name"
        }
    }

    class StringInfo(name: String) : FieldInfo(name) {
        override fun toString(): String {
            return "String: $name"
        }
    }

    class IntInfo(name: String) : FieldInfo(name) {
        override fun toString(): String {
            return "Int: $name"
        }
    }

    class LongInfo(name: String) : FieldInfo(name) {
        override fun toString(): String {
            return "Long: $name"
        }
    }

    class FloatInfo(name: String) : FieldInfo(name) {
        override fun toString(): String {
            return "Float: $name"
        }
    }

    class DoubleInfo(name: String) : FieldInfo(name) {
        override fun toString(): String {
            return "Double: $name"
        }
    }

    class ObjectInfo(name: String) : FieldInfo(name) {
        val fieldList = ArrayList<FieldInfo>()
        override fun toString(): String {
            val builder = StringBuilder()
            builder.append("Object: ").append(name).append(" { ")
            fieldList.forEachIndexed { index, fieldInfo ->
                if (index > 0) {
                    builder.append(", ")
                }
                builder.append(fieldInfo.toString())
            }
            builder.append(" }")
            return builder.toString()
        }
    }

    class ListInfo(name: String, val item: FieldInfo) : FieldInfo(name) {
        override fun toString(): String {
            return "List: $name { $item }"
        }
    }

}