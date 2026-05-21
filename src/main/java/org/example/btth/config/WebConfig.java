package org.example.btth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

/**
 * Cấu hình phục vụ file tĩnh từ thư mục uploads.
 * Cho phép truy cập ảnh qua URL: GET /uploads/{ten-file}
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Chuyển đường dẫn tương đối thành URI tuyệt đối để Spring MVC phục vụ đúng
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absolutePath);
    }
}
