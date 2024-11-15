# Marketplace
&nbsp;&nbsp;&nbsp;&nbsp;A simple marketplace application with client-server architecture. The project allows users to interact with a server through socket communication, where the client can send commands and receive responses.

### Table of Contents
* [Project Overview](#project-overview)
* [Features](#features)
* [Technologies Used](#technologies-used)
* [Folder Structure](#folder-structure)
* [Usage](#usage)
* [Testing](#testing)
* [Code Quality](#code-quality)



### Project Overview
&nbsp;&nbsp;&nbsp;&nbsp;This project implements a client-server application where the client communicates with the server to send commands and receive responses. The communication is handled via Java sockets. The server processes commands and returns appropriate results to the client.

&nbsp;&nbsp;&nbsp;&nbsp;The core functionality includes adding, removing, and listing items, or executing any other server-side functionality that can be implemented based on commands.

### Features
* Client-server architecture with socket communication
* Command-based interactions (e.g., add, remove, list)
* Server processes client commands and sends back responses
* Graceful error handling and disconnection management
* Unit tests for client and server functionality

### Technologies Used
* **Java** 
* **JUnit5** - Testing framework for unit tests
* **Mockito** - Mocking framework for testing server-client interactions
* **Java NIO** - Non-Blocking I/O for client-server communication

### Folder Structure
&nbsp;&nbsp;&nbsp;&nbsp;Here is the structure of the project:

```plaintext
marketplace/
│
├── wish/
│   ├── list/
│       ├── storage/
│           ├── Storage.java
│           ├── InMemotyStorage.java
│       ├── client/
│           ├── Client.java
│       ├── server/
│           ├── Server.java
│       └── command/
│           ├── Command.java
│           ├── CommandCreator.java
│           ├── CommandExecutor.java
│
└── test/
    ├── ServerTest.java
    ├── CommandCreatorTest.java
    ├── CommandExecutorTest.java
    └── InMemoryStorageTest.java
```
- wish/list contains all the core functionality of the project, split into subfolders based on roles (storage, client, server, command).
- test/ contains all the tests related to the different components.

  
### Usage
&nbsp;&nbsp;&nbsp;&nbsp;Once the application is set up, you can interact with it through the command-line client. Here's a simple interaction:

1. **Start the Server:** Run the Server class to start listening for client connections.
2. **Start the Client:** Run the Client class. The client will connect to the server and await commands.
3. **Send Commands:** You can enter commands such as:
    - _buy-item <user> <item_id>_: Buys the item with id <item_id> and marks it as sold.
    - _bid-item <user> <item_id> <price>_: Creates a bid for item with id <item_id> for <price> dollars.
    - _view-bids <item_id>_: Lists all the bids for the item with id <item_id>.
    - _remove-item <user> <item_id>_: Removes item with id <item_id>.
    - _list-item <user> <item_name> <price>_: Adds a new item to the storage. 
    - _list-items_: Lists all items.
4. **Exit the Client:** Type "exit" to terminate the client.
5. **Exit the Server:** The server automatically shuts down after 20 seconds if no clients are connected.

### Testing
&nbsp;&nbsp;&nbsp;&nbsp;Unit tests for the project can be found in the test/ folder. In this project, the business logic (everything apart from the client and server) has been thoroughly tested with 100% code coverage. The tests for the business logic validate the correctness and reliability of key components, such as command handling, storage management, and other core functionalities. These tests ensure that the core operations of the system are functioning as expected. Unfortunately, the client and server components currently have limited test coverage.

### Code quality
&nbsp;&nbsp;&nbsp;&nbsp;This project uses Checkstyle to maintain consistent code style across the codebase.
* The Checkstyle configuration file (checkstyle.xml) is located in the config/ directory.
* You can use Checkstyle with your IDE (e.g., IntelliJ IDEA, Eclipse) or integrate it into your build process (e.g., with Maven or Gradle) to ensure that your code adheres to the required style.

