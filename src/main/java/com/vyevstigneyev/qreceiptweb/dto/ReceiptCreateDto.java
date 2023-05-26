package com.vyevstigneyev.qreceiptweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptCreateDto {
    List<ReceiptCreateFieldDto> products = new ArrayList<>();
}
