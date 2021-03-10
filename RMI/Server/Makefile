# all: java.policy compile-server

# java.policy: FORCE
# 	@echo "Creating server java policy"
# 	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
# 	@echo "permission java.security.AllPermission;" >> java.policy
# 	@echo "};" >> java.policy

# FORCE: ;

# compile-server: Server/RMI/*.java Server/Interface/IResourceManager.java Server/Common/*.java Server/Interface/*.java
# 	javac Server/RMI/*.java Server/Interface/IResourceManager.java Server/Common/*.java Server/Interface/*.java

# clean:
# 	rm -f Server/Interface/*.class Server/Common/*.class Server/RMI/*.class Server/LockManager/*.class Server/Middleware/*.class
# 	rm -f java.policy 

all: java.policy compile-server

java.policy: FORCE
	@echo "Creating server java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

RMIInterface.jar: Server/Interface/IResourceManager.java
	@echo "Compiling RMI server interface"
	javac Server/Interface/IResourceManager.java
	jar cvf RMIInterface.jar Server/Interface/*.class

compile-server: Server/RMI/*.java Server/Interface/*.java Server/Common/*.java Server/LockManager/*.java Server/Middleware/*.java 
	javac Server/RMI/*.java Server/Interface/*.java Server/Common/*.java Server/LockManager/*.java Server/Middleware/*.java 

clean:
	rm -f Server/Interface/*.class Server/Common/*.class Server/RMI/*.class Server/TCP/*.class
	rm -f RMIInterface.jar
	rm -f java.policy 