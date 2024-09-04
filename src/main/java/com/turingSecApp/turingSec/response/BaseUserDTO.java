package com.turingSecApp.turingSec.response;

import com.turingSecApp.turingSec.model.entities.report.embedded.AttachmentDetails;
import com.turingSecApp.turingSec.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseUserDTO {
    private UUID id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
//    private boolean activated;
    private String country;

    private Set<Role> roles = new HashSet<>();
//    private AttachmentDetails profile_img;
//    private AttachmentDetails background_img;
}
