package com.lollipop.json.builder

import com.lollipop.json.CodeBuilder
import com.lollipop.json.Command
import com.lollipop.json.FieldInfo
import org.json.JSONObject

object KotlinDataClassBuilder : CodeBuilder() {

    private val AnnotationCommand = Command.Enum(
        "annotation",
        "注解",
        listOf(AnnotationType.GSON.name, AnnotationType.NONE.name)
    )

    private val NullableCommand = Command.Bool(
        "nullable",
        "Nullable",
    )

    private val DefaultEnableCommand = Command.Bool(
        "defaultValue",
        "默认值",
    )

    private val SetEnableCommand = Command.Bool(
        "setEnable",
        "Setter",
    )

    private var annotationType: AnnotationType = AnnotationType.GSON

    private var nullable: Boolean = false

    private var defaultValue: Boolean = true

    private var setterEnable: Boolean = false

    override val icon: String = ""
    override val name: String = "Kotlin"

    private val commandList = listOf(
        AnnotationCommand,
        NullableCommand,
        DefaultEnableCommand,
        SetEnableCommand
    )

    override fun build(list: List<FieldInfo>): String {
        val rootField = FieldInfo.ObjectInfo("Root")
        rootField.fieldList.addAll(list)
        val builder = StringBuilder()
        appendClass(rootField, builder, 0)
        return builder.toString()
    }

    override fun getCommandList(): List<Command> {
        return commandList
    }

    override fun getDefaultValue(command: Command): String {
        return when (command) {
            AnnotationCommand -> annotationType.name
            NullableCommand -> NullableCommand.value(nullable)
            DefaultEnableCommand -> DefaultEnableCommand.value(defaultValue)
            SetEnableCommand -> SetEnableCommand.value(setterEnable)
            else -> ""
        }
    }

    override fun onCommand(command: Command, value: String) {
        when (command) {
            AnnotationCommand -> {
                annotationType = AnnotationType.values().find { it.name == value } ?: AnnotationType.NONE
            }

            NullableCommand -> {
                nullable = Command.parseBoolean(value)
            }

            DefaultEnableCommand -> {
                defaultValue = Command.parseBoolean(value)
            }

            SetEnableCommand -> {
                setterEnable = Command.parseBoolean(value)
            }

            else -> {}
        }
    }

    override fun getProfileDetail(): JSONObject {
        return JSONObject()
            .put(AnnotationCommand.describe, annotationType.name)
            .put(NullableCommand.describe, nullable)
            .put(DefaultEnableCommand.describe, defaultValue)
            .put(SetEnableCommand.describe, setterEnable)
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