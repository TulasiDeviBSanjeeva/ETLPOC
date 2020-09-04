~ ETLPOC ~
POC einer ETL-Lösung mit Spring-Batch

## Umfang des Problems
Aufgabe: Politische Reden.   
Szenario: Implementierung einer Funktion in Java oder Scala mit anschließender Codeüberprüfung und produktivem Einsatz.   
Ziel: Verarbeitung von Statistiken über politische Reden.   
Eingabe: CSV-Dateien (UTF-8-Kodierung) entsprechend dem folgenden Schema:    
Redner, Thema, Datum, Worte. Beispiel CSV-Inhalt -  

```
Redner,Thema,Datum,Worte
Alexander Abel,Bildungspolitik,2012-10-30,5310 
Bernhard Belling,Kohlesubventionen,2012-11-05,1210 
Caesare Collins,Kohlesubventionen,2012-11-06-06,1119
Alexander Abel,Innere Sicherheit,2012-12-11,911

```

Sie möchten in der Lage sein, einen HTTP-Server mit Maven oder SBT zu starten, der 1 oder mehrere URLS als Query-Parameter unter der Route 
GET /Bewertung ?url1=url1&url2=url2.
Die CSV-Dateien, die sich unter diesen URLs befinden, werden ausgewertet, und wenn die Eingabe gültig ist, werden die folgenden Fragen beantwortet. 
Wenn für eine Frage keine oder keine eindeutige Antwort möglich ist, sollte dieses Feld mit Null gefüllt werden.
1. Welcher Politiker hat im Jahr 2013 die meisten Reden gehalten?
2. Welcher Politiker hat die meisten Reden zum Thema "Innere Sicherheit" gehalten? 
3. Welcher Politiker hat insgesamt die wenigsten Worte gesprochen?

```
JSON-Ausgabe:
{
"mostSpeeches": String | null, "mostSecurity": Zeichenkette | null, "leastWordy": Zeichenkette | null
}
```

```
Antwort:
Stand: 200
Körper: {
"meisteReden": null, "meisteSicherheit": {\an8}"Alexander Abel." "leastWordy": "Caesare Collins"
}
```

## Vorgeschlagene Lösung
Die vorgeschlagene Lösung ist so konzipiert, dass nicht nur wenige Datensätze, sondern auch mehrere Gigabyte verarbeitet werden können.
Die ETL-Lösung wird mit Spring Batch adressiert. Von der Batch-Anwendung wird erwartet, dass sie große Datenmengen verarbeiten kann, leistungsfähig und robust ist. 

TechStack :  
Java8  
SpringBoot-Batch  
SpringBoot-Daten JPA  
H2-In-Memory-Datenbank  
Swagger2  
Und einige andere Dienstprogrammbibliotheken von lombok, apache commons, httpclient  
Maven als Build-Tool  

## Ausführung
Diese Anwendung ist in einem Tiegel verpackt, in den Tomcat 8 eingebettet ist. Eine Tomcat- oder JBoss-Installation ist nicht erforderlich. 

* Dieses Repository klonen 
* Stellen Sie sicher, dass Sie JDK 1.8 und Maven 3.x verwenden
* Navigieren Sie zum Projektverzeichnis und bauen Sie das Projekt und führen Sie die Tests durch, indem Sie ```mvn clean install``` ausführen
* Nach erfolgreicher Erstellung können Sie die Anwendung mit :
```
        mvn spring boot:run

```

Sobald die Anwendung läuft, sollten Sie etwas wie das hier sehen

```
2020-08-31 01:20:08.983 INFO 21420 --- [ restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat startete auf Port(s): 8080 (http)
2020-08-31 01:20:09.548 INFO 21420 --- [ restartedMain] com.pnc.etlpoc.ETLPOCAnwendung : ETLPOC-Anwendung wurde in 5,095 Sekunden gestartet (JVM läuft seit 6.321)
```

### Zum Anzeigen der Swagger 2 API-Dokumente

