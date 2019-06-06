package me.exrates.riskmanagement.dao;

import me.exrates.riskmanagement.model.Event;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class EventsDaoImpl implements EventsDao{

    @Qualifier("masterTemplate")
    @Autowired
    private NamedParameterJdbcOperations jdbcOperations;
    
    @Override
    public List<Event> getAllEvents() {
        final String sql = "SELECT * FROM EVENT";
        return jdbcOperations.queryForList(sql, Collections.EMPTY_MAP, Event.class);
    }
}
