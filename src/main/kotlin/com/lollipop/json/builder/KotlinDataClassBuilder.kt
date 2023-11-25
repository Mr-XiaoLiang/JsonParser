package com.lollipop.json.builder

import com.lollipop.json.CodeBuilder
import com.lollipop.json.FieldInfo

object KotlinDataClassBuilder : CodeBuilder() {

    var annotationType: AnnotationType = AnnotationType.GSON

    var nullable: Boolean = false

    var defaultValue: Boolean = true

    var setEnable: Boolean = false

    override val icon: String = ""
    override val name: String = "Kotlin"

    override fun build(list: List<FieldInfo>): String {
        val builder = StringBuilder()
        val objectList = ArrayList<FieldInfo.ObjectInfo>()
        list.forEach {
            appendField(it, builder, 0)
            if (it is FieldInfo.ObjectInfo) {
                objectList.add(it)
            }
            if (it is FieldInfo.ListInfo && it.item is FieldInfo.ObjectInfo) {
                objectList.add(it.item)
            }
        }
        objectList.forEach {
            appendClass(it, builder, 0)
        }
        return builder.toString()
    }

    private fun appendField(info: FieldInfo, builder: StringBuilder, tab: Int) {
        if (annotationType == AnnotationType.GSON) {
            builder.appendTab(tab)
                .append("@SerializedName(\"")
                .append(info.name)
                .append("\")\n")
        }
        builder.appendTab(tab)
        if (setEnable) {
            builder.append("var ")
        } else {
            builder.append("val ")
        }
        builder.append(info.typedName(prefix, suffix, FieldInfo.CamelCase.SMALL))
        builder.append(": ")
        when (info) {
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

            is FieldInfo.ListInfo -> {
                builder.append("List<")
                    .append(info.item.typedName(prefix, suffix, FieldInfo.CamelCase.BIG))
                    .append(">")
            }

            is FieldInfo.ObjectInfo -> {
                builder.append(info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG))
            }
        }
        if (nullable) {
            builder.append("?")
        }
        builder.append(" ")
        if (defaultValue) {
            when (info) {
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

                is FieldInfo.ObjectInfo -> {
                    builder.append("= ").append(info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG)).append("() ")
                }
            }
        }
        builder.append(", ")
    }

    private fun appendClass(info: FieldInfo.ObjectInfo, builder: StringBuilder, tab: Int) {
        builder.appendTab(tab)
            .append("data class ")
            .append(info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG))
            .append("(")
            .append("\n")
        val customClassList = ArrayList<FieldInfo.ObjectInfo>()
        info.fieldList.forEach {
            appendField(it, builder, tab + 1)
            builder.append("\n")
            if (it is FieldInfo.ObjectInfo) {
                customClassList.add(it)
            }
            if (it is FieldInfo.ListInfo && it.item is FieldInfo.ObjectInfo) {
                customClassList.add(it.item)
            }
        }
        builder.append(")")
        if (customClassList.isNotEmpty()) {
            builder.append(" {")
            customClassList.forEach {
                appendClass(it, builder, tab + 1)
            }
            builder.appendTab(tab).append("}")
        }
        builder.append("\n")
    }

    private fun StringBuilder.appendTab(tab: Int): StringBuilder {
        val builder = this
        for (i in 0 until tab) {
            // 一个Tab等于4个空格
            builder.append("    ")
        }
        return builder
    }

    enum class AnnotationType {
        NONE,
        GSON
    }

}