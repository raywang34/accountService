package account.controller;

import account.model.Event;
import account.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SecurityController {

    @Autowired
    EventService eventService;

    @GetMapping("/api/security/events")
    public List<Event> getEvents() {
        return eventService.findAll();
    }
}