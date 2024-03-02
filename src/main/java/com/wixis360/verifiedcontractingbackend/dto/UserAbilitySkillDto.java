package com.wixis360.verifiedcontractingbackend.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserAbilitySkillDto {
    private String id;
    private List<String> abilities;
    private List<String> skills;
}
