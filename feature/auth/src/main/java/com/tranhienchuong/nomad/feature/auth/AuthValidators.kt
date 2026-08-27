package com.tranhienchuong.nomad.feature.auth

import java.util.regex.Pattern

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
)

object AuthValidators {
    private val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
    )

    fun validateFullName(fullName: String): ValidationResult {
        val trimmed = fullName.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "Vui lòng nhập họ và tên")
            trimmed.length < 2 -> ValidationResult(false, "Họ và tên phải có ít nhất 2 ký tự")
            else -> ValidationResult(true)
        }
    }

    fun validateEmail(email: String): ValidationResult {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(false, "Email không được để trống")
            !EMAIL_ADDRESS_PATTERN.matcher(trimmed).matches() -> ValidationResult(false, "Định dạng email không hợp lệ")
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult(false, "Mật khẩu không được để trống")
            password.length < 6 -> ValidationResult(false, "Mật khẩu phải có ít nhất 6 ký tự")
            else -> ValidationResult(true)
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isEmpty() -> ValidationResult(false, "Vui lòng nhập lại mật khẩu")
            confirmPassword != password -> ValidationResult(false, "Mật khẩu xác nhận không khớp")
            else -> ValidationResult(true)
        }
    }
}
