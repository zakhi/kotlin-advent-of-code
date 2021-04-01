package zakhi


fun entireTextOf(fileName: String): String {
    return object {}::class.java.getResource("/zakhi/$fileName").readText()
}
