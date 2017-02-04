# connect4_game
test exercise for connect4 game

Table of Contents

i.	Platform versions for app development and testing

ii.	Application overview

iii.	Configurable elements

iv.	REST API 

v.	Response Codes for failure scenarios




i. Platform versions for app development and testing:

    JDK version: 1.8.111

    Jetty version: 9.4.1

    Mongo DB version: 3.4.1

    Configuration file : config.properties


ii. Application overview:

    The application is designed to take care of creating a connect4 game with a session, playing turns in the game and validating the
    
    turn, suspending a game thus invalidating the session, resume the game and create a fresh session if it was suspended earlier. The
    
    game can be marked completed if there is a winner, marked drawn if there is no winner, marked abandoned if the players decide to
    
    leave the game and delete(hard delete) the game if it is in a terminal state. The terminal states are the states of the game from
    
    which the game cannot be resumed to be played again. The project has the following modules:

    1. Connect4 logging : Responsible for enabling logging in the project.

    2. Connect4 DB Connection Manager : Responsible for managing the  connections to the underlying database and performing the low            level CRUD operations for the game.

    3. Connect4 Interfaces : Responsible for defining the interfaces that provide API programming to the service and DAO layer.

    4. Connect 4 domain : API implementation of the domain interface that abstract out the low level details of Mongo DB java driver            implementation.

    5. Connect 4 Service library : API implementation of the service interface that is exposed RESTfully in the current connect 4              project implementation.

    6. Connect 4 Rest Application : Module responsible for exposing the game operations RESTfully. The module includes all the other            modules and is WAR packaged. The war can be deployed to jetty/tomcat to expose the services.

    The REST APIs consume and produce JSON Media type.


iii. Configurable elements:


    The config.properties file should be updated to provide the database name, database host and port values to be used by the               application to connect to the underlying mongo DB instance

    The lo4j2.xml file contains the details on the log files created for the application and should be updated to change the defaults.       By default the logs are generated in /tmp directory.

    The project has been developed to be deployable as a web application to an external container instance(Jetty as been used to test       the application).


iv. REST API:

    Rest Call details are documented below: 

1. API to create Game: 

        http://localhost:8080/connect4/games/create/
	
        Http Method: POST

         body: 	{
				"firstPlayerName": “Jack”,
				"secondPlayerName" : “Jill”
		}
2. API to get game data: 

        http://localhost:8080/connect4/games/588e2a6dee15e421ee665345
	
        Http method: GET
	
3. API to play turn: 

         http://localhost:8080/connect4/games/play
	 
         Http method: PUT
	 
         body:	 {
				"sessionId" : "a26287b4-cec5-4f98-bd23-2e7f35a700b7",
				
        			"playerName" : "Jack",
					
       				"gameId" : "588f8fd9ee15e4304b647126",
				
        			"gridColumnToFill" : "1",
					
        			"gridRowToFill" : "5"
		    }
		    
4. API to suspend a game: 

       http://localhost:8080/connect4/games/suspend/588f8fd9ee15e4304b647126
       
       Http method: PUT
       
5. API to resume a game: 

      http://localhost:8080/connect4/games/resume/588f8fd9ee15e4304b647126
      
      Http method: PUT
      
6. API to complete a game:

       http://localhost:8080/connect4/games/complete/588f8fd9ee15e4304b647126
       
       Http method: PUT
       
7. API to draw a game:

       http://localhost:8080/connect4/games/draw/588f8fd9ee15e4304b647126
       
       Http method: PUT
       
8. API to abandon a game:
        http://localhost:8080/connect4/games/abandon/588f8fd9ee15e4304b647126
	
        Http method: PUT
	
9. API to delete a game:

        http://localhost:8080/connect4/games/delete/588f8fd9ee15e4304b647126
	
        Http method: DELETE
	

v.  Response codes for various Failure scenarios are: 

    28001 : Indicates a validation failure in the incoming request.

    28002 : Indicates consecutive turns tried to be played by same player.

    28003 : Indicates game with given details is not present in the system.

    28004 : Indicates game is in an invalid state to perform the operation.
    
    28005 : Indicates that the action cannot be performed since game transition will be invalid.

    28006 : Indicates that an incorrect grid cell was tried to be filled.

    28007 : Indicates that first turn was tried to be played by second player.

    28008 : Indicates that game deletion was tried for a game that is not in a terminal state.

    28099 : Indicates that an internal error occurred in the system.



















