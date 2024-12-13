package testing;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thePowerpuffCards.api.controller.SessionController;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.core.models.User;

import java.io.BufferedWriter;
import java.io.StringWriter;

public class SessionControllerTest {

    private SessionController sessionController;

    @Mock
    private UsersDaoDb usersDao;

    private StringWriter stringWriter;
    private BufferedWriter out;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        sessionController = new SessionController(usersDao);
        stringWriter = new StringWriter();
        out = new BufferedWriter(stringWriter);
    }

    @Test
    void testCreateSession_Success() throws Exception {
        String requestBody = "{\"Username\":\"testUser\", \"Password\":\"testPassword\"}";
        User mockUser = new User("testUser", "testPassword");
        mockUser.setToken("testUser-mtcgToken");

        when(usersDao.findUserByUsernameAndPassword("testUser", "testPassword")).thenReturn(mockUser);

        sessionController.handleRequest("POST", "/sessions", null, requestBody, out);

        verify(usersDao).addSession(eq(mockUser));
        assertTrue(stringWriter.toString().contains("200 OK"));
        assertTrue(stringWriter.toString().contains("testUser-mtcgToken"));
    }
    @Test
    void testCreateSession_InvalidCredentials() throws Exception {
        String requestBody = "{\"Username\":\"testUser\", \"Password\":\"wrongPassword\"}";

        when(usersDao.findUserByUsernameAndPassword("testUser", "wrongPassword")).thenReturn(null);

        sessionController.handleRequest("POST", "/sessions", null, requestBody, out);

        assertTrue(stringWriter.toString().contains("401 Unauthorized"));
        assertTrue(stringWriter.toString().contains("Invalid credentials"));
    }
    @Test
    void testCreateSession_MissingData() throws Exception {
        String requestBody = "{}"; // Kein Benutzername und Passwort

        sessionController.handleRequest("POST", "/sessions", null, requestBody, out);

        assertTrue(stringWriter.toString().contains("401 Unauthorized"));
        assertTrue(stringWriter.toString().contains("Invalid credentials"));
    }

}
