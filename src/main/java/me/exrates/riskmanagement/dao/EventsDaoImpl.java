package me.exrates.riskmanagement.dao;

import me.exrates.riskmanagement.model.Event;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class EventsDaoImpl implements EventsDao{

    private final NamedParameterJdbcOperations jdbcOperations;

    public EventsDaoImpl(@Qualifier("masterTemplate") NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Event> getAllEvents() {
        final String sql = "SELECT * FROM EVENT";
        return jdbcOperations.queryForList(sql, Collections.EMPTY_MAP, Event.class);
    }

    @Override
    public long save(Event event) {
        /*todo: impl*/
        return 0L;
    }
}
