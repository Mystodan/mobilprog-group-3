package whenweekly.misc

import java.util.*

/**
 * Convert UUID to byte array
 *
 * @return Byte array
 */
fun genInvCode(): String {
    fun getRandNum(min: Int, max: Int): Int = Random().nextInt(max + 1) + min
    var invCode = ""
    val symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    for (i in 0..19) {
        invCode += symbols[getRandNum(0, symbols.length - 1)]
    }
    return invCode
}

