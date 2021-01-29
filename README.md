# Network Protocols

A toy project, implemented reliable data transfer network protocol to transfer string to 3 server simultaneously using raw socket

compile:

```
javac -cp rocksaw-1.1.0.jar; *.java
```


run servers:

```
java -cp rocksaw-1.1.0.jar; runServer <Server front end port> <server MD5 port> <Server SHA256 port>
```

example: `java -cp rocksaw-1.1.0.jar; runServer 3200 1600 100`


run client: 

```
java -cp rocksaw-1.1.0.jar; runClient <Server's ip address> <ServerFronend's port> <Message>
```
example: ``java -cp rocksaw-1.1.0.jar; runClient 192.168.100.10 3200 helloWorld!``


Note:
- The rocksaw dll file is 32bits so this need to be compile by java 32bits
- Run command prompt with administrator's permission. 
- Client's fix port is 80, consider not to use the same port for servers if client and servers run in the same machine.
- Time for client to shutdown is a little bit long ( this doesn't happen on ubuntu, it shuts down right away) so please be patient.
