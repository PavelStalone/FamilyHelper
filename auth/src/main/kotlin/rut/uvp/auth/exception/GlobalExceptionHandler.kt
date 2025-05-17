package rut.uvp.auth.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    data class ApiError(val timestamp: LocalDateTime = LocalDateTime.now(), val status: Int, val message: String)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<ApiError> {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid request")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val msg = ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return buildResponse(HttpStatus.BAD_REQUEST, msg)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuth(ex: AuthenticationException): ResponseEntity<ApiError> {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.message ?: "Unauthorized")
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ApiError> {
        return buildResponse(HttpStatus.FORBIDDEN, ex.message ?: "Forbidden")
    }

    @ExceptionHandler(Exception::class)
    fun handleOther(ex: Exception): ResponseEntity<ApiError> {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.message ?: "Internal error")
    }

    private fun buildResponse(status: HttpStatus, message: String): ResponseEntity<ApiError> {
        return ResponseEntity.status(status).body(ApiError(status = status.value(), message = message))
    }
} 