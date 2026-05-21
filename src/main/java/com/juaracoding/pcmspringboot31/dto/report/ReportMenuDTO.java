package com.juaracoding.pcmspringboot31.dto.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportMenuDTO {
    private String nama;
    private String path;
    @JsonIgnore
    private String deskripsi;
    @JsonProperty("kode_menu")
    private String kodeMenu;
}