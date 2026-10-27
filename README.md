# TaskFlow

TaskFlow ist ein Task-Management-System, mit dem Benutzer persönliche Aufgaben verwalten können. Das System bietet eine REST-API (JSON) zum Zugriff und zur Synchronisation und soll hauptsächlich in Firmen als Arbeitsmittel für MitarbeiterInnen eingesetzt werden, um ihre persönlichen Aufgaben zu planen.

Das Projekt entsteht im Rahmen der Lehrveranstaltung **Software Quality** an der FH und wird von einem Team aus vier Studenten umgesetzt. 

## Inhaltsverzeichnis

1. [Team und Aufgabenverteilung](#team-und-aufgabenverteilung)
2. [Technologie-Stack](#technologie-stack)
3. [Funktionale Anforderungen](#funktionale-anforderungen)
4. [REST-API](#rest-api)
5. [Datenmodell](#datenmodell)
6. [Qualitätsanforderungen](#qualitätsanforderungen)
7. [Projekt starten](#projekt-starten)
8. [Projektstruktur](#projektstruktur)
9. [Abnahmekriterien](#abnahmekriterien)

## Team und Aufgabenverteilung

| Bereich | Personen | Aufgaben |
|---|---|---|
| Frontend | Markus, Thomas | User Interface |
| Backend | Casper, Umejr | Spring Boot und PostgreSQL |

### Personen

<b>Casper</b> [<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg" width="24" style="margin: 0 0.5em; transform: translate(0, 7px)"/>](https://github.com/casper-zielinski) [GitHub-Profil](https://github.com/casper-zielinski) <span style="margin: 0 0.5rem;"></span>
[<img src="https://static.thenounproject.com/png/286716-200.png" width="24" style="margin: 0 0.5rem; transform: translate(0, 7px)">](https://casperzielinski-portfolio.vercel.app/) [Portfolio](https://casperzielinski-portfolio.vercel.app/)

<b>Markus</b> [<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg" width="24" style="margin: 0 0.5em; transform: translate(0, 7px)"/>](https://github.com/Th3Stubi) [GitHub-Profil](https://github.com/Th3Stubi) <span style="margin: 0 0.5rem;"></span>
[<img src="https://static.thenounproject.com/png/286716-200.png" width="24" style="margin: 0 0.5rem; transform: translate(0, 7px)">](https://casperzielinski-portfolio.vercel.app/) [Portfolio](https://casperzielinski-portfolio.vercel.app/)

<b>Umejr</b> [<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg" width="24" style="margin: 0 0.5em; transform: translate(0, 7px)"/>](https://github.com/Umex10) [GitHub-Profil](https://github.com/Umex10) <span style="margin: 0 0.5rem;"></span>
[<img src="https://static.thenounproject.com/png/286716-200.png" width="24" style="margin: 0 0.5rem; transform: translate(0, 7px)">](https://dev-resume-sigma.vercel.app/) [Portfolio](https://dev-resume-sigma.vercel.app/)

<b>Thomas</b> [<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg" width="24" style="margin: 0 0.5em; transform: translate(0, 7px)"/>](https://github.com/FrontendTF) [GitHub-Profil](https://github.com/FrontendTF) <span style="margin: 0 0.5rem;"></span>
[<img src="https://static.thenounproject.com/png/286716-200.png" width="24" style="margin: 0 0.5rem; transform: translate(0, 7px)">](https://tfutureweb.at/) [Portfolio](https://tfutureweb.at/)

Weitere Zuständigkeiten im Backend:

| Thema | Verantwortlich |
|---|---|
| Docker-Containerisierung | Casper |
| CI/CD | Casper |
| OpenAPI | Markus |
| SonarQube | Thomas |
| Jackson (JSON) | Backend-Team |
| JUnit 5 | Jeder testet seine eigenen API-Endpunkte |

Aufteilung der Endpunkte: Umejr implementiert die lesenden Endpunkte (**GET**), Casper die verändernden Endpunkte (**POST, PUT, DELETE**).

## Technologie-Stack

| Bereich | Technologie |
|---|---|
| Sprache | Java 21 (gefordert: Java 17 oder höher) |
| Framework | Spring Boot 4.1.2 (Spring Web MVC, Spring Data JPA, Actuator) |
| Build-Tool | Maven (Maven Wrapper `mvnw` im Backend-Ordner) |
| Datenbank | PostgreSQL 17.7 |
| JSON | Jackson |
| Tests | JUnit 5, Mockito |
| Containerisierung | Docker, Docker Compose |
| Qualitätssicherung | SonarQube, Checkstyle, CI-Pipeline (Build, Tests, Checkstyle) |
| Frontend | noch offen |

Hinweis: In der Anforderungsspezifikation ist SQLite als Datenbank genannt. Das Team hat sich für **PostgreSQL** entschieden.

## Funktionale Anforderungen

Kurzfassung der Anforderungen aus der Spezifikation:

- **Aufgabenverwaltung:** Aufgaben mit Titel, optionaler Beschreibung, Priorität (NIEDRIG, MITTEL, HOCH) und optionalem Fälligkeitsdatum. Jede Aufgabe erhält automatisch eine eindeutige ID und einen Erstellungszeitstempel. Aufgaben können bearbeitet werden (alle Attribute außer ID und Erstellungszeitpunkt) und gelöscht werden, wobei eine Bestätigung erforderlich ist.
- **Status:** OFFEN, IN_BEARBEITUNG und ABGESCHLOSSEN. Bei einer Statusänderung wird automatisch ein Zeitstempel gespeichert.
- **Ansicht und Filterung:** Listenansicht, standardmäßig nach Priorität und danach nach Fälligkeitsdatum sortiert. Filter nach Status, Priorität, Fälligkeit (überfällig, heute, diese Woche) und Suchbegriffen in Titel oder Beschreibung.
- **Detailansicht:** Alle Informationen einer Aufgabe inklusive der Historie der Statusänderungen.
- **Statistik:** Anzahl der Aufgaben pro Status und Anzahl überfälliger Aufgaben.
- **Datenpersistenz:** Beim Start werden die gespeicherten Daten automatisch geladen, bei jeder Änderung aktualisiert. Eine Backup-Funktion exportiert die Datenbasis in eine JSON-Datei mit Zeitstempel.
- **Benachrichtigungen:** Beim Start wird geprüft, welche Aufgaben überfällig oder heute fällig sind, und diese werden in einer eigenen Ansicht hervorgehoben.
- **Einschränkung:** Eine Benutzerverwaltung ist optional. Alle Benutzer können ohne Anmeldung auf die Anwendung zugreifen.

## REST-API

Die API verwendet JSON und liefert die passenden HTTP-Statuscodes.

| Methode | Pfad | Beschreibung | Zuständig |
|---|---|---|---|
| GET | `/api/tasks` | Alle Aufgaben abrufen | Umejr |
| GET | `/api/tasks/{id}` | Einzelne Aufgabe abrufen | Umejr |
| GET | `/api/tasks/stats` | Statistiken abrufen | Umejr |
| POST | `/api/tasks` | Neue Aufgabe erstellen | Casper |
| PUT | `/api/tasks/{id}` | Aufgabe aktualisieren | Casper |
| DELETE | `/api/tasks/{id}` | Aufgabe löschen | Casper |

## Datenmodell

```
Task {
  id            INTEGER UNIQUE,
  Title         VARCHAR(100),
  Beschreibung  VARCHAR(100),
  Priorität     ENUM,
  Status        ENUM,
  changedAt     Date
}

Status    { OFFEN, IN_BEARBEITUNG, ABGESCHLOSSEN }
Priorität { NIEDRIG, MITTEL, HOCH }
```

## Qualitätsanforderungen

Die Qualitätsziele basieren auf der Norm ISO 25010 und sind mit messbaren Zielwerten hinterlegt.

| # | Qualitätsziel | Stakeholder | ISO-25010-Kriterium | Messgröße | Zielwert |
|---|---|---|---|---|---|
| 1 | **Einfache Bedienbarkeit** (Req. 3.1) | Endbenutzer | Interaction Capability: Operability, Learnability, User Error Protection | Zeit bis zur ersten Aufgabe und Erfolgsquote im Usability-Test; Anteil der Fehlermeldungen mit Hinweis zur Korrektur | 60 s oder weniger, mindestens 90 % Erfolg; 100 % der Meldungen mit Hinweis |
| 2 | **Hohe Performance** (Req. 3.2) | Endbenutzer, Betreiber | Performance Efficiency: Time Behaviour, Capacity | Startzeit und 95. Perzentil der Filterzeit, jeweils mit 100.000 Aufgaben (Lasttest) | Start 2 s oder weniger, Filter 100 ms oder weniger |
| 3 | **Hohe Zuverlässigkeit** (Req. 3.3) | Betreiber, Endbenutzer | Reliability: Availability, Fault Tolerance, Recoverability; Security: Integrity | Uptime pro Monat; Abstürze in Negativtests mit ungültigen Eingaben; verlorene Daten im Abbruch-Test (Prozess hart beenden) | mindestens 99,5 % (Vorgabe über 99 %); 0 Abstürze; 0 Datenverluste |
| 4 | **Gute Wartbarkeit** (Req. 3.4) | Entwicklungsteam | Maintainability: Modularity, Testability | Testabdeckung der Geschäftslogik (JUnit 5); Verstöße gegen Coding-Standards (Checkstyle); Komplexität pro Methode | mindestens 80 %; 0 Verstöße; höchstens 10 |
| 5 | **Korrekte Funktionalität** (Req. 2 und 5) | Endbenutzer, Management | Functional Stability: Completeness, Correctness | Anteil umgesetzter und getesteter funktionaler Anforderungen; Anzahl kritischer Bugs; Anteil der 6 REST-Endpunkte mit korrekten Statuscodes | 100 %; 0 kritische Bugs; 6 von 6 |
| 6 | **Portabilität** (Req. 3.6) | Endbenutzer, Management | Flexibility: Adaptability; Compatibility: Interoperability (JSON-API) | Anzahl Browser (Firefox, Chrome, Safari), in denen die End-to-End-Tests bestehen | 3 von 3 |

### Konstruktive Maßnahmen

1. **Bedienbarkeit:** Einheitliche UI-Guidelines, Lösch-Bestätigung, Prototyp vorab testen, zentrale Fehlertexte.
2. **Performance:** Datenbank-Indizes (Status, Priorität, Fälligkeit), Filtern und Sortieren in der Datenbank statt im Speicher, Paginierung, schlanker Start (Lazy Loading).
3. **Zuverlässigkeit:** Datenbank-Transaktionen, Eingabevalidierung, zentrale Fehlerbehandlung, Health-Check mit automatischem Neustart, tägliche Backups (auch JSON-Export).
4. **Wartbarkeit:** Schichten (Controller, Service, Repository), JUnit 5 und Mockito, Merge Requests mit Code-Review, CI-Pipeline (Build, Tests, Checkstyle).
5. **Funktionalität:** Ein Testfall pro Anforderung, API-Integrationstests, Definition of Done mit Abnahmekriterien.
6. **Portabilität:** Standard-HTML/CSS/JS ohne browserspezifische Funktionen, automatische UI-Tests (zum Beispiel Playwright, auch mit WebKit für Safari).

## Projekt starten

### Voraussetzungen

- [Docker](https://www.docker.com/) mit Docker Compose
- Für die lokale Entwicklung ohne Container zusätzlich ein JDK 21 (Maven muss nicht installiert sein, der Wrapper `mvnw` ist im Repository enthalten)
- Die Ports **5432** (Datenbank) und **8080** (Backend) müssen frei sein

### Variante 1: Alles mit Docker Compose (empfohlen)

Im Projekt-Root:

```bash
docker compose up --build
```

Das baut das Backend-Image, startet PostgreSQL und das Spring-Boot-Backend. Das Backend startet erst, wenn die Datenbank bereit ist (Healthcheck).

Beenden:

```bash
docker compose down
```

Das Compose-Setup verwendet kein Volume. Beim Beenden gehen die Daten in der Datenbank verloren.

### Variante 2: Backend-Ordner

Der Ordner `backend/` ist eigenständig startbar und enthält ebenfalls Datenbank und Backend:

```bash
cd backend
docker compose up --build
```

Der Root-Stack bindet genau diese Datei ein, deshalb ist das Ergebnis dasselbe.

### Variante 3: Nur die Datenbank im Container, Backend lokal

Für die Entwicklung in der IDE:

```bash
cd backend
docker compose up -d db
./mvnw spring-boot:run
```

Das lokal laufende Backend verbindet sich über `localhost:5432` mit der Datenbank (siehe `backend/src/main/resources/application.properties`).

### Prüfen, ob alles läuft

Der Health-Endpunkt des Backends muss den Status `UP` melden:

```bash
curl http://localhost:8080/actuator/health
```

### Zugangsdaten der Entwicklungsdatenbank

| Eigenschaft | Wert |
|---|---|
| Host (lokal) | `localhost:5432` |
| Host (im Compose-Netzwerk) | `db:5432` |
| Datenbank | `taskflow_db` |
| Benutzer | `postgres` |
| Passwort | `postgres` |

Diese Zugangsdaten gelten nur für die lokale Entwicklung.

## Projektstruktur

```
TaskFlow/
├── backend/                  Spring-Boot-Anwendung (Maven)
│   ├── src/main/java/com/taskflow/taskflow/
│   ├── src/main/resources/application.properties
│   ├── Dockerfile            Multi-Stage-Build des Backend-Images
│   ├── .dockerignore
│   ├── compose.yaml          Definition von Datenbank und Backend
│   └── pom.xml
├── frontend/                 noch offen
├── docker-compose.yaml       bindet backend/compose.yaml ein
└── README.md
```

## Abnahmekriterien

Das System ist abnahmereif, wenn:

- alle funktionalen Anforderungen implementiert und getestet sind,
- die Testabdeckung mindestens 80 % beträgt,
- keine kritischen Bugs bekannt sind,
- eine Benutzerdokumentation vorhanden ist,
- der Code den vereinbarten Coding-Standards entspricht.
