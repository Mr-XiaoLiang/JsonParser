package com.lollipop.json.builder

import com.lollipop.json.CodeBuilder
import com.lollipop.json.Command
import com.lollipop.json.FieldInfo
import org.json.JSONObject

abstract class JvmClassBuilder : CodeBuilder() {

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

    protected var annotationType: AnnotationType = AnnotationType.GSON

    protected var nullable: Boolean = false

    protected var defaultValue: Boolean = true

    protected var setterEnable: Boolean = false

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
        appendClass(rootField, builder)
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

    protected abstract fun appendClass(info: FieldInfo.ObjectInfo, builder: StringBuilder)

    protected fun StringBuilder.appendTab(tab: Int): StringBuilder {
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