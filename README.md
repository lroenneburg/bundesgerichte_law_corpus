# Bundesgerichte Law Decisions
### Bachelorthesis von Lennart Rönneburg

Dieses Repository ist Teil der Bachelorarbeit von Lennart Rönneburg der Universität Hamburg.
Die Arbeit trägt den Titel **"Analyse eines juristischen Entscheidungscorpus mit Methoden der Netzwerkforschung und Sprachtechnologie"** und entstand in einer Kooperation der Universität Hamburg, sowie der Bucerius Law School.

Im Rahmen der Bachelorarbeit wurden 55.000 Entscheidungen des Bundesverfassungsgerichtes, der obersten Gerichte des Bundes, sowie des Bundespatentgerichtes der Bundesrepublik Deutschland quantitativ untersucht. Die Untersuchung stellt dabei vor, wie die Entscheidungen mit netzwerktechnologischen und sprachtechnologischen Werkzeugen analysiert werden.

Link zu der Arbeit:
(Der Link folgt...)

Die Software steht zur freien Verfügung und kann aus diesem Repository gecloned werden.
Um die Software nutzen zu können, muss der Zugang zu einer Elasticsearch Datenbank bestehen. Die Zugänge zu dieser Datenbank können in der [application.properties](https://github.com/lroenneburg/bundesgerichte_law_corpus/blob/main/src/main/resources/application.properties) unter der Variable
```sh
eldb.host = [...] (host und port - Bsp: *beispielurl.beispielserver.de:1234*)
```
eingetragen werden.

Um die Anwendung zu starten muss dann schließlich die Spring-Boot Anwendung ausgeführt werden. Dies geschieht, indem die Klasse **LawCorpusApplication** ausgeführt wird.

Um die Webapplikation der Anwendung aufzurufen kann anschließend die folgende URL im Browser aufgerufen werden:
```sh
http://localhost:8080
```

Dort können die Informationen zum Projekt, Entscheidungscluster und Ergebnisse des Projektes angesehen werden. Es besteht auch die Möglichkeit die Datenbasis zu durchsuchen, dies kann über die oben auf der Webseite integrierte Suchmaschine passieren. Hier kann per Aktenzeichen (z.B. *1 ABR 11/18* oder *1 Ws 20/45*) oder mit einzelnen Termen wie z.B. "*Wegezeiten*" oder "*Musik*" nach Entscheidungstiteln gesucht werden.
