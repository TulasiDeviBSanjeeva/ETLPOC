# ETLPOC
POC of an ETL solution using spring batch

## Problem Scope
Task: Political speeches
Scenario: Implementation of a feature in Java or Scala with subsequent code review and productive deployment.
Goal: Processing statistics on political speeches.
Input: CSV files (UTF-8 encoding) corresponding to the following scheme:
speaker, subject, date, words. Example CSV content -

```
Speaker,Subject,Date,Words
Alexander Abel,Education Policy,2012-10-30,5310 
Bernhard Belling,coal subsidies,2012-11-05,1210 
Caesare Collins,coal subsidies,2012-11-06,1119
Alexander Abel,Internal Security,2012-12-11,911

```

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
* Navigate to the project directory and build the project and run the tests by running ```mvn clean install```
* Once successfully built, you can run the application using :
```
        mvn spring-boot:run

```

Once the application runs you should see something like this

```
2020-08-31 01:20:08.983  INFO 21420 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http)
2020-08-31 01:20:09.548  INFO 21420 --- [  restartedMain] com.pnc.etlpoc.ETLPOCApplication         : Started ETLPOCApplication in 5.095 seconds (JVM running for 6.321)
```

### To view Swagger 2 API docs

* The API is "self documented" by Swagger2. You can access the api documentation under http://localhost:8080/v2/api-docs
* Browse to http://localhost:8080/swagger-ui.html to test.
* If you are using any rest client like Postman, following requests would be helpful
### TestCase 1
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv
```
Response Code : 200
{
    "mostSpeeches": "Alexander Abel",
    "mostSecurity": "Alexander Abel",
    "leastWords": "Caesare Collins"
}
```

### TestCase2
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv&url2=null
```
{
    "status": "400 BAD_REQUEST",
    "message": "One or both resource request parameter does not have the .csv file extension.",
    "exception": "JobParametersInvalidException"
}
```

### TestCase3
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data6.csv
```
{
    "status": "404 NOT_FOUND",
    "message": "Job[ETL] execution failed.Reason : 'Resource at url (https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data6.csv) NOT found or corrupted.'",
    "exception": "ResourceNotFoundException"
}
```

### TestCase4
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/addresses.csv
```
{
    "status": "400 BAD_REQUEST",
    "message": "Job[ETL] execution failed.Reason : 'org.springframework.batch.item.file.FlatFileParseException: Parsing error at line: 1 in resource=[class path resource [data/addresses.csv]], input=[John,Doe,120 jefferson st.,Riverside, NJ, 08075]'",
    "exception": "FileParseException"
}
```

### TestCase5
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data3.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data4.csv
```
{
    "mostSpeeches": "zero",
    "mostSecurity": "Alexander Abel2",
    "leastWords": "Caesare Collins2"
}
```

### TestCase6
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data4.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data5.csv

```
{
    "mostSpeeches": "zero",
    "mostSecurity": "Alexander Abel2",    // NOTE TODO : There are two Speakers(Alexander Abel2 and Caesare Collins2) as results who spoke on subject 'Innere                                                      Sicherheit', which should result in 'zero but the query picks the first record from the resultset.
    "leastWords": "Bernhard Belling2"
}
```
}

## About the Service

The service is just a simple speaker summary REST service. It uses an in-memory database (H2) to store the data. You can also do with a relational database like MySQL or PostgreSQL. Make sure to update your 'application.properties' with right DB configuration.

There is only a single endpoint, you can call:

```
http://localhost:8080/api/speakers/info?url1=url1&url2=url2
```
And addresses following queries -
* Which politician made the most speeches in 2013?
* Which politician gave the most speeches on the topic of "internal security"?
* Which politician spoke the fewest words overall?

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


