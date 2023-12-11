package com.lollipop.json

import org.json.JSONObject

abstract class CodeBuilder {

    companion object {
        var prefix: String = ""
        var suffix: String = ""
    }

    abstract val icon: String

    abstract val name: String

    abstract fun build(list: List<FieldInfo>): String

    abstract fun getCommandList(): List<Command>

    abstract fun getDefaultValue(command: Command): String

    abstract fun onCommand(command: Command, value: String)

    abstract fun getProfileDetail(): JSONObject

    protected fun StringBuilder.appendTab(tab: Int): StringBuilder {
        val builder = this
        for (i in 0 until tab) {
            // 一个Tab等于4个空格
            builder.append("    ")
        }
        return builder
    }

}