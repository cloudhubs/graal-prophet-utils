# Graal Prophet Utils

This repository is a version of the prophet-utils repository, adapted to the GraalVM static analysis of microservice applications project

## Note:
- This program only currently accepts Java microservices
- Program only runs on Windows and Linux machines

## Setup:
1. Install or build your every microservice
2. Run microservice-setup.sh
    - You will need to configure the 'directory' variable to contain the path to your system for your machine. It currently contains all train-ticket microservices, but for the bash script to unzip all microservice jars for another project, you must replace the current list with one of all their names.
    - This script unzips all of the project jars to expose the BOOT-INF directory. It also deletes the SnakeYAML jar from the projects target if it exists.

3. ProphetUtils accepts one argument: the path to the microservices JSON. The format of the microservices JSON is as follows
    - ```
        {
            "systemName": "systemnameHere",
            "microservices":[
                {
                "baseDir": "/home/person/work/microservice_system/microservice_root/",
                "basePackage": "edu.baylor.ecs.cms",
                "microserviceName": "cms"
            },
            {
                "baseDir": "/home/person/work/microservice_system/microservice_root/",
                "basePackage": "edu.baylor.ems",
                "microserviceName": "ems"
            },
            .
            .
            . etc
            ]
        }
        ```


