package com.turingSecApp.turingSec.payload;

import com.turingSecApp.turingSec.dao.entities.AssetTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterCompanyPayload {
    private String firstName;
    private String lastName;
    private String email;
    private String companyName;
    private String jobTitle;
   // private Set<Long> assets_id;
    private String message;
   // private Set<> bugbounty;
}
