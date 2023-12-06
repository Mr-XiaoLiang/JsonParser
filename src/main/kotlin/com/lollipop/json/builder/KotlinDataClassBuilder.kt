package com.lollipop.json.builder

import com.lollipop.json.Command
import com.lollipop.json.FieldInfo

object KotlinDataClassBuilder : JvmClassBuilder() {

    override val icon: String = ""
    override val name: String = "Kotlin"

    private val commandList = listOf(
        AnnotationCommand,
        NullableCommand,
        DefaultEnableCommand,
        SetEnableCommand
    )

    override fun getCommandList(): List<Command> {
        return commandList
    }

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
        builder.append(": ").append(getClassName(info))
        if (nullable) {
            builder.append("?")
        }
        builder.append(" ")
        if (defaultValue) {
            builder.append("= ").append(Companion.getDefaultValue(info))
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