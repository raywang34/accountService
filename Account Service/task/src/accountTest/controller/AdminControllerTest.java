package accountTest.controller;

import account.controller.AdminController;
import account.model.Group;
import account.model.Role;
import account.model.User;
import account.service.EventService;
import account.service.GroupService;
import account.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ray
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    UserService userService;

    @Mock
    GroupService groupService;

    @Mock
    EventService eventService;

    @InjectMocks
    private AdminController adminController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    public void putRoleGrantTest() throws Exception {
        // [Arrange] 預期資料
        User initialUser = new User();
        initialUser.setId(2);
        initialUser.setName("John");
        initialUser.setLastname("Doe");
        initialUser.setEmail("johndoe@acme.com");
        initialUser.setRoles(List.of("ROLE_USER"));

        User expectedUser = new User();
        expectedUser.setId(2);
        expectedUser.setName("John");
        expectedUser.setLastname("Doe");
        expectedUser.setEmail("johndoe@acme.com");
        expectedUser.setRoles(List.of("ROLE_USER", "ROLE_ACCOUNTANT"));

        Role role = new Role();
        role.setUser("johndoe@acme.com");
        role.setRole("ACCOUNTANT");
        role.setOperation("GRANT");

        Group group = new Group();
        group.setId(Long.valueOf(3));
        group.setCode("ROLE_ACCOUNTANT");

//        Event event = new Event(
//                new Date(),
//                "GRANT_ROLE",
//                "raywang@acme.com",
//                String.format("Grant role %s to %s", role.getRole(), "johndoe@acme.com"),
//                "/api/admin/user/role");

        // 模擬userService.findByEmailIgnoreCase(user) 回傳 initialUser
        Mockito.when(userService.findByEmailIgnoreCase(role.getUser())).thenReturn(initialUser);

        // 模擬userService.findByCodeIgnoreCase(code) 回傳 group
        Mockito.when(groupService.findByCodeIgnoreCase("ROLE_" + role.getRole())).thenReturn(group);

        // 模擬userService.save(user) 回傳 expectedUser
        //Mockito.when(userService.save(expectedUser)).thenReturn(expectedUser);

        // 模擬eventService.save(event) 回傳 event
        //Mockito.when(eventService.save(event)).thenReturn(event);

        JSONObject jsonObject = new JSONObject(role);

        String requestBody = jsonObject.toString();

        // 模擬呼叫[PUT] "/api/admin/user/role"
        String returnString = mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/user/role")
                        .accept(MediaType.APPLICATION_JSON) // response 設定型別
                        .contentType(MediaType.APPLICATION_JSON) // request 設定型別
                        .content(requestBody)) // body 內容
                .andExpect(status().isOk()) // 預期回應的status code 為200
                .andReturn().getResponse().getContentAsString();

        User actualUser = objectMapper.readValue(returnString, new TypeReference<User>() {
        });

        // [Assert] 預期與實際的資料
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getUserTest() throws Exception {
        // [Arrange] 預期資料
        List<User> expectedUsers = new ArrayList<>();

        User expectedUser = new User();
        expectedUser.setId(1);
        expectedUser.setName("Ray");
        expectedUser.setLastname("Wang");
        expectedUser.setEmail("raywang@acme.com");
        expectedUser.setRoles(List.of("ROLE_ADMINISTRATOR"));

        expectedUsers.add(expectedUser);

        expectedUser = new User();
        expectedUser.setId(2);
        expectedUser.setName("John");
        expectedUser.setLastname("Doe");
        expectedUser.setEmail("johndoe@acme.com");
        expectedUser.setRoles(List.of("ROLE_USER"));

        expectedUsers.add(expectedUser);

        // 定義模擬呼叫userRepository.findAll() 要回傳的預設結果
        Mockito.when(userService.findAll()).thenReturn(expectedUsers);

        // 模擬呼叫[GET]: /api/admin/user
        String returnString = mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/user")
                        .accept(MediaType.APPLICATION_JSON)) // response 設定型別
                .andExpect(status().isOk()) // 預期回應的status code 為200
                .andReturn().getResponse().getContentAsString();

        List<User> actualUsers = objectMapper.readValue(returnString, new TypeReference<List<User>>() {
        });

        // [Assert] 預期與實際的資料
        assertEquals(expectedUsers, actualUsers);
    }
}
