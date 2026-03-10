package vn.edu.uit.socialjob.platform.modules.auth.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account.path:}")
    private Resource serviceAccount;

    @Value("${FIREBASE_JSON_CONTENT:}")
    private String firebaseJsonContent;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        InputStream stream;

        // Ưu tiên đọc từ biến môi trường (Dành cho Render)
        if (firebaseJsonContent != null && !firebaseJsonContent.trim().isEmpty()) {
            stream = new ByteArrayInputStream(firebaseJsonContent.getBytes(StandardCharsets.UTF_8));
        } 
        // Nếu không có biến môi trường thì mới đọc từ file (Dành cho Local)
        else if (serviceAccount != null && serviceAccount.exists()) {
            stream = serviceAccount.getInputStream();
        } else {
            throw new IOException("Không tìm thấy cấu hình Firebase (cả file lẫn biến môi trường đều trống!)");
        }

        try (stream) {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(stream))
                .build();
            return FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}