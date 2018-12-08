package ApplicationTWO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import javax.swing.*;

public class Controller {

    private int type;
    private String path;
    private int counter = 0;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea statusOutput;

    @FXML
    private ProgressBar formattingProgress;

    @FXML
    void initialize() {

        try {
            connectToSocketAndSetupProgram();
            openSocketAndSendFilesLocation();
        } catch (Exception e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ERROR: Start the Application ONE first.");
            System.exit(0);
        }

    }

    private void openSocketAndSendFilesLocation() throws Exception {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4568);
        } catch (IOException e) {
            statusOutput.appendText("ERROR: Could not listen on port: 4568");
//            System.exit(-1);
            return;
        }

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            statusOutput.appendText("ERROR: Accept socket failed: 4568");
//            System.exit(-1);
            return;
        }

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine;
        out.println("Successfully connected to application.");

        Thread copyingProgram = new Thread(() -> {
                new File("LABDIR").mkdir();
                new CopyFiles().runProgram(new File(path), type, statusOutput, formattingProgress, out);
        });
        copyingProgram.start();

        while ((inputLine = in.readLine()) != null) {
            if (inputLine.equals("quit")) {
                out.println(inputLine);
                break;
            }
        }

        out.close();
        in.close();
        serverSocket.close();
        clientSocket.close();
    }

    private void connectToSocketAndSetupProgram() throws Exception {
        Socket socket = new Socket("localhost", 4567);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String fromServer;
        String fromUser;

        while ((fromServer = in.readLine()) != null) {
            switch (counter) {
                case 1:
                    type = Integer.parseInt(fromServer);
                    break;
                case 2:
                    path = fromServer;
                    break;
            }
            if (fromServer.equals("quit")) {
                break;
            }

            fromUser = getNextValueFromSocket();
            out.println(fromUser);
        }

        out.close();
        in.close();
        socket.close();
    }

    private String getNextValueFromSocket() {
        switch (counter) {
            case 0:
                counter++;
                return "type";
            case 1:
                counter++;
                return "path";
            default:
                counter = 0;
                return "quit";
        }
    }

}
