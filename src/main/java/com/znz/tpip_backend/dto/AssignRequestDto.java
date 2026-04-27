package com.znz.tpip_backend.dto;

import lombok.Data;

@Data
public class AssignRequestDto {
    private Long internId;
    private Long schoolId;
    private Long mentorId;

}
