package vn.edu.uit.socialjob.platform.modules.jobpost.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchJobRequest {
    private String text; // Chuỗi tìm kiếm đã qua xử lý Auto-suggest
    private String location;
    private List<String> skillIds; // Mảng UUID bóc tách từ FE
    private Integer minSalary;
    private Integer maxSalary;
}
    