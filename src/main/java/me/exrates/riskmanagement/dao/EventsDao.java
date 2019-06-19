package me.exrates.riskmanagement.dao;

import me.exrates.riskmanagement.model.Event;

import java.util.List;

public interface EventsDao {
    List<Event> getAllEvents();

    long save(Event event);
}