* Die API wird von Swagger2 "selbst dokumentiert". Sie können auf die api-Dokumentation unter http://localhost:8080/v2/api-docs zugreifen.
* Navigieren Sie zum Testen zu http://localhost:8080/swagger-ui.html.
* Wenn Sie einen Rest Client wie Postman verwenden, wären folgende Anfragen hilfreich

### Testfall 1
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv
```
Response Code : 200
{
    "mostSpeeches": "Alexander Abel",
    "mostSecurity": "Alexander Abel",
    "leastWords": "Caesare Collins"
}
```

### Testfall2
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv&url2=null
```
{
    "status": "400 BAD_REQUEST",
    "message": "One or both resource request parameter does not have the .csv file extension.",
    "exception": "JobParametersInvalidException"
}
```

### Testfall3
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data6.csv
```
{
    "status": "404 NOT_FOUND",
    "message": "Job[ETL] execution failed.Reason : 'Resource at url (https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data6.csv) NOT found or corrupted.'",
    "exception": "ResourceNotFoundException"
}
```

### Testfall4
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data1.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/addresses.csv
```
{
    "status": "400 BAD_REQUEST",
    "message": "Job[ETL] execution failed.Reason : 'org.springframework.batch.item.file.FlatFileParseException: Parsing error at line: 1 in resource=[class path resource [data/addresses.csv]], input=[John,Doe,120 jefferson st.,Riverside, NJ, 08075]'",
    "exception": "FileParseException"
}
```

### Testfall5
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data3.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data4.csv
```
{
    "mostSpeeches": "zero",
    "mostSecurity": "Alexander Abel2",
    "leastWords": "Caesare Collins2"
}
```

### Testfall6
http://localhost:8080/api/speakers/info?url1=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data4.csv&url2=https://raw.githubusercontent.com/TulasiDeviBSanjeeva/TestData/master/data/speaker-data5.csv

```
{
    "mostSpeeches": "zero",
    "mostSecurity": "Alexander Abel2",    // NOTE TODO : There are two Speakers(Alexander Abel2 and Caesare Collins2) as results who spoke on subject 'Innere                                                      Sicherheit', which should result in 'zero but the query picks the first record from the resultset.
    "leastWords": "Bernhard Belling2"
}
```
}

## Über den Dienst

Der Dienst ist nur eine einfache Sprecherzusammenfassung REST-Dienst. Er verwendet eine In-Memory-Datenbank (H2) zur Speicherung der Daten. Sie können auch eine relationale Datenbank wie MySQL oder PostgreSQL verwenden. Stellen Sie sicher, dass Sie Ihre 'application.properties' mit der richtigen DB-Konfiguration aktualisieren.

Es gibt nur einen einzigen Endpunkt, den Sie aufrufen können:

```
http://localhost:8080/api/speakers/info?url1=url1&url2=url2
```
Und spricht folgende Fragen an -
* Welcher Politiker hat 2013 die meisten Reden gehalten?
* Welcher Politiker hat die meisten Reden zum Thema "Innere Sicherheit" gehalten?
* Welcher Politiker hat insgesamt die wenigsten Worte gesprochen?

### Zusammenfassende Informationen zu den Rednern abrufen

Zum Beispiel: CLI-Curl verwenden
```
curl -X GET "http://localhost:8080/api/speakers/info?url1=https%3A%2F%2Fraw.githubusercontent.com%2FTulasiDeviBSanjeeva%2FTestData%2Fmaster%2Fdata%2Fspeaker-data1.csv&url2=https%3A%2F%2Fraw.githubusercontent.com%2FTulasiDeviBSanjeeva%2FTestData%2Fmaster%2Fdata%2Fspeaker-data2.csv" -H "accept: application/json"

HttpStatus Code 200
Response body
{
  "mostSpeeches": "Alexander Abel",
  "meisteSicherheit": "Alexander Abel",
  "LeastWords": "Cäsar Collins"
}
```
