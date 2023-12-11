package com.lollipop.json.builder

import com.lollipop.json.CodeBuilder
import com.lollipop.json.Command
import com.lollipop.json.FieldInfo
import org.json.JSONObject

object SwiftModelableBuilder : CodeBuilder() {

    override val icon: String = ""
    override val name: String = "Swift"

    private val NullableCommand = Command.Bool(
        "nullable",
        "Nullable",
    )

    private val SetEnableCommand = Command.Bool(
        "setEnable",
        "Setter",
    )

    private var nullable = false

    private var setterEnable: Boolean = false

    override fun build(list: List<FieldInfo>): String {
        return BuilderImpl().build(list)
    }

    class BuilderImpl {
        private val customClassCache = HashMap<String, String>()
        private val listClassCache = HashMap<String, String>()
        private val classNameCache = HashMap<String, String>()
        private val fieldCache = HashMap<String, String>()

        fun build(list: List<FieldInfo>): String {
            val rootField = FieldInfo.ObjectInfo("Root")
            rootField.fieldList.addAll(list)
            val builder = StringBuilder()
            // 引入两个module
            builder.append("import Foundation\n")
            builder.append("import Networking\n")
            buildStruct(rootField, builder, 0)
            return builder.toString()
        }

        private fun buildStruct(info: FieldInfo.ObjectInfo, builder: StringBuilder, tab: Int) {
            builder.appendTab(tab).append("public struct ").className(info).append(": APIModelable, Codable {\n")
            info.fieldList.forEach { field ->
                builder.appendTab(tab + 1).append("public ")
                if (setterEnable) {
                    builder.append("var ")
                } else {
                    builder.append("let ")
                }
                builder.fieldName(field).append(": ").className(field)
                if (nullable) {
                    builder.append("?")
                }
                builder.append("\n")
            }
            builder.appendTab(tab + 1).append("init(json: JSON) {\n")

            val innerClassList = ArrayList<FieldInfo.ObjectInfo>()

            info.fieldList.forEach { field ->

                if (field is FieldInfo.ObjectInfo) {
                    innerClassList.add(field)
                }
                if (field is FieldInfo.ListInfo && field.item is FieldInfo.ObjectInfo) {
                    innerClassList.add(field.item)
                }

                builder.appendTab(tab + 2).fieldName(field)
                when (field) {
                    is FieldInfo.BooleanInfo -> {
                        builder.append(" = json.").append(field.name).append(".boolValue\n")
                    }

                    is FieldInfo.DoubleInfo -> {
                        builder.append(" = json.").append(field.name).append(".doubleOrStringValue\n")
                    }

                    is FieldInfo.FloatInfo -> {
                        builder.append(" = json.").append(field.name).append(".floatOrStringValue\n")
                    }

                    is FieldInfo.IntInfo -> {
                        builder.append(" = json.").append(field.name).append(".intOrStringValue\n")
                    }

                    is FieldInfo.StringInfo -> {
                        val isUrl = field.fieldDemo.startsWith("http", ignoreCase = true)
                        if (isUrl) {
                            builder.append(" = json.").append(field.name).append(".urlValue\n")
                        } else {
                            builder.append(" = json.").append(field.name).append(".stringOrIntValue\n")
                        }
                    }

                    is FieldInfo.LongInfo -> {
                        builder.append(" = json.").append(field.name).append(".int64OrStringValue\n")
                    }

                    is FieldInfo.ListInfo,
                    is FieldInfo.ObjectInfo -> {
                        builder.append(" = .init(json: json.").append(field.name).append(")\n")
                    }
                }
            }
            builder.appendTab(tab + 1).append("}\n")

            innerClassList.forEach { field ->
                buildStruct(field, builder, tab + 1)
            }

            builder.appendTab(tab).append("}\n")
        }

        private fun StringBuilder.className(info: FieldInfo): StringBuilder {
            val s = classNameCache[info.name]
            if (s != null) {
                append(s)
                return this
            }
            val clazz = getClassName(info)
            classNameCache[info.name] = clazz
            append(clazz)
            return this
        }

        private fun StringBuilder.fieldName(info: FieldInfo): StringBuilder {
            val s = fieldCache[info.name]
            if (s != null) {
                append(s)
                return this
            }
            val typedName = info.typedName(prefix, suffix, FieldInfo.CamelCase.SMALL)
            fieldCache[info.name] = typedName
            append(typedName)
            return this
        }

        private fun getClassName(info: FieldInfo): String {
            return when (info) {
                is FieldInfo.BooleanInfo -> {
                    "Bool"
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
                    "Int64"
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
                    val name = "[" + getClassName(info.item) + "]"
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

    }

    override fun getCommandList(): List<Command> {
        return listOf(NullableCommand, SetEnableCommand)
    }

    override fun getDefaultValue(command: Command): String {
        return when (command) {
            NullableCommand -> {
                NullableCommand.value(nullable)
            }

            SetEnableCommand -> {
                SetEnableCommand.value(setterEnable)
            }

            else -> {
                ""
            }
        }
    }

    override fun onCommand(command: Command, value: String) {
        when (command) {
            NullableCommand -> {
                nullable = Command.parseBoolean(value)
            }

            SetEnableCommand -> {
                setterEnable = Command.parseBoolean(value)
            }

            else -> {

            }
        }
    }

    override fun getProfileDetail(): JSONObject {
        return JSONObject()
            .put(NullableCommand.describe, nullable)
            .put(SetEnableCommand.describe, setterEnable)
    }
}