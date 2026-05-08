package com.juaracoding.pcmspringboot31.dto.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juaracoding.pcmspringboot31.dto.RelDTO;
import com.juaracoding.pcmspringboot31.model.Menu;
import com.juaracoding.pcmspringboot31.util.ConstantMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ValAksesDTO {
    @NotNull(message = ConstantMessage.NOT_NULL)
    @NotEmpty(message = ConstantMessage.NOT_EMPTY)
    @NotBlank(message = ConstantMessage.NOT_BLANK)
    @Pattern(regexp = "^[a-zA-Z0-9\\s]{5,20}$", message = "Alfanumeric dan spasi min 5 maks 20")
    private String nama;
    @NotNull(message = ConstantMessage.NOT_NULL)
    @NotEmpty(message = ConstantMessage.NOT_EMPTY)
    @NotBlank(message = ConstantMessage.NOT_BLANK)
    @Pattern(regexp = "^[a-zA-Z0-9\\s]{20,255}$", message = "Alfanumeric dan spasi min 20 maks 255")
    private String deskripsi;
//    @NotNull(message = ConstantMessage.NOT_NULL)
    private List<Long> menuId;
}