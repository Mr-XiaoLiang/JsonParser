package com.lollipop.json.builder

import com.lollipop.json.Command
import com.lollipop.json.FieldInfo

object JavaBeanBuilder : JvmClassBuilder() {

    override val icon: String = ""
    override val name: String = "Java"

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
            var def = getDefaultValue(info)
            if (info is FieldInfo.ObjectInfo || info is FieldInfo.ListInfo) {
                // java 中需要 new
                def = "new $def"
            }
            defCache[info.name] = def
            append(def)
            return this
        }

        private fun appendClass(info: FieldInfo.ObjectInfo, builder: StringBuilder, tab: Int) {
            builder.appendTab(tab).append("public class ").className(info).append(" {\n")

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

            builder.appendTab(tab).append("}\n")
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
            builder.appendTab(tab).append("public ").className(info).getterName(info).append("() {\n")
            if (!defaultValue) {
                // 不能为空，所以我们需要加上一段空判断代码
                builder.appendTab(tab + 1).append("if (null == ").fieldName(info).append(") {\n")
                    .appendTab(tab + 2).append("return ").defaultValue(info).append(";\n")
                    .appendTab(tab + 1).append("}\n")
            }
            builder.appendTab(tab + 1).append("return ").fieldName(info).append(";\n")
                .appendTab(tab).append("}")

            // setter 部分
            // public void setVal(String val) {
            //     this.val = val;
            // }

            builder.appendTab(tab).append("public void ").setterName(info).append("(")
                .className(info).append(" value")
                .append(") {\n")
            builder.appendTab(tab + 1).append("this.").fieldName(info).append(" = value;\n")
                .appendTab(tab).append("};\n")

        }

//        private fun appendConstructor(className: String, fieldList: List<FieldInfo>, builder: StringBuilder, tab: Int) {
//
//        }

    }

}