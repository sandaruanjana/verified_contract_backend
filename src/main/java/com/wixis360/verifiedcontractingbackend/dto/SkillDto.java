package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

@Data
public class SkillDto {
    private String id;
    private String type;
    private String name;
    private int isEnabled;
}
