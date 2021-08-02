package io.eyram.spotii.utils

import java.security.MessageDigest


object Utils {

    fun sha256(input: String) = hashString(input)

    private fun hashString( input: String): ByteArray {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
    }
}