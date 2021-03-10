make clean && make
java -Djava.security.policy=java.policy -cp ../Server/RMIInterface.jar:. Client.ClientUnitTest
