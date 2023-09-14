# Kafka Message processing application

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup and run](#setup-and-run)
* [Features](#features)
* [Future plans](#future-plans)
* [Contact](#contact)
* [References](#references)

## General info 
This project is a Kafka multi-threaded message processing application. 
It is a messaging application that allows users to send and retrieve messages.

## Technologies
* Java 17
* Spring Boot 3
* Kafka
* GraphQL
* Maven
* Spring Data JPA, Hibernate
* Spring Security
* Spring Actuator

## Setup and run
1. To setup this project, you should configure `application.yml` file in the `resources` folder.

2. Also, set up the following properties:
    ```
    CONSUMER_COMMIT_INTERVAL_MILLISECONDS=<commit-intervals-time-in-milliseconds>;
    CONSUMER_POLL_TIMEOUT_MILLISECONDS=<polling-timeout-interval-in-milliseconds>;
    CONSUMER_THREAD_POOL_THREADS_AMOUNT=<thread-pool-threads-amount>;
    DATABASE_URL=jdbc:postgresql://localhost:5432/<database-name>;
    DATABASE_USERNAME=<database-access-username>;
    DATABASE_PASSWORD=<database-user-password>;
    JWT_EXPIRATION_MILLISECONDS=<jwt-token-expiration-in-milliseconds>;
    JWT_SECRET=<jwt-token-secret>;
    KAFKA_CONSUMER_GROUP_ID=<messaging-group>;
    KAFKA_TOPIC_NAME=<topic-name-in-kafka-broker>;
    ```
3. Run zookeeper and kafka broker ([click](https://kafka.apache.org/quickstart)).
4. Create a kafka topic via command in terminal (in the kafka directory):
    ```
    (Windows) .\bin\kafka-topics.bat --create --topic <topic-name> --bootstrap-server localhost:9092
    
    (macOS) bin/kafka-topics.sh --create --topic <topic-name> --bootstrap-server localhost:9092
    ```
5. Run the application with required variables.

## Features
 - Sending and retrieving messages
 - Processing messages in kafka topic
 - Login and registration
 - Mini admin panel
 - Auth via JWT tokens
 - Ability to retrieve only necessary information using GraphQL

## Future plans
    - Switch serialization/deserialization to Avro
    - Work on Custom Exception Handling
    - Add more features to admin panel
    - Add paging and sorting
    - Improve security
    - Improve logging
    - Add more tests
## Contact
Created by [@yeldos-manap](
https://www.linkedin.com/in/yeldos-manap
) - feel free to contact me on LinkedIn!

## References
* [Spring Kafka Multi Threaded Processing](https://www.confluent.io/blog/kafka-consumer-multi-threaded-messaging/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/3.0.10/reference/htmlsingle/#messaging.kafka)