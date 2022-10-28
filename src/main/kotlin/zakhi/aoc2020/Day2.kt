package zakhi.aoc2020

import zakhi.helpers.matchEachLineOf


fun main() {
    val passwords = matchEachLineOf("aoc2020/day2", Regex("""(\d+)-(\d+) (\w): (\w+)""")) { (min, max, letter, password) ->
        PasswordWithPolicy(min.toInt()..max.toInt(), letter.first(), password)
    }

    val validCount = passwords.count { it.valid }
    println("The number of valid passwords is $validCount")

    val validByNewPolicyCount = passwords.count { it.validByNewPolicy }
    println("The number of valid passwords by the new policy is $validByNewPolicyCount")
}


private data class PasswordWithPolicy(
    val appearances: IntRange,
    val letter: Char,
    val password: String
) {
    val valid: Boolean get() = password.count { it == letter } in appearances

    val validByNewPolicy: Boolean get() =
        (password[appearances.first - 1] == letter) xor (password[appearances.last - 1] == letter)
}
