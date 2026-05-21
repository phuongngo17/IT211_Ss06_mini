package org.example.btth.exception;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Xử lý tập trung tất cả ngoại lệ trong ứng dụng.
 * Trả về JSON thống nhất với đầy đủ thông tin lỗi.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Lỗi validation từ @Valid (ví dụ: thiếu name, price âm)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ", fieldErrors);
    }

    // Lỗi file upload vượt quá kích thước cho phép (5MB)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE,
                "Kích thước file vượt quá giới hạn cho phép (5 MB)", null);
    }

    // Lỗi định dạng file không hợp lệ hoặc file rỗng
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // Lỗi 404 Not Found hoặc 400 Bad Request được ném qua ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildErrorResponse(status, ex.getReason(), null);
    }

    // Bắt tất cả các lỗi không mong muốn còn lại
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Đã xảy ra lỗi không mong muốn: " + ex.getMessage(), null);
    }

    // -------------------------------------------------------------------------

    /** Tạo body JSON chuẩn cho tất cả các response lỗi */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message, Object details) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (details != null) body.put("details", details);
        return ResponseEntity.status(status).body(body);
    }
}
