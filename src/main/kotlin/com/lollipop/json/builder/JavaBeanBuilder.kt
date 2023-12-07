package com.lollipop.json.builder

import com.lollipop.json.Command
import com.lollipop.json.FieldInfo

object JavaBeanBuilder : JvmClassBuilder() {

    private val customClassCache = HashMap<String, String>()
    private val listClassCache = HashMap<String, String>()
    private val defaultObjectValueCache = HashMap<String, String>()
    private val defaultListValueCache = HashMap<String, String>()

    override val icon: String = ""
    override val name: String = "Java"

    private fun getClassName(info: FieldInfo): String {
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
                "Integer"
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

    private fun getDefaultValue(info: FieldInfo): String {
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
                val s = defaultListValueCache[info.name]
                if (s != null) {
                    return s
                }
                val value = "new ArrayList<" + getClassName(info.item) + ">()"
                defaultListValueCache[info.name] = value
                value
            }

            is FieldInfo.ObjectInfo -> {
                val s = defaultObjectValueCache[info.name]
                if (s != null) {
                    return s
                }
                val value = "new " + getClassName(info) + "()"
                defaultObjectValueCache[info.name] = value
                value
            }

        }
    }

    private val commandList = listOf(
        AnnotationCommand,
        DefaultEnableCommand,
        SetEnableCommand
    )

    override fun getCommandList(): List<Command> {
        return commandList
    }

    override fun appendClass(info: FieldInfo.ObjectInfo, builder: StringBuilder) {
        BuilderImpl().build(info, builder)
    }

    private class BuilderImpl {

        private val classNameCache = HashMap<String, String>()
        private val fieldCache = HashMap<String, String>()
        private val getterCache = HashMap<String, String>()
        private val setterCache = HashMap<String, String>()
        private val defCache = HashMap<String, String>()

        fun build(rootInfo: FieldInfo.ObjectInfo, builder: StringBuilder) {
            appendClass(rootInfo, builder, 0)
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
            val typedName = getFieldName(info)
            fieldCache[info.name] = typedName
            append(typedName)
            return this
        }

        private fun StringBuilder.setterName(info: FieldInfo): StringBuilder {
            val s = setterCache[info.name]
            if (s != null) {
                append(s)
                return this
            }
            val typedName = "set" + info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG)
            setterCache[info.name] = typedName
            append(typedName)
            return this
        }

        private fun StringBuilder.getterName(info: FieldInfo): StringBuilder {
            val s = getterCache[info.name]
            if (s != null) {
                append(s)
                return this
            }
            val typedName = "get" + info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG)
            getterCache[info.name] = typedName
            append(typedName)
            return this
        }

        private fun StringBuilder.defaultValue(info: FieldInfo): StringBuilder {
            val s = defCache[info.name]
            if (s != null) {
                append(s)
                return this
            }
            val def = getDefaultValue(info)
            defCache[info.name] = def
            append(def)
            return this
        }

        private fun appendClass(info: FieldInfo.ObjectInfo, builder: StringBuilder, tab: Int) {
            builder.appendTab(tab).append("public ")
            if (tab > 0) {
                builder.append("static ")
            }
            builder.append("class ").className(info).append(" {\n")

            val customClassList = ArrayList<FieldInfo.ObjectInfo>()

            // 添加成员变量
            info.fieldList.forEach {
                appendField(it, builder, tab + 1)
                if (it is FieldInfo.ObjectInfo) {
                    customClassList.add(it)
                }
                if (it is FieldInfo.ListInfo && it.item is FieldInfo.ObjectInfo) {
                    customClassList.add(it.item)
                }
            }

            // 添加构造函数
            appendConstructor(info, builder, tab)

            // 添加getter setter
            info.fieldList.forEach {
                appendGetterAndSetter(it, builder, tab + 1)
            }

            if (customClassList.isNotEmpty()) {
                builder.append("\n")
                customClassList.forEach {
                    appendClass(it, builder, tab + 1)
                }
            }

            builder.appendTab(tab).append("}\n\n")
        }

        private fun appendField(info: FieldInfo, builder: StringBuilder, tab: Int) {
            if (annotationType == AnnotationType.GSON) {
                builder.appendTab(tab)
                    .append("@SerializedName(\"")
                    .append(info.name)
                    .append("\")\n")
            }
            builder.appendTab(tab)
                .append("private ")
                .className(info)
                .append(" ")
                .fieldName(info)
                .append(" = null;\n")
        }

        private fun appendGetterAndSetter(info: FieldInfo, builder: StringBuilder, tab: Int) {
            // getter 部分
            // public String getVal() {
            //     if (val == null) {
            //         return "";
            //     }
            //     return val;
            // }
            builder.appendTab(tab).append("public ").className(info).append(" ").getterName(info).append("() {\n")
            if (defaultValue) {
                // 不能为空，所以我们需要加上一段空判断代码
                builder.appendTab(tab + 1).append("if (null == ").fieldName(info).append(") {\n")
                    .appendTab(tab + 2).append("this.").fieldName(info).append(" = ").defaultValue(info).append(";\n")
                    .appendTab(tab + 1).append("}\n")
            }
            builder.appendTab(tab + 1).append("return ").fieldName(info).append(";\n\n")
                .appendTab(tab).append("}\n")

            // setter 部分
            // public void setVal(String val) {
            //     this.val = val;
            // }

            builder.appendTab(tab).append("public void ").setterName(info).append("(")
                .className(info).append(" value")
                .append(") {\n")
            builder.appendTab(tab + 1).append("this.").fieldName(info).append(" = value;\n")
                .appendTab(tab).append("}\n\n")

        }

        private fun appendConstructor(info: FieldInfo.ObjectInfo, builder: StringBuilder, tab: Int) {
            //     public Demo(String val, Integer foo, Long l) {
            //        this.val = val;
            //        this.foo = foo;
            //        this.l = l;
            //    }
            builder.appendTab(tab + 1).append("public ").className(info).append("() {}\n")
            val fieldList = info.fieldList
            if (fieldList.isEmpty()) {
                return
            }
            builder.appendTab(tab + 1).append("public ").className(info).append("(\n")
            builder.appendTab(tab + 2)
            for (index in fieldList.indices) {
                if (index > 0) {
                    builder.append(",\n")
                }
                val field = fieldList[index]
                builder.className(field).append(" ").fieldName(field)
            }
            builder.append("\n").appendTab(tab + 1).append(") {\n")
            for (index in fieldList.indices) {
                val field = fieldList[index]
                builder.appendTab(tab + 2).append("this.").fieldName(field)
                    .append(" = ").fieldName(field).append(";\n")
            }
            builder.appendTab(tab + 1).append("}\n")
        }

    }

}