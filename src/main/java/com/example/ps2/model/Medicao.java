package com.example.ps2.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

@Data
@Document()
public class Medicao {

    @Id
    private String id;
    @PositiveOrZero
    private Double co2_vol;
    @PositiveOrZero
    private Double ph_vol;
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$")
    private String medicao_date;
    private Sensor sensor;
}
