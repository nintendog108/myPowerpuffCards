package testing;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
// LÄUFT 5/6 , createTrade Success nicht.
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thePowerpuffCards.api.controller.TradeController;
import thePowerpuffCards.persistence.dao.TradeDaoDb;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Map;

public class TradeControllerTest {
    private TradeController tradeController;

    @Mock
    private TradeDaoDb tradeDao;

    private BufferedWriter out;
    private StringWriter stringWriter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        tradeController = new TradeController(tradeDao);
        stringWriter = new StringWriter();
        out = new BufferedWriter(stringWriter);
    }

    @Test
    void testCreateTrade_Success() throws Exception {
        String token = "Bearer testUser-mtcgToken";
        String requestBody = "{\"tradeId\":\"trade123\", \"offeredCardId\":\"card123\", \"requiredType\":\"SpellCard\", \"minDamage\":50}";

        doNothing().when(tradeDao).createTrade(any());

        tradeController.handleRequest("POST", "/tradings", Map.of("Authorization", token), requestBody, out);

        System.out.println("Test Output: " + stringWriter.toString()); // Debugging
        assertTrue(stringWriter.toString().contains("Trade created successfully"));
    }


    @Test
    void testCreateTrade_Unauthorized() throws Exception {
        String requestBody = "{\"tradeId\":\"trade123\", \"offeredCardId\":\"card123\", \"requiredType\":\"SpellCard\", \"minDamage\":50}";

        tradeController.handleRequest("POST", "/tradings", Map.of(), requestBody, out);

        assertTrue(stringWriter.toString().contains("Invalid token."));
    }

    @Test
    void testDeleteTrade_Success() throws Exception {
        String token = "Bearer testUser-mtcgToken";

        when(tradeDao.deleteTrade("trade123", "testUser")).thenReturn(true);

        tradeController.handleRequest("DELETE", "/tradings/trade123", Map.of("Authorization", token), null, out);

        assertTrue(stringWriter.toString().contains("Trade deleted successfully."));
    }

    @Test
    void testDeleteTrade_NotFound() throws Exception {
        String token = "Bearer testUser-mtcgToken";

        when(tradeDao.deleteTrade("trade123", "testUser")).thenReturn(false);

        tradeController.handleRequest("DELETE", "/tradings/trade123", Map.of("Authorization", token), null, out);

        assertTrue(stringWriter.toString().contains("Trade not found or you are not authorized to delete it."));
    }

    @Test
    void testAcceptTrade_Success() throws Exception {
        String token = "Bearer testUser-mtcgToken";
        String body = "\"card456\"";

        doNothing().when(tradeDao).acceptTrade("trade123", "testUser", "card456");

        tradeController.handleRequest("POST", "/tradings/trade123", Map.of("Authorization", token), body, out);

        assertTrue(stringWriter.toString().contains("Trade accepted successfully."));
    }

    @Test
    void testAcceptTrade_InvalidCard() throws Exception {
        String token = "Bearer testUser-mtcgToken";
        String body = "\"invalidCard\"";

        doThrow(new IllegalArgumentException("Card does not meet trade requirements."))
                .when(tradeDao).acceptTrade("trade123", "testUser", "invalidCard");

        tradeController.handleRequest("POST", "/tradings/trade123", Map.of("Authorization", token), body, out);

        assertTrue(stringWriter.toString().contains("Card does not meet trade requirements."));
    }
}
