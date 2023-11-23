package com.lollipop.json

import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.BigInteger

object JsonParser {

    fun parse(json: String): List<FieldInfo> {
        try {
            val jsonValue = json.trim()
            if (jsonValue.startsWith("[")) {
                return parse(JSONArray(jsonValue))
            }
            return parse(JSONObject(json))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return emptyList()
    }

    fun parse(json: JSONObject): List<FieldInfo> {
        if (json.isEmpty) {
            return emptyList()
        }
        val list = ArrayList<FieldInfo>()
        parseObject(json, list)
        return list
    }

    fun parse(json: JSONArray): List<FieldInfo> {
        if (json.isEmpty) {
            return emptyList()
        }
        val list = ArrayList<FieldInfo>()
        val length = json.length()
        for (index in 0 until length) {
            val value = json.opt(index)
            try {
                val info = parseValue("", value)
                list.add(info)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return list
    }

    private fun parseObject(json: JSONObject, list: MutableList<FieldInfo>) {
        if (json.isEmpty()) {
            return
        }
        json.keySet().forEach { key ->
            val value = json.opt(key)
            try {
                val info = parseValue(key, value)
                list.add(info)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private fun parseArray(json: JSONArray, name: String): FieldInfo {
        if (json.isEmpty()) {
            return FieldInfo.ListInfo(name, FieldInfo.ObjectInfo(name))
        }
        val length = json.length()

        var item: FieldInfo? = null

        for (index in 0 until length) {
            val value = json.opt(index)
            try {
                val info = parseValue(name, value)
                if (item == null) {
                    item = info
                    continue
                }
                item = mergeInfo(item, info)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return FieldInfo.ListInfo(name, item ?: FieldInfo.ObjectInfo(name))
    }

    private fun mergeInfo(info1: FieldInfo, info2: FieldInfo): FieldInfo {
        if (info1::class != info2::class || info1 is FieldInfo.ObjectInfo) {
            val fieldMap = HashMap<String, FieldInfo>()
            if (info1 is FieldInfo.ObjectInfo) {
                info1.fieldList.forEach {
                    fieldMap[it.name] = it
                }
            }
            if (info2 is FieldInfo.ObjectInfo) {
                info2.fieldList.forEach {
                    val value = fieldMap[it.name]
                    if (value != null) {
                        fieldMap[it.name] = mergeInfo(value, it)
                    } else {
                        fieldMap[it.name] = it
                    }
                }
            }
            val newInfo = FieldInfo.ObjectInfo(info1.name)
            newInfo.fieldList.addAll(fieldMap.values)
            return newInfo
        } else if (info1 is FieldInfo.ListInfo) {
            val other = if (info2 is FieldInfo.ListInfo) {
                info2.item
            } else {
                info2
            }
            return FieldInfo.ListInfo(info1.name, mergeInfo(info1.item, other))
        }
        return info1
    }

    private fun parseValue(key: String, value: Any): FieldInfo {
        when (value) {
            is Boolean -> {
                return FieldInfo.BooleanInfo(key)
            }

            is Float -> {
                return FieldInfo.FloatInfo(key)
            }

            is BigDecimal -> {
                return FieldInfo.DoubleInfo(key)
            }

            is Double -> {
                return FieldInfo.DoubleInfo(key)
            }

            is BigInteger -> {
                return FieldInfo.LongInfo(key)
            }

            is Int -> {
                return FieldInfo.IntInfo(key)
            }

            is Long -> {
                return FieldInfo.LongInfo(key)
            }

            is String -> {
                return FieldInfo.StringInfo(key)
            }

            is JSONObject -> {
                val info = FieldInfo.ObjectInfo(key)
                parseObject(value, info.fieldList)
                return info
            }

            is JSONArray -> {
                return parseArray(value, key)
            }
        }
        return FieldInfo.ObjectInfo(key)
    }

}