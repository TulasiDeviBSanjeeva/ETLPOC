~ ETLPOC ~
POC einer ETL-Lösung mit Spring-Batch

## Umfang des Problems
Aufgabe: Politische Reden
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
