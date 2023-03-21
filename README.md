# Graal Prophet Utils

This repository is a version of the prophet-utils repository, adapted to the GraalVM static analysis of microservice applications project

## Available Scripts

```bash
mvn clean install
mvn package
```

## Running tests
```bash
mvn test
```

## For Setup:
Run microservice-setup.sh. You will need to configure the 'directory' variable to contain the path to train-ticket for your machine. It currently contains all train-ticket microservices, but for the bash script to unzip 
all microservice jars for another project, you must replace the current list with one of all their names.