package testing;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thePowerpuffCards.api.controller.CardController;
import thePowerpuffCards.core.models.cards.monster.MonsterCard;
import thePowerpuffCards.core.models.cards.monster.MonsterType;
import thePowerpuffCards.core.models.cards.spell.SpellCard;
import thePowerpuffCards.persistence.dao.UsersDaoDb;
import thePowerpuffCards.core.models.User;
import thePowerpuffCards.core.models.cards.Card;
import thePowerpuffCards.core.models.cards.ElementType;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CardControllerTest {

    private CardController cardController;

    @Mock
    private UsersDaoDb usersDao;

    private StringWriter stringWriter;
    private BufferedWriter out;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        cardController = new CardController(usersDao);
        stringWriter = new StringWriter();
        out = new BufferedWriter(stringWriter);
    }

    @Test
    void testShowAllCards_Success() throws Exception {
        String token = "Bearer testUser-mtcgToken";
        User mockUser = new User("testUser", "testPassword");

        MonsterCard card = new MonsterCard("cardId", "Dragon", 75.0, ElementType.FIRE, MonsterType.DRAGON);

        when(usersDao.getText("testUser")).thenReturn(Optional.of(mockUser));
        when(usersDao.getCardsFromStack("testUser")).thenReturn(List.of(card));

        cardController.handleRequest("GET", "/cards", Map.of("Authorization", token), null, out);

        assertTrue(stringWriter.toString().contains("200 OK"));
        assertTrue(stringWriter.toString().contains("Dragon"));
    }


    @Test // passt
    void testShowAllCards_NoCards() throws Exception {
        String token = "Bearer testUser-mtcgToken";
        User mockUser = new User("testUser", "testPassword");

        when(usersDao.getText("testUser")).thenReturn(Optional.of(mockUser));
        when(usersDao.getCardsFromStack("testUser")).thenReturn(List.of());

        cardController.handleRequest("GET", "/cards", Map.of("Authorization", token), null, out);

        assertTrue(stringWriter.toString().contains("200 OK"));
        assertTrue(stringWriter.toString().contains("No cards available for this user."));
    }

    @Test // passt
    void testShowAllCards_MissingToken() throws Exception {
        cardController.handleRequest("GET", "/cards", Map.of(), null, out);

        assertTrue(stringWriter.toString().contains("401 Unauthorized"));
        assertTrue(stringWriter.toString().contains("Missing or invalid token."));
    }
    @Test
    void testShowAllCards_UserNotFound() throws Exception {
        String token = "Bearer testUser-mtcgToken";

        when(usersDao.getText("testUser")).thenReturn(Optional.empty());

        cardController.handleRequest("GET", "/cards", Map.of("Authorization", token), null, out);

        assertTrue(stringWriter.toString().contains("400 Bad Request"));
        assertTrue(stringWriter.toString().contains("User not found."));
    }


}