package org.saigon.striker.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.saigon.striker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO switch to reactive controller
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@WithMockUser
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void getUser() throws Exception {
        mockMvc.perform(get("/admin/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("user.username", is("user")))
                .andExpect(jsonPath("user.password", is("password")));
    }
}
