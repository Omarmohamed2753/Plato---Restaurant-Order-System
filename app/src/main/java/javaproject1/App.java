package javaproject1;

public class App {
    
public static void main(String[] args) {
    Thread clientThread = new Thread(() -> {
        // Client operations
    }, "ClientThread");
    clientThread.start();

    Thread adminThread = new Thread(() -> {
        // Admin operations
    }, "AdminThread");
    adminThread.start();
}

}
