package vn.edu.uit.socialjob.platform.modules.company.dto;
import java.util.UUID;

import com.google.firebase.internal.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.uit.socialjob.platform.common.enums.CompanyRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyUserRequest {
    
    @NonNull
    private UUID userId;

    @NonNull
    private CompanyRole role;
}
