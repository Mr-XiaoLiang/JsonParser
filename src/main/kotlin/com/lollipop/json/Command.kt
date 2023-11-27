package com.lollipop.json

sealed class Command(val icon: String, val describe: String) {

    // TODO 增加ICON实现
    /**
     * 基础的CURL模式
     */
    object Curl : Command("", "CURL")

    /**
     * 基础的JSON模式
     */
    object Json : Command("", "Json")
    class Custom(val key: String, icon: String, describe: String) : Command(icon, describe)

}