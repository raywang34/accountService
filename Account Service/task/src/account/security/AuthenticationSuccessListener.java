package account.security;

import account.model.User;
import account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements
        ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    UserService userService;

    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {

        String username = e.getAuthentication().getName();

        User user = userService.findByEmailIgnoreCase(username);

        user.setFailedAttempt(0);
        userService.save(user);
    }
}