## About this Project

This **Snake Eyes** application is a basic representation of the popular
dice rolling game. The gameplay of this application involves rolling two
dices to determine whether you hit **Snake Eyes**, which earns you a 30x
multiplier against your stake; Or a **Pair** (of any outcome), which
earns you a 7x multiplier against your stake. Dices with no matching 
outcomes do not get any prizes other than the satisfaction of pressing
the flashy button.

## Notes about this submission
Since this is a demo application developed with limited time and 
resources, there are processes that are deemed out-of-scope, these 
include (but not limited to):
- New player sign-up process: Players without an ID is considered a new
player in this application. And will be initialised with 1000 units. In 
practice, Sign ups should be handled by a separate micro-service.
- There are no log-in process for existing players: If time is not a
constraint then I would have handled player authentication and 
identification via OAuth tokens, rather than plain-old playerId.
- Permanent persistence: I've chosen to use H2 in-memory database for
demonstration purpose, as it is easier to set up for quick prototyping.

I have, however, taken the liberty of building a simple UI for this game 
even though I believe UI is also out of the scope of this assessment. It
is rather basic with Ajax calls so please don not judge too hard on this! I 
built it mainly because it would be a shame to have only the logic and no
visuals for such classic game.


## Designs/Tools
The following is a list of tools/frameworks that I have used to develop 
this application:
- Spring Boot (Web, JPA)
- Maven
- Lombok
- H2 Database
- Rest-Assured Integration Test suite
- WireMock 
- JUnit / Mockito
- Swagger/OpenAPI
- ThymeLeaf
- Bootstrap for UI

## Flow of this application
1. Once landed on the UI, you will be greeted with an interface which
contains 2 input fields and a button that is disabled to begin with.
2. Once a stake is entered into the first field, the button is enabled
and ready to submit.
3. Player ID is optional, there will be no existing players in the H2
database because it is only stored in-memory, and it is cleared 
everytime application is exited.
4. Once you press 'PLAY', there's no going back! (Not that you'd expect
to). You will see the outcome of the 2 dices and how much you have won/
not won. 
5. Once you have played one game as a new player, you will auto-
matically be assigned a playerId, and as long as this playerId stays
in the field, the application assumes you are playing as that player.
(obviously this should be handled by cookies and tokens in real life).
6. Playing as the same player you will see the history of the player's
gameplay outcome at the bottom (gameplay log). Here you can track the
outcome history and also the history of the player's balance.
7. You may play as new player if you empty the playerId field, and you 
can always go back to player with ID of 1.


## Building the project
1) Clone/Pull via Git or download zip file of this project (and un-zip) 
to a directory;


2) Make sure you have Maven installed on your machine, you can check 
this by inspecting the version of installed Maven via the following
command (using command line tool):

``mvn -version``

3. Using command line tool, navigate to the root directory where this
README.md file and, specifically, the pom.xml file is located;
4. Run the following maven command via the command line:

``mvn clean install``    (Also runs Tests)

or

``mvn clean install -DskipTests``    (Does not run Tests)



## Running the application
You may run the application by running the following maven command via 
the command line:

``mvn spring-boot:run``

## Accessing the simple UI
Once the application is running, you can navigate to the following URL
from your browser of choice for the UI:

``http://localhost:8080/``

## Accessing the API documentation
Once the application is running, you can navigate to the following URL
for the OpenAPI (swagger) documentation:

``http://localhost:8080/swagger-ui.html``

From the SwaggerUI interface you can test the application endpoints
via OpenAPI by clicking "Try it out" buttons for the respective
endpoints.

## Run Tests Only
To run tests only, run the following command on command line:

``mvn verify``


## Building Docker image
A Dockerfile is included in this Spring-Boot application for the
purpose of building a Docker image. In order to perform such action,
Docker must be installed on the machine. Please refer to the Docker
website for instructions on installing Docker the right way.



##Thank you for taking an interest in this project.

