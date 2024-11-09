# Marketplace
A simple marketplace application with client-server architecture. The project allows users to interact with a server through socket communication, where the client can send commands and receive responses.

### Table of Contents
* [Project Overview](#project-overview)
* [Features](#features)
* [Technologies Used](#technologies-used)
* Folder Structure
* [Usage](#usage)
* [Testing](#testing)



### Project Overview
This project implements a client-server application where the client communicates with the server to send commands and receive responses. The communication is handled via Java sockets. The server processes commands and returns appropriate results to the client.

The core functionality includes adding, removing, and listing items, or executing any other server-side functionality that can be implemented based on commands.

### Features
Client-server architecture with socket communication
Command-based interactions (e.g., add, remove, list)
Server processes client commands and sends back responses
Graceful error handling and disconnection management
Unit tests for client and server functionality

### Technologies Used
Java - Programming language used for both the client and server
JUnit - Testing framework for unit tests
Mockito - Mocking framework for testing server-client interactions
SocketChannel - Java NIO for client-server communication

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
