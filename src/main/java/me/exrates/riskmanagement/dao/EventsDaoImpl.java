package me.exrates.riskmanagement.dao;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class EventsDaoImpl implements EventsDao{

    @Qualifier("masterTemplate")
    @Autowired
    private NamedParameterJdbcOperations jdbcOperations;
}
