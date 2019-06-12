package me.exrates.riskmanagement.model;

import lombok.Data;
import me.exrates.riskmanagement.model.enums.EventCathegoryEnum;
import me.exrates.riskmanagement.model.enums.EventTypeEnum;

import java.time.LocalDateTime;

@Data
public class Event extends BaseEntity{

    private EventTypeEnum type;
    private EventCathegoryEnum event;
    private String email;
    private String description;
    private LocalDateTime timestamp;
    private Object metadata;
}
