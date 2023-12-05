package com.lollipop.json.builder

import com.lollipop.json.FieldInfo

object KotlinDataClassBuilder : JvmClassBuilder() {

    override val icon: String = ""
    override val name: String = "Kotlin"

    override fun appendClass(info: FieldInfo.ObjectInfo, builder: StringBuilder) {
        appendClass(info, builder, 0)
    }

    private fun appendField(info: FieldInfo, builder: StringBuilder, tab: Int) {
        if (annotationType == AnnotationType.GSON) {
            builder.appendTab(tab)
                .append("@SerializedName(\"")
                .append(info.name)
                .append("\")\n")
        }
        builder.appendTab(tab)
        if (setterEnable) {
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
        builder.append(", \n")
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
            if (it is FieldInfo.ObjectInfo) {
                customClassList.add(it)
            }
            if (it is FieldInfo.ListInfo && it.item is FieldInfo.ObjectInfo) {
                customClassList.add(it.item)
            }
        }
        builder.appendTab(tab).append(")")
        if (customClassList.isNotEmpty()) {
            builder.append(" {\n")
            customClassList.forEach {
                appendClass(it, builder, tab + 1)
            }
            builder.appendTab(tab).append("}")
        }
        builder.append("\n")
    }

}