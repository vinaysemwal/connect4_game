package com.gluck.gaming.service.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gluck.gaming.service.exception.Connect4ServiceValidationException;
import com.gluck.gaming.service.model.CreateGameRequest;
import com.gluck.gaming.service.model.PlayTurnRequest;
import com.gluck.gaming.service.validator.Connect4ServiceValidator;

/**
 * Test class for {@link Connect4ServiceValidator}
 *
 * @author Vinay Semwal
 */
public class Connect4ServiceValidatorTest {

    private static Connect4ServiceValidator validator = null;

    /**
     * set up test data.
     */
    @BeforeClass
    public static void setUp() {
        validator = new Connect4ServiceValidator();
    }

    /**
     * Negative test scenarios for createGame API validation
     */
    @Test
    public void testCreateGameValidations() {
        //Empty CreatGameRequest
        try {
            validator.validate(new CreateGameRequest());
            fail("Validation expected to fail for null request. ");
        } catch (final Connect4ServiceValidationException ex) {
            assertEquals("Player names to create the game must not be null or empty", ex.getMessage());
        }
    }

    /**
     * Validations failure scenarios for PlayTurnRequest.
     */
    @Test
    public void testPlayTurnValidations() {
        //Empty PlayTurnRequest
        try {
            validator.validate(new PlayTurnRequest());
            fail("Validation expected to fail for empty request. ");
        } catch (final Connect4ServiceValidationException ex) {
            assertNotNull("Expected error messages to be present for empty request validation failure.", ex.getErrors());
            assertEquals("Expectet 4 error messages to be present for empty request validation failure.", 4, ex.getErrors().size());
            assertTrue(ex.getErrors().contains("Session id is mandatory to play turn."));
            assertTrue(ex.getErrors().contains("Player name cannot be null or empty."));
            assertTrue(ex.getErrors().contains("Invalid grid cell coordinates provided to fill."));
            assertTrue(ex.getErrors().contains("Game id is mandatory, cannot be null or empty."));
        }
        //Invalid gameId in  PlayTurnRequest
        try {
            validator.validate(new PlayTurnRequest("invalidGameId", "sessionId", "playerName", 1, 2));
            fail("Validation expected to fail for invalid game id in request. ");
        } catch (final Connect4ServiceValidationException ex) {
            assertNotNull("Expected error messages to be present for request validation failure.", ex.getErrors());
            assertEquals("Expectet 1 error message(s) to be present for request validation failure due to invalid game id.", 1, ex.getErrors().size());
            assertEquals("Invalid game Id provided.", ex.getErrors().get(0));
        }
    }

    /**
     * Happy path test for CreateGameRequest Validation.
     */
    @Test
    public void testCreateGameValidationsHappyPath() {
        //Empty CreatGameRequest
        try {
            validator.validate(new CreateGameRequest("p1", "p2"));
        } catch (final Connect4ServiceValidationException ex) {
            fail("Expected succcessful validation for correct CreateGameRequest instance.");
        }
    }

    /**
     * Validation for null game Id.
     */
    @Test
    public void testValidateNullGameId() {
        try {
            validator.valicateGameId(null);
            fail("Null game Id must fail validation");
        } catch (final Connect4ServiceValidationException e) {
            assertEquals("Game id is mandatory, cannot be null or empty.", e.getMessage());
        }
    }

    /**
     * Validation for empty game Id.
     */
    @Test
    public void testValidateEmptyGameId() {
        try {
            validator.valicateGameId("");
            fail("Empty game Id must fail validation");
        } catch (final Connect4ServiceValidationException e) {
            assertEquals("Game id is mandatory, cannot be null or empty.", e.getMessage());
        }
    }

    /**
     * Validation for invalid game Id.
     */
    @Test
    public void testValidateInvalidGameId() {
        try {
            validator.valicateGameId("invalidId");
            fail("Invalid game Id must fail validation");
        } catch (final Connect4ServiceValidationException e) {
            assertEquals("Invalid game Id provided.", e.getMessage());
        }
    }

}
