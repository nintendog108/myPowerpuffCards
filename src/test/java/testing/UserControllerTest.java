package testing;
// LÄUFT 2/2
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thePowerpuffCards.api.controller.UserController;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.core.models.User;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Optional;

public class UserControllerTest {

    private UserController userController;

    @Mock
    private UsersDaoDb usersDao;

    private StringWriter stringWriter;
    private BufferedWriter out;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(usersDao);
        stringWriter = new StringWriter();
        out = new BufferedWriter(stringWriter);
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        String requestBody = "{\"Username\":\"testUser\", \"Password\":\"testPassword\"}";
        User newUser = new User("testUser", "testPassword");

        when(usersDao.userExists("testUser")).thenReturn(false);

        userController.handleRequest("POST", "/users", null, requestBody, out);

        verify(usersDao).save(eq(newUser));
        assertTrue(stringWriter.toString().contains("201 Created"));
    }
    @Test
    void testRegisterUser_UserAlreadyExists() throws Exception {
        String requestBody = "{\"Username\":\"testUser\", \"Password\":\"testPassword\"}";

        when(usersDao.userExists("testUser")).thenReturn(true);

        userController.handleRequest("POST", "/users", null, requestBody, out);
        assertTrue(stringWriter.toString().contains("HTTP/1.1 409"));
        assertTrue(stringWriter.toString().contains("Username already exists."));
    }



}
