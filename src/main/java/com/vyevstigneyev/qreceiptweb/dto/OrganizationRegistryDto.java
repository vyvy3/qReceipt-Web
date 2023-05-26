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
public class OrganizationRegistryDto {
    private String name;
    private String category;
    private UUID picture;
}
