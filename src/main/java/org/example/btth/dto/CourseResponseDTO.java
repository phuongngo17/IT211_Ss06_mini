package org.example.btth.dto;

import lombok.*;

/** DTO trả về cho client, chứa đầy đủ thông tin của một khóa học */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponseDTO {

    private Long id;            // Mã khóa học
    private String name;        // Tên khóa học
    private String description; // Mô tả chi tiết
    private Double price;       // Giá khóa học
    private String imageUrl;    // Đường dẫn ảnh đại diện (null nếu chưa có)
}
