package account.security;

import account.model.Event;
import account.model.User;
import account.service.EventService;
import account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    @Autowired
    private HttpServletRequest request;

    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {

        String path = request.getRequestURI();

        String username = e.getAuthentication().getName();

        eventService.save(new Event(
                new Date(),
                "LOGIN_FAILED",
                username,
                path,
                path));

        User user = userService.findByEmailIgnoreCase(username);

        if (user != null && !user.getRoles().contains("ROLE_ADMINISTRATOR")) {
            int num = user.getFailedAttempt();

            if (num < MAX_FAILED_ATTEMPTS) {
                num++;
                user.setFailedAttempt(num);
            }

            if (num == 5) {
                eventService.save(new Event(
                        new Date(),
                        "BRUTE_FORCE",
                        username,
                        path,
                        path));

                user.setAccountNonLocked(false);

                eventService.save(new Event(
                        new Date(),
                        "LOCK_USER",
                        username,
                        String.format("Lock user %s", username),
                        path));
            }

            userService.save(user);
        }
    }
}