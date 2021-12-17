# history
The database of the history of the voting and its services

To build the application run the command inside the history folder
```
./gradlew build
```
Start the Java application with the following commands:
```
./gradlew startMongoDb
```
to start a MongoDB instance. Then:
```
java -jar build/libs/history-1.0.0-SNAPSHOT.jar --server.port=8280 --spring.profiles.active=dev
```
and open [http://localhost:8280/swagger-ui/index.html](http://localhost:8280/swagger-ui/index.html) in your browser to connect to the vote application.

If you need to start it on a environment production:
```
java -Djavax.net.ssl.trustStore=./application.keystore -Djavax.net.ssl.trustStorePassword=password -jar build/libs/history-1.0.0-SNAPSHOT.jar --server.ssl.key-store=./application.keystore --server.ssl.key-store-password=password --server.ssl.trust-store=./application.keystore --server.ssl.trust-store-password=password --server.port=8643 --spring.profiles.active=prod
```

Add the following DNS in your /etc/hosts file:
```
$IP_ADDRESS  vota-votingpapers.vige.it
$IP_ADDRESS  vota-voting.vige.it

and open [https://vota-history.vige.it:8643/swagger-ui.html](https://vota-history.vige.it:8643/swagger-ui.html) in your browser to connect to the vote application.

## certificates

in a production environment we are using a default certificate but you could move a different ssl certificate and keys. Use this command to generate it:
```
keytool -genkey -alias history -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore ./application.keystore -validity 3650 -dname "CN=vota-history.vige.it, OU=Vige, O=Vige, L=Rome, S=Italy, C=IT" -storepass password -keypass password
```
You need to export the auth certificate and import it through the command:
```
keytool -import -alias auth -file ${exported_auth_certificate}.pem -keystore ./application.keystore -storepass password -keypass password
```
Same thing for the votingpapers certificate:
```
keytool -import -alias votingpapers -file ${exported_votingpapers_certificate}.pem -keystore ./application.keystore -storepass password -keypass password
```
Same thing for the voting certificate:
```
keytool -import -alias voting -file ${exported_voting_certificate}.pem -keystore ./application.keystore -storepass password -keypass password
```

## Eclipse

To make the project as an Eclipse project go in the root folder of the project and run the following command:
```
./gradlew eclipse
```

## Docker

If you need a complete environment you can download docker and import the application through the command:
```
docker pull vige/vota-history
```
To run the image use the command:
```
docker run -d --name vota-history -p8280:8280 vige/vota-history
```
Then open [http://vota-history.vige.it:8280/swagger-ui/index.html](http://vota-history.vige.it:8280/swagger-ui/index.html) to connect to the vote application
