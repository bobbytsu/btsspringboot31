package com.juaracoding.pcmspringboot31.dto.query;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class SearchAksesDTO {
    private String nama;
    private String deskripsi;
    private LocalDate start;
    private LocalDate end;
}