package com.lollipop.json.builder

import com.lollipop.json.FieldInfo

object JavaBeanBuilder : JvmClassBuilder() {

    override val icon: String = ""
    override val name: String = "Java"

    override fun appendClass(info: FieldInfo.ObjectInfo, builder: StringBuilder) {
        BuilderImpl(info, builder).build()
    }

    private class BuilderImpl(val rootInfo: FieldInfo.ObjectInfo, val builder: StringBuilder) {

        private val classNameCache = HashMap<String, String>()
        private val fieldCache = HashMap<String, String>()
        private val getterCache = HashMap<String, String>()
        private val setterCache = HashMap<String, String>()

        fun build() {
//            appendClass(rootInfo, builder, 0)
            // TODO
        }

        private fun className(info: FieldInfo, builder: StringBuilder) {
            val s = classNameCache[info.name]
            if (s != null) {
                builder.append(s)
                return
            }
            val clazz = when (info) {
                is FieldInfo.BooleanInfo -> { "Boolean" }

                is FieldInfo.DoubleInfo -> { "Double" }

                is FieldInfo.FloatInfo -> { "Float" }

                is FieldInfo.IntInfo -> { "Int" }

                is FieldInfo.LongInfo -> { "Long" }

                is FieldInfo.StringInfo -> { "String" }

                is FieldInfo.ListInfo -> {
                    "List<" + info.item.typedName(prefix, suffix, FieldInfo.CamelCase.BIG) + ">"
                }

                is FieldInfo.ObjectInfo -> {
                    info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG)
                }
            }
            classNameCache[info.name] = clazz
            builder.append(clazz)
        }

        private fun fieldName(info: FieldInfo, builder: StringBuilder) {
            val s = fieldCache[info.name]
            if (s != null) {
                builder.append(s)
                return
            }
            val typedName = info.typedName(prefix, suffix, FieldInfo.CamelCase.SMALL)
            fieldCache[info.name] = typedName
            builder.append(typedName)
        }

        private fun setterName(info: FieldInfo, builder: StringBuilder) {
            val s = setterCache[info.name]
            if (s != null) {
                builder.append(s)
                return
            }
            val typedName = "set" + info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG)
            setterCache[info.name] = typedName
            builder.append(typedName)
        }

        private fun getterName(info: FieldInfo, builder: StringBuilder) {
            val s = getterCache[info.name]
            if (s != null) {
                builder.append(s)
                return
            }
            val typedName = "get" + info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG)
            getterCache[info.name] = typedName
            builder.append(typedName)
        }

//        private fun appendField(info: FieldInfo, builder: StringBuilder, tab: Int) {
//            if (annotationType == AnnotationType.GSON) {
//                builder.appendTab(tab)
//                    .append("@SerializedName(\"")
//                    .append(info.name)
//                    .append("\")\n")
//            }
//            builder.appendTab(tab)
//            if (setterEnable) {
//                builder.append("var ")
//            } else {
//                builder.append("val ")
//            }
//            builder.append(info.typedName(prefix, suffix, FieldInfo.CamelCase.SMALL))
//            builder.append(": ")
//            when (info) {
//                is FieldInfo.BooleanInfo -> {
//                    builder.append("Boolean")
//                }
//
//                is FieldInfo.DoubleInfo -> {
//                    builder.append("Double")
//                }
//
//                is FieldInfo.FloatInfo -> {
//                    builder.append("Float")
//                }
//
//                is FieldInfo.IntInfo -> {
//                    builder.append("Int")
//                }
//
//                is FieldInfo.LongInfo -> {
//                    builder.append("Long")
//                }
//
//                is FieldInfo.StringInfo -> {
//                    builder.append("String")
//                }
//
//                is FieldInfo.ListInfo -> {
//                    builder.append("List<")
//                        .append(info.item.typedName(prefix, suffix, FieldInfo.CamelCase.BIG))
//                        .append(">")
//                }
//
//                is FieldInfo.ObjectInfo -> {
//                    builder.append(info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG))
//                }
//            }
//            if (nullable) {
//                builder.append("?")
//            }
//            builder.append(" ")
//            if (defaultValue) {
//                when (info) {
//                    is FieldInfo.BooleanInfo -> {
//                        builder.append("= false ")
//                    }
//
//                    is FieldInfo.DoubleInfo -> {
//                        builder.append("= 0.0 ")
//                    }
//
//                    is FieldInfo.FloatInfo -> {
//                        builder.append("= 0F ")
//                    }
//
//                    is FieldInfo.IntInfo -> {
//                        builder.append("= 0 ")
//                    }
//
//                    is FieldInfo.LongInfo -> {
//                        builder.append("= 0L ")
//                    }
//
//                    is FieldInfo.StringInfo -> {
//                        builder.append("= \"\" ")
//                    }
//
//                    is FieldInfo.ListInfo -> {
//                        builder.append("= emptyList() ")
//                    }
//
//                    is FieldInfo.ObjectInfo -> {
//                        builder.append("= ").append(info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG))
//                            .append("() ")
//                    }
//                }
//            }
//            builder.append(", \n")
//        }

        /*
    public class Demo {

        private String val = null;

        private Integer foo = null;

        private Long l = null;

        public String getVal() {
            if (val == null) {
                return "";
            }
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }
    }

         */

//        private fun appendClass(info: FieldInfo.ObjectInfo, builder: StringBuilder, tab: Int) {
//            val className = info.typedName(prefix, suffix, FieldInfo.CamelCase.BIG)
//            builder.appendTab(tab)
//                .append("public class ")
//                .append(className)
//                .append(" {")
//                .append("\n")
//            val customClassList = ArrayList<FieldInfo.ObjectInfo>()
//            info.fieldList.forEach {
//                appendField(it, builder, tab + 1)
//                if (it is FieldInfo.ObjectInfo) {
//                    customClassList.add(it)
//                }
//                if (it is FieldInfo.ListInfo && it.item is FieldInfo.ObjectInfo) {
//                    customClassList.add(it.item)
//                }
//            }
//            builder.appendTab(tab).append(")")
//            if (customClassList.isNotEmpty()) {
//                builder.append(" {\n")
//                customClassList.forEach {
//                    appendClass(it, builder, tab + 1)
//                }
//                builder.appendTab(tab).append("}")
//            }
//            builder.appendTab(tab).append("}\n")
//
//            builder::class.java.getConstructor()
//
//        }

//        private fun appendConstructor(className: String, fieldList: List<FieldInfo>, builder: StringBuilder, tab: Int) {
//
//        }

    }

}