package com.lorenzl5.hermes.passwort.generator

import java.math.BigInteger
import java.nio.charset.Charset
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import org.mindrot.jbcrypt.BCrypt
import org.mindrot.jbcrypt.Base64

class PasswordGenerator(
    domain: String,
    username: String,
    masterPassword: String,
    deviceId: String,
    iteration: Int,
    hashIterations: Int,
    hashAlgorithm: String,
    bcryptCost: String
) {
    private val hashValue: ByteArray

    init {
        // PBKDF2-HMAC-SHA256: key=masterPassword, password=salt, iterations=hashIterations
        // salt = (iteration*100 + domain + username + deviceId) as UTF-8
        val salt = (iteration * 100 + domain + username + deviceId).toByteArray(Charset.forName("UTF-8"))
        val keySpec = PBEKeySpec(
            masterPassword.toCharArray(),
            salt,
            hashIterations,
            256
        )
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(keySpec).encoded
        val base64Salt = Base64.encodeToString(keyBytes, Base64.NO_WRAP)
        // BCrypt: $2a$<bcryptCost>$<base64Salt>
        val bcryptHash = BCrypt.hashpw(masterPassword, "\$2a\$${bcryptCost}\$$base64Salt")
        // transformPassword: take last 31 bytes of UTF-8 encoding, drop first byte
        val utf8 = bcryptHash.toByteArray(Charset.forName("UTF-8"))
        hashValue = ByteArray(31)
        for (i in 0 until 31) {
            if (i + 1 < utf8.size) {
                hashValue[i] = utf8[i + 1]
            }
        }
    }

    fun getPassword(
        special: Int,
        lower: Int,
        upper: Int,
        numbers: Int,
        length: Int
    ): String {
        // Convert hashValue to BigInteger (with sign byte prepended as in original)
        val signed = ByteArray(hashValue.size + 1)
        signed[0] = 0
        System.arraycopy(hashValue, 0, signed, 1, hashValue.size)
        var bigInt = BigInteger(signed)

        // Character pools
        val specialPool = "#!\"~|@^°$%&/()[]{}=-_+*<>;.\"".map { it.toString() }.toMutableList()
        val lowerPool = "abcdefghijklmnopqrstuvwxyz".map { it.toString() }.toMutableList()
        val upperPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".map { it.toString() }.toMutableList()
        val numberPool = "0123456789".map { it.toString() }.toMutableList()

        // Selected pools based on flags
        val selected = mutableListOf<String>()
        if (special == 1) selected.addAll(specialPool)
        if (lower == 1) selected.addAll(lowerPool)
        if (upper == 1) selected.addAll(upperPool)
        if (numbers == 1) selected.addAll(numberPool)

        if (selected.isEmpty()) return ""

        // Build template
        val template = buildTemplate(special, lower, upper, numbers, length)
        val shuffledTemplate = shuffleTemplate(template)

        // Generate password by modular subtraction
        val sb = StringBuilder()
        for (ch in shuffledTemplate) {
            if (bigInt.compareTo(BigInteger.ZERO) <= 0) break
            val pool = when (ch) {
                'a' -> lowerPool
                'A' -> upperPool
                'n' -> numberPool
                's' -> specialPool
                else -> selected
            }
            val result = bigInt.divideAndRemainder(BigInteger.valueOf(pool.size.toLong()))
            sb.append(pool[result[1].toInt()])
            bigInt = result[0]
        }
        return sb.toString()
    }

    fun deleteFinalize() {
        // Zero out sensitive data
        for (i in hashValue.indices) hashValue[i] = 0
    }

    private fun buildTemplate(special: Int, lower: Int, upper: Int, numbers: Int, length: Int): String {
        val sb = StringBuilder()
        var count = 0
        if (special == 1) { sb.append('s'); count++ }
        if (lower == 1) { sb.append('a'); count++ }
        if (upper == 1) { sb.append('A'); count++ }
        if (numbers == 1) { sb.append('n'); count++ }
        while (count < length) { sb.append('x'); count++ }
        return sb.toString()
    }

    private fun shuffleTemplate(template: String): String {
        // Use the hash to create a deterministic shuffle
        val chars = template.toMutableList()
        var seed = 0
        for (b in hashValue) seed = seed * 31 + b
        var state = seed
        for (i in chars.indices.reversed()) {
            state = state xor 0x5bd1e995
            state = state * 1
            val j = Math.abs(state % (i + 1))
            val tmp = chars[i]
            chars[i] = chars[j]
            chars[j] = tmp
        }
        return chars.joinToString("")
    }
}
