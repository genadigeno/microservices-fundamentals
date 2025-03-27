# Introduction to Microservices
introduction-to-microservices practical tasks

1. Resource service
2. Resource processor
3. Song Service

## Resource service
server port `SERVER_PORT` default 8083 </br>

GET:     /resources/{id} </br>
DELETE:  /resources?id=1,2,3 </br>
POST:    /resources </br>

Database name: **songs** </br>
Schema name: **public** </br>
DB username: `POSTGRES_USERNAME` default **postgres** </br>
DB password: `POSTGRES_PASSWORD` default **postgres** </br>
JDBC url: `POSTGRES_URL` default **jdbc:postgresql://localhost:54321/resources?currentSchema=public**

AWS S3 configurations:</br>
aws region: `AWS_REGION` default **us-east-1** </br>
aws credentials kay id: `AWS_ACCESS_KEY_ID` default **test** </br>
aws credentials access key: `AWS_SECRET_KEY` default **test** </br>
aws s3 bucket: `AWS_BUCKET_NAME` default **module-1** </br>

## Song service
server port `SERVER_PORT` default 8082 </br>

GET:     /songs/{id} </br>
DELETE:  /songs?id=1,2,3 </br>
POST:    /songs </br>

Database name: **resources** </br>
Schema name: **public** </br>
DB username: `POSTGRES_USERNAME` default **postgres** </br>
DB password: `POSTGRES_PASSWORD` default **postgres** </br>
connection string: `POSTGRES_URL` default **jdbc:postgresql://localhost:54322/songs?currentSchema=public**

## localstack
check if the bucket has been created:
```shell
  docker exec -it localstack awslocal s3api list-buckets
```
create bucket manually into container if it was not created on startup: 
```shell
  docker exec -it localstack awslocal s3 mb s3://module-1
```
list out files:
```shell
  docker exec -it localstack awslocal s3api list-objects --bucket module-1
```