package account.service;

import account.model.UserDetailsImpl;
import account.model.User;
import account.repository.UserRepository;
import account.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Locale;

/**
 * @author Ray
 */
@Service
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, LockedException {
        User user = findByEmailIgnoreCase(email);

        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + email);
        }

        if (!user.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }

        return new UserDetailsImpl(user);
    }

    public User findByEmailIgnoreCase(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);

        if (user != null) {
            user.setRoles(UserUtil.getRoles(user.getUserGroups()));
        }

        return user;
    }

    public List<User> findAll() {
        List<User> users = userRepository.findAll();

        for (User user: users) {
            user.setRoles(UserUtil.getRoles(user.getUserGroups()));
        }

        return users;
    }

    public User save(User toSave) {
        toSave.setEmail(toSave.getEmail().toLowerCase(Locale.ROOT));
        return userRepository.save(toSave);
    }

    public boolean delete(User toDelete) {

        boolean result = false;

        if (userRepository.findById(toDelete.getId()).isPresent()) {
            try {
                userRepository.delete(toDelete);
                result = true;
            } catch (Exception ex) {

            }
        }

        return result;
    }

    public long count() {
        return userRepository.count();
    }
}