# Graal Prophet Utils

This repository is a version of the prophet-utils repository, adapted to the GraalVM static analysis of microservice applications project

## Note:
- This program only currently accepts Java microservices
- Program only runs on Windows and Linux machines
- This program depends on cloudhubs/graal repository

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


# TWO VERSIONS

## main branch
1. This is a generalized approach and implementation for all microservice systems
2. Uses Levenshtein distance and approximates the distance for service dependancy linking

## Austin-adding-new-params-for-requests branch
1. This branch is for analysis of train ticket
2. This one is not recommended for generalized approach as it parses patterns specific to train ticket
    * Parses destination microservice from URI and or host name depending on version of train ticket
3. Uses partial signature matching for the URI, body param, and HTTP method

## NOTE: Both approaches parse by body param and HTTP method

## FUTURE IMPROVEMENT RECOMMENDATIONS:
- Partial signature matching for the URI could be implemented for the generalized approach in the main branch instead of approximating with distance
- Partial signature matching is defined as only matching hard coded parts for the URI and ignoring path params
