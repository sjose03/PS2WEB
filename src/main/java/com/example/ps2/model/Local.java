package com.example.ps2.model;


import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data

public class Local {


    @NotBlank
    private String nome;
    @NotBlank
    private  String uf;
    @NotBlank
    private  String cidade;
}
