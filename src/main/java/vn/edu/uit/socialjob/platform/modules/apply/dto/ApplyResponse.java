package vn.edu.uit.socialjob.platform.modules.apply.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyResponse {
    private UUID id;
    private UUID jobPostId;
    private UUID userId;
    private String fileName;
    private LocalDateTime createdAt;
}
