package com.gluck.gaming.rest;

import javax.naming.OperationNotSupportedException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gluck.gaming.objects.factory.Connect4Factory;
import com.gluck.gaming.rest.exception.mapper.Connect4ExceptionMapper;
import com.gluck.gaming.service.Connect4Service;
import com.gluck.gaming.service.exception.Connect4ServiceValidationException;
import com.gluck.gaming.service.exception.ConsecutiveTurnsNotAllowedException;
import com.gluck.gaming.service.exception.GameDeletionNotAllowedException;
import com.gluck.gaming.service.exception.GameNotFoundException;
import com.gluck.gaming.service.exception.IncorrectGameStartException;
import com.gluck.gaming.service.exception.InvalidGameStateException;
import com.gluck.gaming.service.exception.InvalidGameStateTransitionException;
import com.gluck.gaming.service.exception.InvalidGridCellToFillException;
import com.gluck.gaming.service.model.CreateGameRequest;
import com.gluck.gaming.service.model.PlayTurnRequest;

/**
 * Service class to expose the Connect4 game APIs RESTfully.
 *
 * @author Vinay Semwal
 */
@Path("/games")
public class Connect4RestService {

    private static final Logger logger = LogManager.getLogger(Connect4RestService.class);

    private static final String ID = "id";

    private static Connect4Service connect4Service;

    static {
        connect4Service = Connect4Factory.getConnect4Service();
    }

    /**
     * @param request {@link CreateGameRequest}
     * @return {@link Response}
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGame(final CreateGameRequest request) {
        try {
            logger.info("Received create game request : {}", request);
            return Response.ok(connect4Service.createGame(request)).status(Status.CREATED).build();
        } catch (final Connect4ServiceValidationException e) {
            logger.error("Request to create the game failed with exception : {}", e);
            return Connect4ExceptionMapper.toResponse(e).build();
        }
    }

    /**
     * @param gameId Unique identifier of the game for which data is to be fetched
     * @return {@link Response}
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGameData(@PathParam(value = ID) final String gameId) {
        try {
            logger.info("Received request to fetch data for game with id : {}", gameId);
            return Response.ok(connect4Service.getGameData(gameId)).status(Status.FOUND).build();
        } catch (final GameNotFoundException | Connect4ServiceValidationException e) {
            logger.error("Request to get the game data failed with exception : {}", e);
            return Connect4ExceptionMapper.toResponse(e).build();
        }
    }

    /**
     * @param request {@link PlayTurnRequest}
     * @return {@link Response}
     */
    @PUT
    @Path("/play")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response playTurn(final PlayTurnRequest request) {
        try {
            logger.info("Received request play a turn in game. Request  : {}", request);
            return Response.ok(connect4Service.playTurn(request)).status(Status.ACCEPTED).build();
        } catch (final
            GameNotFoundException
            | Connect4ServiceValidationException
            | InvalidGameStateException
            | ConsecutiveTurnsNotAllowedException
            | InvalidGridCellToFillException
            | IncorrectGameStartException e) {
            logger.error("Request to play turn in the game with id : {} failed with exception : {}", request.getGameId(), e);
            return Connect4ExceptionMapper.toResponse(e).build();
        }
    }

    /**
     * @param gameId unique identifier of the game to be suspended.
     * @return {@link Response}
     */
    @PUT
    @Path("/suspend/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response suspendGame(@PathParam(value = ID) final String gameId) {
        try {
            logger.info("Received request to suspend a game with id : {}", gameId);
            connect4Service.suspendGame(gameId);
            return Response.ok().status(Status.ACCEPTED).build();
        } catch (final GameNotFoundException | Connect4ServiceValidationException | InvalidGameStateTransitionException e) {
            logger.error("Request to suspend the game with id : {} failed with exception : {}", gameId, e);
            return Connect4ExceptionMapper.toResponse(e).build();
        }
    }

    /**
     * @param gameId unique identifier of the game to be resumed.
     * @return session Id of the newly created game session
     */
    @PUT
    @Path("/resume/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resumeGame(@PathParam(value = ID) final String gameId) {
        try {
            logger.info("Received request to resume a game with id : {}", gameId);
            return Response.ok(connect4Service.resumeGame(gameId)).status(Status.ACCEPTED).build();
        } catch (final GameNotFoundException | Connect4ServiceValidationException | InvalidGameStateTransitionException e) {
            logger.error("Request to resume the game with id : {} failed with exception : {}", gameId, e);
            return Connect4ExceptionMapper.toResponse(e).build();
        }
    }

    /**
     * @param gameId unique identifier of the game to be completed
     * @return {@link Response}
     */
    @PUT
    @Path("/complete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeGame(@PathParam(value = ID) final String gameId) {
        try {
            logger.info("Received request to complete a game with id : {}", gameId);
            connect4Service.completeGame(gameId);
            logger.info("Completed the game with id : {}", gameId);
            return Response.ok().status(Status.NO_CONTENT).build();
        } catch (final GameNotFoundException | Connect4ServiceValidationException | InvalidGameStateTransitionException e) {
            logger.error("Request to complete the game with id : {} failed with exception : {}", gameId, e);
            return Connect4ExceptionMapper.toResponse(e).build();
        }
    }

    /**
     * @param gameId unique identifier of the game to draw
     * @return {@link Response}
     */
    @PUT
    @Path("/draw/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response drawGame(@PathParam(value = ID) final String gameId) {
        try {
            logger.info("Received request to draw a game with id : {}", gameId);
            connect4Service.drawGame(gameId);
            logger.info("Drawn the game with id : {}", gameId);
            return Response.ok().status(Status.ACCEPTED).build();
        } catch (final GameNotFoundException | Connect4ServiceValidationException | InvalidGameStateTransitionException e) {
            logger.error("Request to draw the game with id : {} failed with exception : {}", gameId, e);
            return Connect4ExceptionMapper.toResponse(e).build();
        }
    }

    /**
     * @param gameId Unique Identifier of the game to abandon.
     * @return {@link Response}
     */
    @PUT
    @Path("/abandon/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response abandonGame(@PathParam(value = ID) final String gameId) {
        try {
            logger.info("Received request to abandon a game with id : {}", gameId);
            connect4Service.abandonGame(gameId);
            logger.info("Abandoned the game with id : {}", gameId);
            return Response.ok().status(Status.ACCEPTED).build();
        } catch (final GameNotFoundException | Connect4ServiceValidationException | InvalidGameStateTransitionException e) {
            logger.error("Request to abandon the game with id : {} failed with exception : {}", gameId, e);
            return Connect4ExceptionMapper.toResponse(e).build();
        }
    }

    /**
     * API to hard delete the Game from the System.
     *
     * @param gameId Unique Identifier of the game to be deleted.
     * @return {@link Response}
     */
    @DELETE
    @Path("/delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGame(@PathParam(value = ID) final String gameId) {
        try {
            logger.info("Received request to delete a game with id : {}", gameId);
            connect4Service.deleteGame(gameId);
            logger.info("Deleted the game with id : {}", gameId);
            return Response.ok().status(Status.ACCEPTED).build();
        } catch (final
            GameNotFoundException
            | Connect4ServiceValidationException
            | InvalidGameStateTransitionException
            | OperationNotSupportedException
            | GameDeletionNotAllowedException e) {
            logger.error("Request to delete the game with id : {} failed with exception : {}", gameId, e);
            return Connect4ExceptionMapper.toResponse(e).build();
        }
    }

}
