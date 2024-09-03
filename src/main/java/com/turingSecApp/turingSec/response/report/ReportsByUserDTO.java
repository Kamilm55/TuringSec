package com.turingSecApp.turingSec.response.report;

import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.response.user.UserDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportsByUserDTO {
    private String userId;
    private UserDTO user;
    private boolean has_hacker_profile_pic;
    //private String userImgUrl; // Add image URL field
    private List<Report> reports;
}
