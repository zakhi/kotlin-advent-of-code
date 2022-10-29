package zakhi.aoc2020

import zakhi.aoc2020.PassportField.cid
import zakhi.helpers.entireTextOf
import zakhi.helpers.mapDestructured


fun main() {
    val passports = entireTextOf("aoc2020/day4").split("\n\n").map { group ->
        Passport(Regex("""(\w{3}):(\S+)""").findAll(group)
            .mapDestructured { (a, b) -> PassportField.valueOf(a) to b }.toMap())
    }

    val validPassports = passports.count { it.valid }
    println("The number of valid passports is $validPassports")

    val validPassportsWithValues = passports.count { it.validWithValues }
    println("The number of valid passports with values is $validPassportsWithValues")
}

private data class Passport(
    val fields: Map<PassportField, String>
) {
    val valid: Boolean get() = PassportField.values().all { it == cid || it in fields }

    val validWithValues: Boolean get() = valid && fields.all { it.key.isValid(it.value) }
}

private enum class PassportField(
    private val validation: Regex
) {
    byr(Regex("""19[2-9]\d|200[0-2]""")),
    iyr(Regex("""201\d|2020""")),
    eyr(Regex("""202\d|2030""")),
    hgt(Regex("""(1[5-8]\d|19[0-3])cm|(59|6\d|7[0-6])in""")),
    hcl(Regex("""#[0-9a-f]{6}""")),
    ecl(Regex("""amb|blu|brn|gry|grn|hzl|oth""")),
    pid(Regex("""\d{9}""")),
    cid(Regex(""".*"""));

    fun isValid(value: String) = validation.matches(value)
}
