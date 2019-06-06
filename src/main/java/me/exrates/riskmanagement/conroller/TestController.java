package me.exrates.riskmanagement.conroller;

import me.exrates.riskmanagement.dao.EventsDao;
import me.exrates.riskmanagement.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private EventsDao eventsDao;

    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test(HttpServletRequest request) {
        return request.getServletPath() + " application status ok";
    }

    @GetMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> test() {
        return eventsDao.getAllEvents();
    }
}
