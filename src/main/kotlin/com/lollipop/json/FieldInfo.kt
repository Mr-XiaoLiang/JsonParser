package com.lollipop.json

sealed class FieldInfo(val name: String) {

    enum class CamelCase {
        BIG,
        SMALL
    }

    fun typedName(
        prefix: String,
        suffix: String,
        case: CamelCase
    ): String {
        val keys = name.split("_", "-")
        val builder = StringBuilder()
        formatKey(prefix, case, builder)
        keys.forEach {
            formatKey(it, case, builder)
        }
        formatKey(suffix, case, builder)
        return builder.toString()
    }

    private fun formatKey(key: String, case: CamelCase, builder: StringBuilder) {
        if (key.isEmpty()) {
            return
        }
        // 如果为空，并且是小驼峰，那么就全小写，否则后面的单词都需要首字母大写
        if (case == CamelCase.SMALL && builder.isEmpty()) {
            builder.append(key.lowercase())
            return
        }
        if (key.length == 1) {
            builder.append(key.uppercase())
            return
        }
        builder.append(key.substring(0, 1).uppercase()).append(key.substring(1, key.length).lowercase())
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