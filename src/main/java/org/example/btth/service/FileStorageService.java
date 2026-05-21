package org.example.btth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    // Danh sách các định dạng ảnh được phép upload
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private final Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDirPath) throws IOException {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        // Tạo thư mục uploads nếu chưa tồn tại
        Files.createDirectories(this.uploadDir);
    }

    /**
     * Kiểm tra hợp lệ và lưu file ảnh vào thư mục uploads.
     *
     * @return tên file đã lưu (UUID + phần mở rộng gốc)
     */
    public String store(MultipartFile file) throws IOException {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        // Lấy phần mở rộng từ tên file gốc (ví dụ: .jpg, .png)
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        // Đặt tên file mới bằng UUID để đảm bảo không trùng lặp
        String newFilename = UUID.randomUUID() + extension;
        Path targetPath = uploadDir.resolve(newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return newFilename;
    }

    /**
     * Xóa file vật lý theo tên đã lưu.
     * Bỏ qua nếu file không tồn tại hoặc tên file null/rỗng.
     */
    public void delete(String filename) {
        if (filename == null || filename.isBlank()) return;
        try {
            // Lấy tên file thuần túy phòng trường hợp imageUrl lưu cả đường dẫn URL
            String name = Paths.get(filename).getFileName().toString();
            Path filePath = uploadDir.resolve(name).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Ghi log cảnh báo nhưng không ném ngoại lệ để tránh ảnh hưởng đến API
            System.err.println("Cảnh báo: không thể xóa file " + filename + ": " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------

    /** Kiểm tra file không rỗng và đúng định dạng ảnh cho phép */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Định dạng file không hợp lệ. Chỉ chấp nhận JPG, PNG, GIF và WEBP.");
        }
    }
}
