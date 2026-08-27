package com.tranhienchuong.nomad.feature.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidatorsTest {

    @Test
    fun validateFullName_emptyOrBlank_returnsError() {
        val resultEmpty = AuthValidators.validateFullName("")
        assertFalse(resultEmpty.isValid)
        assertEquals("Vui lòng nhập họ và tên", resultEmpty.errorMessage)

        val resultBlank = AuthValidators.validateFullName("   ")
        assertFalse(resultBlank.isValid)
        assertEquals("Vui lòng nhập họ và tên", resultBlank.errorMessage)
    }

    @Test
    fun validateFullName_tooShort_returnsError() {
        val result = AuthValidators.validateFullName("A")
        assertFalse(result.isValid)
        assertEquals("Họ và tên phải có ít nhất 2 ký tự", result.errorMessage)
    }

    @Test
    fun validateFullName_valid_returnsSuccess() {
        val result = AuthValidators.validateFullName("Chuong Tran")
        assertTrue(result.isValid)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun validateEmail_invalidFormat_returnsError() {
        val empty = AuthValidators.validateEmail("")
        assertFalse(empty.isValid)
        assertEquals("Email không được để trống", empty.errorMessage)

        val invalid = AuthValidators.validateEmail("invalid-email")
        assertFalse(invalid.isValid)
        assertEquals("Định dạng email không hợp lệ", invalid.errorMessage)

        val missingDomain = AuthValidators.validateEmail("test@")
        assertFalse(missingDomain.isValid)
        assertEquals("Định dạng email không hợp lệ", missingDomain.errorMessage)
    }

    @Test
    fun validateEmail_valid_returnsSuccess() {
        val result = AuthValidators.validateEmail("user@nomad.com")
        assertTrue(result.isValid)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun validatePassword_lengthLessThan6_returnsError() {
        val empty = AuthValidators.validatePassword("")
        assertFalse(empty.isValid)
        assertEquals("Mật khẩu không được để trống", empty.errorMessage)

        val shortPass = AuthValidators.validatePassword("12345")
        assertFalse(shortPass.isValid)
        assertEquals("Mật khẩu phải có ít nhất 6 ký tự", shortPass.errorMessage)
    }

    @Test
    fun validatePassword_valid_returnsSuccess() {
        val result = AuthValidators.validatePassword("123456")
        assertTrue(result.isValid)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun validateConfirmPassword_mismatch_returnsError() {
        val mismatch = AuthValidators.validateConfirmPassword("123456", "654321")
        assertFalse(mismatch.isValid)
        assertEquals("Mật khẩu xác nhận không khớp", mismatch.errorMessage)
    }

    @Test
    fun validateConfirmPassword_match_returnsSuccess() {
        val match = AuthValidators.validateConfirmPassword("123456", "123456")
        assertTrue(match.isValid)
        assertEquals(null, match.errorMessage)
    }
}
