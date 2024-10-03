# TCP and UDP Key-Value Store Client

This project implements a simple TCP and UDP Key-Value Store Client in Java. The client communicates with a TCP/UDP Key-Value Store server, allowing users to perform basic operations such as adding, retrieving, and deleting key-value pairs.

## Features

- **TCP and UDP Communication**: Supports both TCP and UDP protocols for communication with the respective server.
- **Add a key-value pair**: Store data in the key-value store.
- **Retrieve a value by key**: Fetch data using its associated key.
- **Delete a key-value pair**: Remove data from the store.
- **Custom logging**: Logs all operations with timestamps and log levels.
- **Logging to file and console**: Supports logging output to both a console and a file.

## Requirements

- Java 8 or higher
- A TCP/UDP Key-Value Store server running

## Setup Instructions

1. **Clone the repository:**

   ```bash
   git clone https://github.com/DiyaWadhwani/KeyValueStoreServer.git
   ```

2. **Compile the repository:**

   ```bash
   javac *.java
   ```

3. **Run the server:**

   You can run the server with the following command:

   ```bash
   java <FilenameServer> <port>
   ```

   Example for TCP:

   ```bash
   java TCPKeyValueStoreServer 5001
   ```

   Example for UDP:

   ```bash
   java UDPKeyValueStoreServer 4000
   ```

4. **Run the client:**

   You can run the client with the following command:

   ```bash
   java <FilenameClient> <host> <port>
   ```

   Example for TCP:

   ```bash
   java TCPKeyValueStoreClient localhost 5001
   ```

   Example for UDP:

   ```bash
   java UDPKeyValueStoreClient localhost 4000
   ```

## User Operation Requirements

- The user must perform at least **5 put**, **5 get**, and **5 delete** operations before they can exit the program.
- After completing the required operations, the user can `exit` to close the application.

## Usage

Upon running the client, you will be prompted with options:

1. **Add a key-value pair**: (enter choice as 1): You can enter a key and a corresponding value to store in the Key-Value Store.
2. **Get a value by key**: (enter choice as 2): Provide a key to retrieve its associated value from the store.
3. **Delete a key-value pair**: (enter choice as 3): Specify a key to remove it and its associated value from the store.
4. **Exit**: (enter choice as 4): You can terminate the application.

### Logging

- All user inputs and results will be logged according to the logging configuration.
- Logs include timestamps, log levels, and detailed messages for each operation performed.

### Example Interaction

Here's an example of how the interaction with the client might look:

What would you like to do?

1. Add a key-value pair
2. Get a value by key
3. Delete a key-value pair
4. Exit

Enter your choice:

The user will follow the prompts to perform the desired operations, ensuring they complete the required number of operations before exiting.
