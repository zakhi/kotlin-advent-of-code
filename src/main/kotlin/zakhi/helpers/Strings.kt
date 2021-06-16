package zakhi.helpers

import java.math.BigInteger
import java.security.MessageDigest


fun String.md5Hash(): String {
    val md5 = MessageDigest.getInstance("MD5")
    return BigInteger(1, md5.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun <T> Array<T>.join(transform: ((T) -> CharSequence)? = null): String =
    joinToString(separator = "", transform = transform)

fun <T> Sequence<T>.join(transform: ((T) -> CharSequence)? = null): String =
    joinToString(separator = "", transform = transform)

fun <T> Iterable<T>.join(transform: ((T) -> CharSequence)? = null): String =
    joinToString(separator = "", transform = transform)
