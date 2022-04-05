package account.security;

import account.model.Event;
import account.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    EventService eventService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception)
            throws IOException, ServletException {

        Date date = new Date();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String path = request.getRequestURI();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        Map<String, Object> data = new HashMap<>();
        data.put("path", path);
        data.put("timestamp", sdf.format(date));
        data.put("status", 403);
        data.put("error", "Forbidden");
        data.put("message", "Access Denied!");

        eventService.save(new Event(
                date,
                "ACCESS_DENIED",
                username,
                path,
                path));

        response.getOutputStream().println(objectMapper.writeValueAsString(data));
    }
}