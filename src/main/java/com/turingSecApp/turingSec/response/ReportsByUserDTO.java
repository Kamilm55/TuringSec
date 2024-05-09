package com.turingSecApp.turingSec.response;

import com.turingSecApp.turingSec.dao.entities.ReportsEntity;
import com.turingSecApp.turingSec.response.UserDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportsByUserDTO {
   // private Long id;
    private Long userId;
    private UserDTO user;
    private boolean has_hacker_profile_pic;
    //private String userImgUrl; // Add image URL field
    private List<ReportsEntity> reports;
}
