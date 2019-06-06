package me.exrates.riskmanagement.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Event {
    
    private int id;
    private String email;
    private String descritpion;
    private LocalDateTime timestamp;
}
