package me.exrates.riskmanagement.service;

import me.exrates.riskmanagement.model.Event;
import org.springframework.transaction.annotation.Transactional;

public interface EventsService {
    @Transactional
    long saveNewEvent(Event event);
}
