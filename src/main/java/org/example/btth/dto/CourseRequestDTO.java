package org.example.btth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/** DTO nhận dữ liệu từ client cho các yêu cầu POST và PUT */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequestDTO {

    @NotBlank(message = "Tên khóa học không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá khóa học không được để trống")
    @Positive(message = "Giá khóa học phải lớn hơn 0")
    private Double price;
}
