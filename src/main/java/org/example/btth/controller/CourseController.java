package org.example.btth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.btth.dto.*;
import org.example.btth.service.CourseService;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Lấy danh sách khóa học có phân trang và sắp xếp.
     * Ví dụ: GET /api/courses?page=0&size=5&sort=price,desc
     */
    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(courseService.getAll(pageable));
    }

    /**
     * Lấy thông tin chi tiết một khóa học theo ID.
     * Trả về 404 nếu không tìm thấy.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    /**
     * Tạo mới khóa học.
     * Không truyền id và imageUrl — database tự sinh ID, imageUrl mặc định null.
     * Trả về 201 Created kèm dữ liệu vừa tạo.
     */
    @PostMapping
    public ResponseEntity<CourseResponseDTO> create(
            @Valid @RequestBody CourseRequestDTO dto) {
        CourseResponseDTO created = courseService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Cập nhật toàn bộ thông tin khóa học (name, description, price).
     * imageUrl được giữ nguyên, không bị thay đổi.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO dto) {
        return ResponseEntity.ok(courseService.update(id, dto));
    }

    /**
     * Cập nhật một phần thông tin khóa học.
     * Chỉ các trường được gửi lên mới được cập nhật.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> patch(
            @PathVariable Long id,
            @Valid @RequestBody CoursePatchDTO dto) {
        return ResponseEntity.ok(courseService.patch(id, dto));
    }

    /**
     * Xóa khóa học theo ID.
     * Đồng thời xóa file ảnh vật lý trên server nếu có.
     * Trả về 204 No Content khi thành công.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload ảnh đại diện cho khóa học.
     * Dùng form-data với key = "file", chọn file ảnh (JPG/PNG, tối đa 5MB).
     * Nếu đã có ảnh cũ thì xóa trước khi lưu ảnh mới.
     */
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponseDTO> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(courseService.uploadImage(id, file));
    }

    /**
     * Xóa ảnh đại diện của khóa học.
     * Xóa file vật lý trên server và đặt imageUrl về null trong database.
     */
    @DeleteMapping("/{id}/image")
    public ResponseEntity<CourseResponseDTO> deleteImage(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.deleteImage(id));
    }
}
