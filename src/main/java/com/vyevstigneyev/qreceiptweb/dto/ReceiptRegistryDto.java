package com.vyevstigneyev.qreceiptweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptRegistryDto {
    private UUID id;
    private ZonedDateTime createdDate;
    private Double total;
    private OrganizationRegistryDto organization;
}
