package testing;
// LÄUFT 4
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
import java.io.StringWriter;
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
    void testAcquirePackage_NotEnoughCoins() throws Exception {
        String token = "testUser-mtcgToken";
        User mockUser = new User("testUser", "testPassword");
        mockUser.setCoins(2); // z.b weniger als 5 Münzen

        when(authService.authenticate(anyMap())).thenReturn(true);
        when(usersDao.getText("testUser")).thenReturn(Optional.of(mockUser));

        packageController.acquirePackageController(Map.of("Authorization", "Bearer " + token), out);

        verify(usersDao, never()).update(eq(mockUser), any());
        assertTrue(stringWriter.toString().contains("Not enough money."));
        System.out.println("Output: " + stringWriter.toString());

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




}
