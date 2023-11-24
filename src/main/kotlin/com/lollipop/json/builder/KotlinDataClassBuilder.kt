package com.lollipop.json.builder

import com.lollipop.json.CodeBuilder
import com.lollipop.json.FieldInfo

object KotlinDataClassBuilder : CodeBuilder() {

    var annotationType: AnnotationType = AnnotationType.NONE

    var nullable: Boolean = false

    var defaultValue: Boolean = true

    override val icon: String = ""
    override val name: String = "Kotlin"

    override fun build(list: List<FieldInfo>): String {
        TODO("Not yet implemented")
    }

    data class Demo(
        val foo: String
    )

    private fun appendField(info: FieldInfo, builder: StringBuilder, tab: Int) {
        for (i in 0 until tab) {
            // 一个Tab等于4个空格
            builder.append("    ")
        }

        builder.append("val ")
        builder.append(info.typedName(prefix, suffix))
        builder.append(": ")
        when(info) {
            is FieldInfo.BooleanInfo -> {
                builder.append("Boolean")
            }
            is FieldInfo.DoubleInfo -> {
                builder.append("Double")
            }
            is FieldInfo.FloatInfo -> {
                builder.append("Float")
            }
            is FieldInfo.IntInfo -> {
                builder.append("Int")
            }
            is FieldInfo.LongInfo -> {
                builder.append("Long")
            }
            is FieldInfo.StringInfo -> {
                builder.append("String")
            }
            is FieldInfo.ListInfo -> TODO()
            is FieldInfo.ObjectInfo -> TODO()
        }
        if (nullable) {
            builder.append("? ")
        }
        if (defaultValue) {
            when(info) {
                is FieldInfo.BooleanInfo -> {
                    builder.append("= false ")
                }
                is FieldInfo.DoubleInfo -> {
                    builder.append("= 0.0 ")
                }
                is FieldInfo.FloatInfo -> {
                    builder.append("= 0F ")
                }
                is FieldInfo.IntInfo -> {
                    builder.append("= 0 ")
                }
                is FieldInfo.LongInfo -> {
                    builder.append("= 0L ")
                }
                is FieldInfo.StringInfo -> {
                    builder.append("= \"\" ")
                }
                is FieldInfo.ListInfo -> {
                    builder.append("= emptyList() ")
                }
                is FieldInfo.ObjectInfo -> TODO()
            }
        }
    }

    private fun appendClass(info: FieldInfo.ObjectInfo, builder: StringBuilder, tab: Int) {
        for (i in 0 until tab) {
            // 一个Tab等于4个空格
            builder.append("    ")
        }
        // TODO
    }

    enum class AnnotationType {
        NONE,
        GSON
    }

}