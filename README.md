# SimpleRat
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/brease568/SimpleRat)

SimpleRat is a command-line based client / server paired Java Remote Access Tool (RAT). SimpleRat can be used to enable remote command execution and supports SSL encryption.

## Prerequisites

- Java version 8+
- The ability to run commands from the command line.

## Dependencies

- Apache Commons-CLI v1.4

## Usage - without SSL Encryption

To execute the server and client without SSL encryption\:
- Execute the server and specify a port to listen on.
- Execute the client and specify a target IP address and a port to connect to.

ServerRat.jar
```bash
java -jar ServerRat.jar -p 443
Waiting for a connection..
```


ClientRat.jar
```bash
java -jar ClientRat.jar -t 127.0.0.1 -p 443
simpleRat# 
```

## Usage - with SSL Encryption

To execute the server and client with SSL encryption you must do two things\:
- First, you must generate a Java keystore. In this example, the keystore password is 'password'.
```bash
keytool -genkey -alias signFiles -keystore examplestore
```
- Second, execute the server and client with the '-s' switch option and specify the keystore and password.

ServerRat.jar
```bash
java -jar -Djavax.net.ssl.keyStore=examplestore -Djavax.net.ssl.keyStorePassword=password ServerRat.jar -p 443 -s
[SSL: ServerSocket[addr=0.0.0.0/0.0.0.0,localport=443]]
Waiting for a connection..
```


ClientRat.jar
```bash
java -jar -Djavax.net.ssl.trustStore=examplestore -Djavax.net.ssl.trustStorePassword=password ClientRat.jar -t 127.0.0.1 -p 443 -s 
simpleRat# 
```

Currently, this version does not support specifying the keystore via path, so you must use java switch options as follows:


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change. Currently, there are no contributors.



## License
[MIT](https://choosealicense.com/licenses/mit/)
