package org.example.btth.entity;

import jakarta.persistence.*;
import lombok.*;

/** Entity ánh xạ tới bảng "courses" trong database MySQL */
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID tự tăng do database sinh
    private Long id;

    @Column(nullable = false) // Tên khóa học, bắt buộc không được null
    private String name;

    @Column(columnDefinition = "TEXT") // Mô tả dài, dùng kiểu TEXT thay vì VARCHAR
    private String description;

    @Column(nullable = false) // Giá khóa học, bắt buộc không được null
    private Double price;

    @Column(name = "image_url") // Đường dẫn ảnh đại diện, cho phép null
    private String imageUrl;
}
