# ETLPOC
POC of an ETL solution using spring batch

## Problem Scope
Task: Political speeches
Scenario: Implementation of a feature in Java or Scala with subsequent code review and productive deployment.
Goal: Processing statistics on political speeches.
Input: CSV files (UTF-8 encoding) corresponding to the following scheme:
speaker, subject, date, words. Example CSV content -
Speaker,Subject,Date,Words
Alexander Abel,Education Policy,2012-10-30,5310 
Bernhard Belling,coal subsidies,2012-11-05,1210 
Caesare Collins,coal subsidies,2012-11-06,1119
Alexander Abel,Internal Security,2012-12-11,911

You want to be able to start an HTTP server with Maven or SBT that has 1 or more URLS as query parameters under the route 
GET /evaluation ?url1=url1&url2=url2.
The CSV files located at these URLs are evaluated and if the input is valid, the following Questions answered. 
If no or no unique answer is possible for a question, this field should be filled with zero.
1. Which politician made the most speeches in 2013?
2. Which politician gave the most speeches on the topic of "internal security"? 
3. Which politician spoke the fewest words overall?

```
JSON Output:
{
"mostSpeeches": String | zero, "mostSecurity": String | zero, "leastWordy": String | zero
}
```

```
Response:
Status: 200
Body: {
"mostSpeeches": zero, "mostSecurity": "Alexander Abel." "leastWordy": "Caesare Collins"
}
```

## Proposed Solution
The proposed solution is identified to be able to handle not only few data sets but also several gigabytes.
The ETL solution is addressed using Spring Batch. It is expected for the batch application to process large volumes of data, performance and robustness. 

## How to Run
This application is packaged as a jar which has Tomcat 8 embedded. No Tomcat or JBoss installation is necessary. You run it using the ```java -jar``` command.

* Clone this repository 
* Make sure you are using JDK 1.8 and Maven 3.x
* You can build the project and run the tests by running ```mvn clean package```
* You can also build by skipping tests by running ```mvn -DskipTests package```
* Once successfully built, you can run the service by one of these two methods:
```
        java -jar target/etlpoc-0.0.1-SNAPSHOT.jar

```

Once the application runs you should see something like this

```
2020-08-31 01:20:08.983  INFO 21420 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http)
2020-08-31 01:20:09.548  INFO 21420 --- [  restartedMain] com.pnc.etlpoc.ETLPOCApplication         : Started ETLPOCApplication in 5.095 seconds (JVM running for 6.321)
```

## About the Service

The service is just a simple speaker summary REST service. It uses an in-memory database (H2) to store the data. You can also do with a relational database like MySQL or PostgreSQL. Make sure to update your 'application.properties' with right DB configuration.

There is only a single endpoint, you can call:

```
http://localhost:8080/api/speakers/info?url1=url1&url2=url2
```
### Retrieve Speakers Summary Information

For Example: Using CLI Curl
```
curl -X GET "http://localhost:8080/api/speakers/info?url1=https%3A%2F%2Fraw.githubusercontent.com%2FTulasiDeviBSanjeeva%2FTestData%2Fmaster%2Fdata%2Fspeaker-data1.csv&url2=https%3A%2F%2Fraw.githubusercontent.com%2FTulasiDeviBSanjeeva%2FTestData%2Fmaster%2Fdata%2Fspeaker-data2.csv" -H "accept: application/json"

HttpStatus Code 200
Response body
{
  "mostSpeeches": "Alexander Abel",
  "mostSecurity": "Alexander Abel",
  "leastWords": "Caesare Collins"
}
```

### To view Swagger 2 API docs

* The API is "self documented" by Swagger2 using annotations. 
* Run the server and browse to localhost:8090/swagger-ui.html

Try various combinations and test.
