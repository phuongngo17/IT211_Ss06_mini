package org.example.btth.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * DTO dùng cho yêu cầu PATCH.
 * Tất cả các trường đều không bắt buộc — chỉ trường nào khác null mới được cập nhật.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursePatchDTO {

    private String name;        // Tên mới (bỏ qua nếu null)

    private String description; // Mô tả mới (bỏ qua nếu null)

    @Positive(message = "Giá khóa học phải lớn hơn 0")
    private Double price;       // Giá mới (bỏ qua nếu null)
}
