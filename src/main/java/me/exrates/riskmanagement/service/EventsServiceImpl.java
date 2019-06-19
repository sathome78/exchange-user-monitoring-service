package me.exrates.riskmanagement.service;

import me.exrates.riskmanagement.dao.EventsDao;
import me.exrates.riskmanagement.model.Event;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventsServiceImpl implements EventsService {

    private final EventsDao eventsDao;

    public EventsServiceImpl(EventsDao eventsDao) {
        this.eventsDao = eventsDao;
    }

    @Transactional
    @Override
    public long saveNewEvent(Event event) {
        return eventsDao.save(event);
    }
}
