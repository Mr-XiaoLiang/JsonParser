package com.lollipop.json

object KotlinDataClassBuilder : CodeBuilder {

    var annotationType: AnnotationType = AnnotationType.NONE

    override fun build(list: List<FieldInfo>): String {
        TODO("Not yet implemented")
    }

    enum class AnnotationType {
        NONE,
        GSON
    }

}