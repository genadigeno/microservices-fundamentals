# Introduction to Microservices
introduction-to-microservices practical tasks

1. Resource service
2. Song Service

## Resource service
server port (`${SERVER_PORT}`) default 8081 </br>

GET:     /resources/{id} </br>
DELETE:  /resources?id=1,2,3 </br>
POST:    /resources </br>

Database name: **songs** </br>
Schema name: **public** </br>
DB username: (`${POSTGRES_USERNAME}`) default **postgres** </br>
DB password: (`${POSTGRES_PASSWORD}`) default **postgres** </br>
JDBC url: (`${POSTGRES_URL}`) default **jdbc:postgresql://localhost:54321/resources?currentSchema=public**

## Song service
server port (`${SERVER_PORT}`) default 8082 </br>

GET:     /songs/{id} </br>
DELETE:  /songs?id=1,2,3 </br>
POST:    /songs </br>

Database name: **resources** </br>
Schema name: **public** </br>
DB username: (`${POSTGRES_USERNAME}`) default **postgres** </br>
DB password: (`${POSTGRES_PASSWORD}`) default **postgres** </br>
JDBC url: (`${POSTGRES_URL}`) default **jdbc:postgresql://localhost:54322/songs?currentSchema=public**
