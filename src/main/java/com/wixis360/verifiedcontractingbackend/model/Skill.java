package com.wixis360.verifiedcontractingbackend.model;

import lombok.Data;

@Data
public class Skill {
    private String id;
    private String type;
    private String name;
    private int isEnabled;
}
