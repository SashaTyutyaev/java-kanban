package main;

import main.client.KVTaskClient;
import main.exceptions.ManagerSaveException;
import main.server.HttpTaskServer;
import main.server.KVServer;

import java.io.IOException;
import java.net.URI;

public class Main {


    public static void main(String[] args) throws ManagerSaveException, IOException, InterruptedException {
        KVServer kvServer = new KVServer();
        kvServer.stop();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.stop();
        KVTaskClient kvTaskClient = new KVTaskClient(URI.create("http://localhost:8078"));

    }
}
