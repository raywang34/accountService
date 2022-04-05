package account.controller;

import account.model.Event;
import account.model.Group;
import account.model.Role;
import account.model.User;
import account.service.EventService;
import account.service.GroupService;
import account.service.UserService;
import account.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    EventService eventService;

    @PutMapping("/api/admin/user/role")
    public User putRole(@AuthenticationPrincipal UserDetails details, @RequestBody Role role) {
        String code = "ROLE_" + role.getRole();

        User user = userService.findByEmailIgnoreCase(role.getUser());
        Group group = groupService.findByCodeIgnoreCase(code);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        if (group == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        Set<Group> existingGroups = user.getUserGroups();

        switch (role.getOperation()) {
            case "GRANT":
                existingGroups.add(group);

                for (Group existingGroup : existingGroups) {
                    if (existingGroups.size() > 1 && existingGroup.getCode().equals("ROLE_ADMINISTRATOR")) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "The user cannot combine administrative and business roles!");
                    }
                }

                user.setUserGroups(existingGroups);
                userService.save(user);
                eventService.save(new Event(
                        new Date(),
                        "GRANT_ROLE",
                        details.getUsername(),
                        String.format("Grant role %s to %s", role.getRole(), user.getEmail()),
                        "/api/admin/user/role"));
                break;
            case "REMOVE":
                if (code.equals("ROLE_ADMINISTRATOR")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Can't remove ADMINISTRATOR role!");
                }

                if (!user.getRoles().contains(code)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "The user does not have a role!");
                }

                if (existingGroups.size() == 1) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "The user must have at least one role!");
                }

                existingGroups.remove(group);

                user.setUserGroups(existingGroups);
                userService.save(user);
                eventService.save(new Event(
                        new Date(),
                        "REMOVE_ROLE",
                        details.getUsername(),
                        String.format("Remove role %s from %s", role.getRole(), user.getEmail()),
                        "/api/admin/user/role"));
                break;
            default:
                break;
        }

        user.setRoles(UserUtil.getRoles(existingGroups));

        return user;
    }

    @GetMapping("/api/admin/user")
    public List<User> getUser() {
        return userService.findAll();
    }

    @DeleteMapping("/api/admin/user/{email}")
    public Map<String, String> deleteUser(@AuthenticationPrincipal UserDetails details, @PathVariable String email) {

        User user = userService.findByEmailIgnoreCase(email);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        } else if(user.getRoles().contains("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        } else {
            userService.delete(user);
            eventService.save(new Event(
                    new Date(),
                    "DELETE_USER",
                    details.getUsername(),
                    user.getEmail(),
                    "/api/admin/user"));
        }

        return Map.of("user", email, "status", "Deleted successfully!");
    }

    @PutMapping("api/admin/user/access")
    public Map<String, String> changeUserAccess(@AuthenticationPrincipal UserDetails details, @RequestBody Map<String,String> requestBody) {

        User user = userService.findByEmailIgnoreCase(requestBody.get("user"));

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        String username = user.getEmail();
        String operation = requestBody.get("operation");

        if (operation.equals("LOCK")) {
            if (user.getRoles().contains("ROLE_ADMINISTRATOR")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
            }

            user.setAccountNonLocked(false);
            userService.save(user);
            eventService.save(new Event(
                    new Date(),
                    "LOCK_USER",
                    details.getUsername(),
                    String.format("Lock user %s", username),
                    "api/admin/user/access"));

        } else if (operation.equals("UNLOCK")) {
            user.setFailedAttempt(0);
            user.setAccountNonLocked(true);
            userService.save(user);
            eventService.save(new Event(
                    new Date(),
                    "UNLOCK_USER",
                    details.getUsername(),
                    String.format("Unlock user %s", username),
                    "api/admin/user/access"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation should be \"LOCK\" or \"UNLOCK\"!");
        }

        return Map.of("status", String.format("User %s %s!", username, operation.toLowerCase(Locale.ROOT) + "ed"));
    }
}