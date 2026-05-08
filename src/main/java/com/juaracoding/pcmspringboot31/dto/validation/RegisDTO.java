package com.juaracoding.pcmspringboot31.dto.validation;


import jakarta.validation.constraints.Pattern;

public class RegisDTO {
    private Long id;
    @Pattern(regexp = "^[a-zA-Z ]{4,75}$", message = "Nama lengkap hanya boleh mengandung huruf dan spasi")
    private String namaLengkap;
    private String email;
    @Pattern(regexp = "^[a-zA-Z ]{8,16}$", message = "Nama lengkap hanya boleh mengandung huruf dan spasi")
    private String password;
    private String username;
    /**
     * 0812 / 62812 / +62812
     * 0813 / 62813 / +62813
     * 0815 / 62815 / +62815
     */
    @Pattern(regexp = "^[0|62|+62]8[12|13|15|16|21|22|23|][0-9]{6}$", message = "Nama lengkap hanya boleh mengandung huruf dan spasi")
    private String otp;
}
