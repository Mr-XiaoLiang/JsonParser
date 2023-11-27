package com.lollipop.json

abstract class CodeBuilder {

    companion object {
        var prefix: String = ""
        var suffix: String = ""
    }

    abstract val icon: String

    abstract val name: String

    abstract fun build(list: List<FieldInfo>): String

    abstract fun getCommandList(): List<Command.Custom>

    abstract fun onCommand(command: Command, value: String)

}