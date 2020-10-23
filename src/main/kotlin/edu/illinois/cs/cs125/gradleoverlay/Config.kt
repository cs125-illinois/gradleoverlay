package edu.illinois.cs.cs125.gradleoverlay

data class Config(
    val overwrite: List<String>,
    val merge: List<String> = listOf(),
    val delete: List<String> = listOf()
)
