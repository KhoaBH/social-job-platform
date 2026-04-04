package vn.edu.uit.socialjob.platform.modules.network.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SuggestionDTO {

    private UUID userId;
    private double score;
    private int mutual;
}