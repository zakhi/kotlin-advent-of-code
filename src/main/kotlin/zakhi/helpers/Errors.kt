package zakhi.helpers

fun fail(message: String? = null): Nothing = throw if (message == null) Exception() else Exception(message)
