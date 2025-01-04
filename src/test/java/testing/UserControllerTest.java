package testing;
// LÄUFT 5
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thePowerpuffCards.api.controller.CardController;
import thePowerpuffCards.api.controller.UserController;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.core.models.User;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserControllerTest {

    private UserController userController;
    private CardController cardController;

    @Mock
    private UsersDaoDb usersDao;

    private StringWriter stringWriter;
    private BufferedWriter out;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(usersDao);
        cardController = new CardController(usersDao); // Hier initialisieren
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

    @Test
    void testConfigureDeck_ValidDeck() throws IOException {
        User user = new User("kienboec", "daniel");
        when(usersDao.getText("kienboec")).thenReturn(Optional.of(user));

        // Stack des Benutzers simulieren (Karten als MonsterCard oder SpellCard speichern)
        when(usersDao.getCardsFromStack("kienboec")).thenReturn(List.of(
                new MonsterCard("card1", "FireGoblin", 10.0, ElementType.FIRE, MonsterType.GOBLIN),
                new SpellCard("card2", "WaterSpell", 20.0, ElementType.WATER),
                new MonsterCard("card3", "Dragon", 50.0, ElementType.NORMAL, MonsterType.DRAGON),
                new MonsterCard("card4", "Ork", 40.0, ElementType.NORMAL, MonsterType.ORK)
        ));

        List<String> cardIds = List.of("card1", "card2", "card3", "card4");
        String jsonBody = new ObjectMapper().writeValueAsString(cardIds);

        BufferedWriter mockWriter = mock(BufferedWriter.class);
        cardController.configureDeck(Map.of("Authorization", "Bearer kienboec-mtcgToken"), jsonBody, mockWriter);

        verify(mockWriter).write(contains("200 OK"));
    }


    @Test
    void testConfigureDeck_EmptyDeck() throws IOException {
        User user = new User("kienboec", "daniel");
        when(usersDao.getText("kienboec")).thenReturn(Optional.of(user));

        List<String> cardIds = List.of(); // Kein Deck
        String jsonBody = new ObjectMapper().writeValueAsString(cardIds);

        BufferedWriter mockWriter = mock(BufferedWriter.class);
        cardController.configureDeck(Map.of("Authorization", "Bearer kienboec-mtcgToken"), jsonBody, mockWriter);

        verify(mockWriter).write(contains("400 Bad Request"));
    }

    @Test
    void testConfigureDeck_NotEnoughCards() throws IOException {
        User user = new User("kienboec", "daniel");
        when(usersDao.getText("kienboec")).thenReturn(Optional.of(user));

        List<String> cardIds = List.of("card1", "card2", "card3"); // Nur 3 Karten
        String jsonBody = new ObjectMapper().writeValueAsString(cardIds);

        BufferedWriter mockWriter = mock(BufferedWriter.class);
        cardController.configureDeck(Map.of("Authorization", "Bearer kienboec-mtcgToken"), jsonBody, mockWriter);

        verify(mockWriter).write(contains("400 Bad Request"));
    }


}
