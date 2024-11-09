# Marketplace
A simple marketplace application with client-server architecture. The project allows users to interact with a server through socket communication, where the client can send commands and receive responses.

### Table of Contents
* [Project Overview](#project-overview)
* [Features](#features)
* [Technologies Used](#technologies-used)
* [Folder Structure](#folder-structure)
* [Usage](#usage)
* [Testing](#testing)



### Project Overview
This project implements a client-server application where the client communicates with the server to send commands and receive responses. The communication is handled via Java sockets. The server processes commands and returns appropriate results to the client.

The core functionality includes adding, removing, and listing items, or executing any other server-side functionality that can be implemented based on commands.

### Features
* Client-server architecture with socket communication
* Command-based interactions (e.g., add, remove, list)
* Server processes client commands and sends back responses
* Graceful error handling and disconnection management
* Unit tests for client and server functionality

### Technologies Used
* Java 
* JUnit5 - Testing framework for unit tests
* Mockito - Mocking framework for testing server-client interactions
* SocketChannel - Java NIO for client-server communication

### Folder Structure
Here is the structure of the project:

```plaintext
marketplace/
│
├── wish-list/
│   ├── storage/      # Contains storage management classes
│   ├── client/       # Contains client-side logic for connecting to the server
│   ├── server/       # Contains server-side logic for managing connections
│   └── command/      # Contains classes for handling commands
│
└── tests/
    ├── clienttests/  # Contains tests for the client-side logic
    ├── servertests/  # Contains tests for the server-side logic
    ├── commandtests/ # Contains tests for command-related logic
    └── storagetests/ # Contains tests for storage-related functionality
'''

### Usage
Once the application is set up, you can interact with it through the command-line client. Here's a simple interaction:

Start the Server: Run the Server class to start listening for client connections.
Start the Client: Run the Client class. The client will connect to the server and await commands.
Send Commands: You can enter commands such as:
add <item_name>: Adds an item.
remove <item_name>: Removes an item.
list: Lists all items.
Exit the Client: Type exit to terminate the client.

### Testing
Unit tests for the project can be found in the tests/ folder. The tests verify the functionality of both the client and server, ensuring commands are processed correctly and responses are sent and received.
