package org.example.btth.service;

import lombok.RequiredArgsConstructor;
import org.example.btth.dto.*;
import org.example.btth.entity.Course;
import org.example.btth.repository.CourseRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final FileStorageService fileStorageService;

    // -------------------------------------------------------------------------
    // LẤY DỮ LIỆU
    // -------------------------------------------------------------------------

    /** Lấy danh sách khóa học có phân trang và sắp xếp */
    public Page<CourseResponseDTO> getAll(Pageable pageable) {
        return courseRepository.findAll(pageable).map(this::toResponseDTO);
    }

    /** Lấy thông tin một khóa học theo ID, trả 404 nếu không tìm thấy */
    public CourseResponseDTO getById(Long id) {
        return toResponseDTO(findOrThrow(id));
    }

    // -------------------------------------------------------------------------
    // TẠO MỚI
    // -------------------------------------------------------------------------

    /** Tạo khóa học mới — ID do database tự sinh, imageUrl ban đầu là null */
    public CourseResponseDTO create(CourseRequestDTO dto) {
        Course course = Course.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .build();
        return toResponseDTO(courseRepository.save(course));
    }

    // -------------------------------------------------------------------------
    // CẬP NHẬT TOÀN BỘ (PUT)
    // -------------------------------------------------------------------------

    /** Cập nhật toàn bộ name, description, price — imageUrl được giữ nguyên */
    public CourseResponseDTO update(Long id, CourseRequestDTO dto) {
        Course course = findOrThrow(id);
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setPrice(dto.getPrice());
        // imageUrl không thay đổi khi PUT
        return toResponseDTO(courseRepository.save(course));
    }

    // -------------------------------------------------------------------------
    // CẬP NHẬT MỘT PHẦN (PATCH)
    // -------------------------------------------------------------------------

    /** Chỉ cập nhật các trường được gửi lên (khác null) */
    public CourseResponseDTO patch(Long id, CoursePatchDTO dto) {
        Course course = findOrThrow(id);
        if (dto.getName() != null)        course.setName(dto.getName());
        if (dto.getDescription() != null) course.setDescription(dto.getDescription());
        if (dto.getPrice() != null)       course.setPrice(dto.getPrice());
        return toResponseDTO(courseRepository.save(course));
    }

    // -------------------------------------------------------------------------
    // XÓA KHÓA HỌC
    // -------------------------------------------------------------------------

    /** Xóa khóa học theo ID — đồng thời xóa file ảnh vật lý nếu có */
    public void delete(Long id) {
        Course course = findOrThrow(id);
        // Xóa file ảnh trên server trước khi xóa bản ghi trong database
        fileStorageService.delete(course.getImageUrl());
        courseRepository.delete(course);
    }

    // -------------------------------------------------------------------------
    // UPLOAD ẢNH
    // -------------------------------------------------------------------------

    /** Lưu ảnh mới, xóa ảnh cũ nếu đã có, cập nhật imageUrl trong database */
    public CourseResponseDTO uploadImage(Long id, MultipartFile file) throws IOException {
        Course course = findOrThrow(id);

        // Xóa ảnh cũ trước khi lưu ảnh mới để tránh file rác
        fileStorageService.delete(course.getImageUrl());

        String filename = fileStorageService.store(file);
        // Lưu đường dẫn URL tương đối để client có thể truy cập qua HTTP
        course.setImageUrl("/uploads/" + filename);
        return toResponseDTO(courseRepository.save(course));
    }

    // -------------------------------------------------------------------------
    // XÓA ẢNH
    // -------------------------------------------------------------------------

    /** Xóa file ảnh vật lý và đặt imageUrl về null trong database */
    public CourseResponseDTO deleteImage(Long id) {
        Course course = findOrThrow(id);
        if (course.getImageUrl() == null) {
            // Trả 400 nếu khóa học chưa có ảnh
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Khóa học với id " + id + " chưa có ảnh để xóa");
        }
        fileStorageService.delete(course.getImageUrl());
        course.setImageUrl(null);
        return toResponseDTO(courseRepository.save(course));
    }

    // -------------------------------------------------------------------------
    // PHƯƠNG THỨC HỖ TRỢ (PRIVATE)
    // -------------------------------------------------------------------------

    /** Tìm khóa học theo ID, ném ngoại lệ 404 nếu không tồn tại */
    private Course findOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy khóa học với id: " + id));
    }

    /** Chuyển đổi entity Course sang DTO để trả về cho client */
    private CourseResponseDTO toResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .price(course.getPrice())
                .imageUrl(course.getImageUrl())
                .build();
    }
}
