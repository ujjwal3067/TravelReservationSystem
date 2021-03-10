# Overview

## **RMI**

On top of the RMI skeleton provided by the starter code, we make use of a `middleware` server to communicate with multiple clients. We created three servers that can run concurrently to manage requests for `Flights`, `Cars`, and `Rooms`. Each server can be accessed by multiple clients concurrently.

## **TCP Sockets**

In addition to RMI, we created a new Travel Reservation system that serves the same purpose but uses TCP sockets for client-server communication. We also implemented `Middleware` servers for this application.

# Design details

## **RMI**

For each resource, we create an individual server for `Room`, `Car`, and `Flight`. The client is able to establish connection to the `Middleware` and then the `Middleware` can distribute the resource to each individual server. The `Middleware` connects to a RMI registry that does the lookup on which server it should distribute the communication into.

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/19c6c2a9-d17b-4779-9886-a5ec02cfa02a/Untitled.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/19c6c2a9-d17b-4779-9886-a5ec02cfa02a/Untitled.png)

## **TCP**

### **Server:**

We use the **`ServerSocket`** class to implement a server program **`TCPResourceManager`**. Inside the **`TCPResourceManager`**, we have the following steps:

1. **Establish TCP Communication via Sockets:**
    1. Create a server socket and bind it to a specific port number.
    2. Once started, the server socket listens for a connection from the client and accepts it.
    3. The server reads data from the client via an **`InputStream`** obtained from the client socket. All data from the client are sent in the form of **`Strings`**.
    4. The server sends data to the client via the client socket’s **`OutputStream`**.
2. **Handle multiple threads by implementing** **Runnable**
    1. Inside the **`TCPResourceManager`**, we create an inner class **`ClientHandler`** that implements **Runnable**.
    2. Instantiate **`Thread`** class and pass the implementer **`ClientHandler`** to the **`Thread`.** The **`Thread`** has a constructor that accepts the **`Runnable`** instance **`ClientHandler`.**
    3. Rewrite the **`run()`** method to make it an entry point of each thread. Client commands are executed inside the **`run()`** method concurrently.
    4. Invoke **`startSERVER()`** of **`Thread`** instance. It creates a new thread which executes the command passed into the **`run()`** method. Once the **startSERVER()** method is invoked, it keeps listening for the connection from the client until an exit message is passed.
3. **Error Handling**
    1. We use this constructor for creating **`ServerSocket`.** For such server socket object, we need to take care of the following exceptions:

    ![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/d2b4b5ae-fc18-4f9e-aa81-a70d142370d8/Untitled.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/d2b4b5ae-fc18-4f9e-aa81-a70d142370d8/Untitled.png)

    - **IOException**: if an I/O error occurs when creating the socket.

    - **UnknownHostException**: if the IP address of the host could not be determined.

        2. When executing commands from the console, there can be many possibilities for an error to   occur, such as **`IllegalArgumentException`** or network error. Once the server detects an error, it sends a response back to the client stating the error message by using the **`getMessage()`** method.

The UML Diagram for the TCP server looks like the following:

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/7c59d9cf-71bb-4f34-b603-1a2e76e3d9e5/Untitled.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/7c59d9cf-71bb-4f34-b603-1a2e76e3d9e5/Untitled.png)

### **Client:**

We use the **`TCPClient`** class to represents a socket client that extends the abstract class **`Client`**. Inside the **`TCPClient`**, we have the following steps:

1. **Establish TCP Communication via Sockets:**
    1. The client initiates connection to a server specified by hostname/IP address and port number.
    2. The client sends data to the server using an **`OutputStream`**
    3. The client reads data from the server using an **`InputStream`**
    4. The constructor for the client socket is

    ![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/de86762a-dc88-4d9c-9b05-cd1fb450a53b/Untitled.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/de86762a-dc88-4d9c-9b05-cd1fb450a53b/Untitled.png)

2. **Error Handling:** the potential errors from clients are largely similar to those from servers, with the additional check on the number/type arguments from the command prompt. We heavily use **`IllegalArgumentException`** on client side.
3. UML diagram for TCP client:

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a1f00fac-9645-4083-bf81-5c9a2fc3e7e4/Untitled.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a1f00fac-9645-4083-bf81-5c9a2fc3e7e4/Untitled.png)