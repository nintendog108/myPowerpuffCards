package testing;
// LÄUFT 5
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thePowerpuffCards.api.controller.PackageController;
import thePowerpuffCards.persistence.dao.PackageDaoDb;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.persistence.dao.TransactionDaoDb;
import thePowerpuffCards.core.services.AuthService;
import thePowerpuffCards.core.models.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class PackageControllerTest {

    private PackageController packageController;

    @Mock
    private PackageDaoDb packageDao;
    @Mock
    private UsersDaoDb usersDao;
    @Mock
    private AuthService authService;
    @Mock
    private TransactionDaoDb transDao;
    private StringWriter stringWriter;
    private BufferedWriter out;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        packageController = new PackageController(packageDao, usersDao, authService, transDao);
        stringWriter = new StringWriter();
        out = new BufferedWriter(stringWriter);
    }

    @Test
    void testAcquirePackage_ValidUser_Success() throws Exception {
        String token = "testUser-mtcgToken";
        User mockUser = new User("testUser", "testPassword");
        mockUser.setCoins(10);
        when(authService.authenticate(anyMap())).thenReturn(true);
        when(usersDao.getText("testUser")).thenReturn(Optional.of(mockUser));
        when(packageDao.acquirePackage()).thenReturn(new thePowerpuffCards.core.models.cards.Package());

        // perform
        packageController.acquirePackageController(Map.of("Authorization", "Bearer " + token), out);

        // verify and assert
        verify(usersDao).update(eq(mockUser), any());
        assertNotNull(out.toString());
    }


    @Test
    void testAcquirePackage_Unauthorized() throws Exception {
        when(authService.authenticate(anyMap())).thenReturn(false);

        packageController.acquirePackageController(Map.of("Authorization", "Bearer invalidToken"), out);

        assertTrue(stringWriter.toString().contains("Unauthorized request."));
    }
    @Test
    void testAcquirePackage_UserNotFound() throws Exception {
        String token = "testUser-mtcgToken";

        when(authService.authenticate(anyMap())).thenReturn(true);
        when(usersDao.getText("testUser")).thenReturn(Optional.empty());

        packageController.acquirePackageController(Map.of("Authorization", "Bearer " + token), out);

        assertTrue(stringWriter.toString().contains("User not found."));
    }


    @Test
    void testAcquirePackage_NotEnoughMoney() throws IOException {
        User user = new User("kienboec", "daniel");
        user.setCoins(2); // Zu wenig Münzen
        when(usersDao.getText("kienboec")).thenReturn(Optional.of(user));

        BufferedWriter mockWriter = mock(BufferedWriter.class);
        packageController.acquirePackageController(Map.of("Authorization", "Bearer kienboec-mtcgToken"), mockWriter);

        verify(mockWriter).write(contains("401 Unauthorized"));
    }

    @Test
    void testAcquirePackage_NoPackagesAvailable() throws IOException, SQLException {
        User user = new User("kienboec", "daniel");
        user.setCoins(10); // Genug Münzen
        when(usersDao.getText("kienboec")).thenReturn(Optional.of(user));
        when(packageDao.acquirePackage()).thenReturn(null); // Keine Pakete mehr

        BufferedWriter mockWriter = mock(BufferedWriter.class);
        packageController.acquirePackageController(Map.of("Authorization", "Bearer kienboec-mtcgToken"), mockWriter);

        verify(mockWriter).write(contains("401")); // Erwarteter HTTP-Code
    }


}
