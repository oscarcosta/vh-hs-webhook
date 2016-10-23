# Spring Webhook Service

This project implements a webhook calling service, implemented using Spring Boot. But it can be deployed on web services or at any SaaS platform that supports Java 8.

### Messages processing 

When the Destinations and Messages arrive they are persisted in the Database. Then, when a Message is received and persisted, an asynchronous listener processes all the Messages from current Destination sorted by order of arrival. If a Message is not delivered to its Destination, the process queue stops, and a scheduled method tries to process the persisted messages past every 6th hour. All message processing verifies if the message is in the timeout of 24 hours, if not the message is deleted.  

### Requirements:

1. Java 1.8
2. Gradle 
3. Mysql


### Configure, build and run the project:

All project configurations are in the file `/src/main/resources/application.properties`.

Basically, it is need to configure the database connection, setting the following properties:
```
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/webhook
spring.datasource.username=webhook
spring.datasource.password=webhook
```

Then, to build and run a project artifact, you can type the following: 

```
$ gradle build
$ java -jar build/libs/hs-webhook-0.1.0.jar
```

### Examples of services request:

- Register a new destination (URL) returning its id


`curl -v --header "Content-type: application/x-www-form-urlencoded" --request POST --data "url=http://posttestserver.com/post.php?dir=webhook"  http://localhost:8080/destinations`


- List registered destinations [{id, URL},...]

`curl -v --request GET http://localhost:8080/destinations`


- Delete a destination by id

`curl -v --request DELETE http://localhost:8080/destinations/1`


- POST a message to this destination

`curl -v --header "Content-type: text/pain" --request POST --data "WEBHOOK TEST" http://localhost:8080/destinations/1/message` 

