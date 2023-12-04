package com.lollipop.json.builder

import com.lollipop.json.CodeBuilder
import com.lollipop.json.Command
import com.lollipop.json.FieldInfo
import org.json.JSONObject

object JavaBeanBuilder: CodeBuilder() {
    override val icon: String
        get() = TODO("Not yet implemented")

    override val name: String = "Java"

    override fun build(list: List<FieldInfo>): String {
        TODO("Not yet implemented")
    }

    override fun getCommandList(): List<Command> {
        TODO("Not yet implemented")
    }

    override fun getDefaultValue(command: Command): String {
        TODO("Not yet implemented")
    }

    override fun onCommand(command: Command, value: String) {
        TODO("Not yet implemented")
    }

    override fun getProfileDetail(): JSONObject {
        TODO("Not yet implemented")
    }
}