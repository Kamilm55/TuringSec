package com.turingSecApp.turingSec.Request;

import com.turingSecApp.turingSec.dao.entities.ReportsEntity;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportsByUserDTO {
    private Long id;
    private Long userId;
    private UserDTO user;
    private String userImgUrl; // Add image URL field
    private List<ReportsEntity> reports;
}
