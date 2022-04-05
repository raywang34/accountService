package account.controller;

import account.model.Event;
import account.model.Group;
import account.model.User;
import account.service.EventService;
import account.service.GroupService;
import account.service.UserService;
import account.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@RestController
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    EventService eventService;

    @Autowired
    PasswordEncoder encoder;

    private final Set<String> hackerPassSet = Set.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch",
            "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    @PostMapping("/api/auth/signup")
    public User postUser(@AuthenticationPrincipal UserDetails details, @Valid @RequestBody User user) {

        checkPassword("", user.getPassword());

        User existingUser = userService.findByEmailIgnoreCase(user.getEmail());

        if (existingUser == null) {
            Set<Group> groups = new HashSet<>();

            if (userService.count() == 0) {
                groups.add(groupService.findByCodeIgnoreCase("ROLE_ADMINISTRATOR"));
            } else {
                groups.add(groupService.findByCodeIgnoreCase("ROLE_USER"));
            }

            user.setUserGroups(groups);
            user.setRoles(UserUtil.getRoles(user.getUserGroups()));
            user.setPassword(encoder.encode(user.getPassword()));
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);

            userService.save(user);
            eventService.save(new Event(
                    new Date(),
                    "CREATE_USER",
                    details == null ? "Anonymous" : details.getUsername(),
                    user.getEmail().toLowerCase(Locale.ROOT),
                    "/api/auth/signup"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }

        return user;
    }

    @PostMapping("api/auth/changepass")
    public Map<String, String> changePassword(@AuthenticationPrincipal UserDetails details, @RequestBody Map<String, String> requestBody) {

        if (details == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String email = details.getUsername();
        String oldPassword = details.getPassword();
        String newPassword = requestBody.get("new_password");

        checkPassword(oldPassword, newPassword);

        User user = userService.findByEmailIgnoreCase(email);
        user.setPassword(encoder.encode(newPassword));
        userService.save(user);
        eventService.save(new Event(
                new Date(),
                "CHANGE_PASSWORD",
                email,
                email,
                "/api/auth/changepass"));

        return Map.of("email", email.toLowerCase(Locale.ROOT), "status", "The password has been updated successfully");
    }

    private void checkPassword(String oldPassword, String newPassword) {

        if (newPassword.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!");
        }

        if (hackerPassSet.contains(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }

        if (!oldPassword.isEmpty()) {
            if (encoder.matches(newPassword, oldPassword)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
            }
        }
    }
}