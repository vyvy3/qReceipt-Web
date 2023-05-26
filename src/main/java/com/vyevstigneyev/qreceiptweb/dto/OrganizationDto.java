package com.vyevstigneyev.qreceiptweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto {
    private String name;
    private String description;
    private UUID picture;
    private String merchandiseCategory;
}
