package com.lollipop.json

sealed class Command(val describe: String) {

    /**
     * 基础的CURL模式
     */
    object Curl : Command("CURL")

    /**
     * 基础的JSON模式
     */
    object Json : Command("Json")

    /**
     * 配置清单
     */
    object Profile: Command("状态")

    class Custom(val key: String, describe: String) : Command(describe)

    class Enum(val key: String, describe: String, val itemList: List<String>) : Command(describe)

    class Bool(val key: String, describe: String) : Command(describe) {

        fun value(boolean: Boolean): String {
            return if (boolean) { "true" } else { "false" }
        }

    }

    companion object {
        fun parseBoolean(value: String): Boolean {
            return value.equals("true", ignoreCase = true)
        }

        val commonList = listOf(
            Curl, Json, Profile
        )
    }

}