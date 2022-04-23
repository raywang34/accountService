package accountTest.service;

import account.model.User;
import account.repository.UserRepository;
import account.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;

/**
 * @author Ray
 */
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindByEmailIgnoreCase() {

        String email = "raywang@acme.com";

        // [Arrange] 預期資料
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setName("Ray");
        expectedUser.setLastname("Wang");
        expectedUser.setEmail(email);
        expectedUser.setPassword("$2a$10$y7duD3vb4bCC26OcZnRpC.FU50Kp59KBbl6pcxEEA2o74JqI9.nSa");
        expectedUser.setAccountNonLocked(true);
        expectedUser.setFailedAttempt(0);
        expectedUser.setRoles(List.of("ROLE_ADMINISTRATOR"));

        // 定義模擬呼叫userRepository.findByEmailIgnoreCase(email) 要回傳的預設結果
        Mockito.when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(expectedUser));

        // [Act] userService.findByEmailIgnoreCase(email)
        User actualUser = userService.findByEmailIgnoreCase(email);

        // [Assert] 預期與實際的資料
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testFindAll() {

        // [Arrange] 預期資料
        List<User> expectedUsers = new ArrayList<>();

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setName("Ray");
        expectedUser.setLastname("Wang");
        expectedUser.setEmail("raywang@acme.com");
        expectedUser.setPassword("$2a$10$y7duD3vb4bCC26OcZnRpC.FU50Kp59KBbl6pcxEEA2o74JqI9.nSa");
        expectedUser.setAccountNonLocked(true);
        expectedUser.setFailedAttempt(0);
        expectedUser.setRoles(List.of("ROLE_ADMINISTRATOR"));

        expectedUsers.add(expectedUser);

        expectedUser = new User();
        expectedUser.setId(2);
        expectedUser.setName("John");
        expectedUser.setLastname("Doe");
        expectedUser.setEmail("johndoe@acme.com");
        expectedUser.setPassword("$2a$10$y7duD3vb4bCC26OcZnRpC.FU50Kp59KBbl6pcxEEA2o74JqI9.nSa");
        expectedUser.setAccountNonLocked(true);
        expectedUser.setFailedAttempt(0);
        expectedUser.setRoles(List.of("ROLE_USER"));

        expectedUsers.add(expectedUser);

        // 定義模擬呼叫userRepository.findAll() 要回傳的預設結果
        Mockito.when(userRepository.findAll()).thenReturn(expectedUsers);

        // [Act] userService.findAll()
        List<User> actualUsers = userService.findAll();

        // [Assert] 預期與實際的資料
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testSave() {

        // [Arrange] 預期資料
        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setName("Ray");
        expectedUser.setLastname("Wang");
        expectedUser.setEmail("raywang@acme.com");
        expectedUser.setPassword("SecretSecret");
        expectedUser.setAccountNonLocked(true);
        expectedUser.setFailedAttempt(0);
        expectedUser.setRoles(List.of("ROLE_USER"));

        // 定義模擬呼叫userRepository.save(user) 要回傳的預設結果
        Mockito.when(userRepository.save(expectedUser)).thenReturn(expectedUser);

        // [Act] userService.save(user)
        User actualUser = userService.save(expectedUser);

        // [Assert] 預期與實際的資料
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testDeleteSuccess() {

        // 準備刪除的資料
        User expectedUser = new User();
        expectedUser.setId(2);
        expectedUser.setName("John");
        expectedUser.setLastname("Doe");
        expectedUser.setEmail("johndoe@acme.com");
        expectedUser.setPassword("$2a$10$y7duD3vb4bCC26OcZnRpC.FU50Kp59KBbl6pcxEEA2o74JqI9.nSa");
        expectedUser.setAccountNonLocked(true);
        expectedUser.setFailedAttempt(0);
        expectedUser.setRoles(List.of("ROLE_USER"));

        // 定義模擬呼叫userRepository.findById(id) 要回傳的預設結果
        Mockito.when(userRepository.findById(Long.valueOf(2))).thenReturn(Optional.of(expectedUser));

        // [Act] 實際呼叫操作todoService.deleteTodo()
        boolean actualDeleteRlt = userService.delete(expectedUser);

        //  [Assert] 預期與實際的資料
        assertEquals(true, actualDeleteRlt);
    }

    @Test
    public void testDeleteNotExist() {

        // 準備刪除的資料
        User expectedUser = new User();
        expectedUser.setId(100);
        expectedUser.setName("John");
        expectedUser.setLastname("Doe");
        expectedUser.setEmail("johndoe@acme.com");
        expectedUser.setPassword("$2a$10$y7duD3vb4bCC26OcZnRpC.FU50Kp59KBbl6pcxEEA2o74JqI9.nSa");
        expectedUser.setAccountNonLocked(true);
        expectedUser.setFailedAttempt(0);
        expectedUser.setRoles(List.of("ROLE_USER"));

        // 定義模擬呼叫userRepository.findById(id) 要回傳的預設結果
        Mockito.when(userRepository.findById(Long.valueOf(100))).thenReturn(Optional.empty());

        // [Act] 實際呼叫操作userService.delete(user)
        boolean actualDeleteRlt = userService.delete(expectedUser);

        //  [Assert] 預期與實際的資料
        assertEquals(false, actualDeleteRlt);
    }

    @Test
    public void testDeleteException() {

        // 準備刪除的資料
        User expectedUser = new User();
        expectedUser.setId(2);
        expectedUser.setName("John");
        expectedUser.setLastname("Doe");
        expectedUser.setEmail("johndoe@acme.com");
        expectedUser.setPassword("$2a$10$y7duD3vb4bCC26OcZnRpC.FU50Kp59KBbl6pcxEEA2o74JqI9.nSa");
        expectedUser.setAccountNonLocked(true);
        expectedUser.setFailedAttempt(0);
        expectedUser.setRoles(List.of("ROLE_USER"));

        // 定義模擬呼叫userRepository.findById(id) 要回傳的預設結果
        Mockito.when(userRepository.findById(Long.valueOf(2))).thenReturn(Optional.of(expectedUser));

        // 模擬呼叫userRepository.delete(user)，會發生NullPointerException
        doThrow(NullPointerException.class).when(userRepository).delete(expectedUser);

        // [Act] 實際呼叫操作userService.delete(user)
        boolean actualDeleteRlt = userService.delete(expectedUser);

        //  [Assert] 預期與實際的資料
        assertEquals(false, actualDeleteRlt);
    }
}