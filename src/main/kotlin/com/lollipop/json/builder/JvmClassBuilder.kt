package com.lollipop.json.builder

import com.lollipop.json.CodeBuilder
import com.lollipop.json.Command
import com.lollipop.json.FieldInfo
import org.json.JSONObject

abstract class JvmClassBuilder : CodeBuilder() {

    companion object {

        private val customClassCache = HashMap<String, String>()
        private val listClassCache = HashMap<String, String>()

        fun getClassName(info: FieldInfo): String {
            return when (info) {
                is FieldInfo.BooleanInfo -> {
                    "Boolean"
                }

                is FieldInfo.DoubleInfo -> {
                    "Double"
                }

                is FieldInfo.FloatInfo -> {
                    "Float"
                }

                is FieldInfo.IntInfo -> {
                    "Int"
                }

                is FieldInfo.LongInfo -> {
                    "Long"
                }

                is FieldInfo.StringInfo -> {
                    "String"
                }

                is FieldInfo.ListInfo -> {
                    val itemName = info.item.name
                    val s = listClassCache[itemName]
                    if (s != null) {
                        return s
                    }
                    val name = "List<" + getClassName(info.item) + ">"
                    listClassCache[itemName] = name
                    name
                }

                is FieldInfo.ObjectInfo -> {
                    val s = customClassCache[info.name]
                    if (s != null) {
                        return s
                    }
                    val name = info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG)
                    customClassCache[info.name] = name
                    name
                }
            }
        }

        fun getFieldName(info: FieldInfo): String {
            return info.typedName(prefix, suffix, FieldInfo.CamelCase.SMALL)
        }

        fun getDefaultValue(info: FieldInfo): String {
            return when (info) {
                is FieldInfo.BooleanInfo -> {
                    "false"
                }

                is FieldInfo.DoubleInfo -> {
                    "0.0"
                }

                is FieldInfo.FloatInfo -> {
                    "0F"
                }

                is FieldInfo.IntInfo -> {
                    "0"
                }

                is FieldInfo.LongInfo -> {
                    "0L"
                }

                is FieldInfo.StringInfo -> {
                    "\"\""
                }

                is FieldInfo.ListInfo -> {
                    "ArrayList<" + getClassName(info.item) + ">()"
                }

                is FieldInfo.ObjectInfo -> {
                    getClassName(info) + "()"
                }

            }
        }
    }

    protected val AnnotationCommand = Command.Enum(
        "annotation",
        "注解",
        listOf(AnnotationType.GSON.name, AnnotationType.NONE.name)
    )

    protected val NullableCommand = Command.Bool(
        "nullable",
        "Nullable",
    )

    protected val DefaultEnableCommand = Command.Bool(
        "defaultValue",
        "默认值",
    )

    protected val SetEnableCommand = Command.Bool(
        "setEnable",
        "Setter",
    )

    protected var annotationType: AnnotationType = AnnotationType.GSON

    protected var nullable: Boolean = false

    protected var defaultValue: Boolean = true

    protected var setterEnable: Boolean = false

    override fun build(list: List<FieldInfo>): String {
        val rootField = FieldInfo.ObjectInfo("Root")
        rootField.fieldList.addAll(list)
        val builder = StringBuilder()
        appendClass(rootField, builder)
        return builder.toString()
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