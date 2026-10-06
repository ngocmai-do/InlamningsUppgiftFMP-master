Ett bokningssystem för ett pensionat uppdelat i två microservices som kommunicerar via REST.

Tjänster
1. Booking Service (port 8080)

Ansvarar för rum och bokningar. Innehåller Thymeleaf-frontend för hela systemet.

Endpoints:

GET /room/all – visa alla rum
GET /booking/all – visa alla bokningar
POST /booking/create – skapa en bokning
POST /booking/update – uppdatera en bokning
GET /booking/delete/{id} – avboka
GET /booking/customer/{id}/exists – kontrollera om en kund har aktiva bokningar (används av customer-service)
2. Customer Service (port 8081)

Ansvarar för all kundhantering. Exponerar ett REST API som svarar med JSON.

Endpoints:

GET /customers/all – hämta alla kunder
GET /customers/{id} – hämta en kund
POST /customers – registrera en kund
PUT /customers – uppdatera en kund
DELETE /customers/{id} – ta bort en kund


Hur tjänsterna pratar med varandra
Webbläsare
→ Booking Service (Thymeleaf)
→ Customer Service (REST/JSON) – för att hämta och hantera kunder
→ Booking Service databas – för rum och bokningar
När en bokning skapas frågar booking-service customer-service om kunden finns
När en kund tas bort frågar customer-service booking-service om kunden har aktiva bokningar
Om customerservice är nere så går det inte att göra nya bokningar men applikationen krashar inte


Databaser
Tjänst	Databas	Port
Booking Service	pensionat (MySQL)	3308
Customer Service	customers (MySQL)	3307

Varje tjänst har en egen databas. Tjänsterna får aldrig läsa direkt i varandras databaser.

Starta systemet
Krav
Docker Desktop installerat och igång
Starta hela systemet med ett kommando
bash
docker compose up --build

Systemet startar:

Booking Service på http://localhost:8080
Customer Service på http://localhost:8081
Två MySQL-databaser
Stänga ner systemet
bash
docker compose down

Stänga ner och rensa databaser:

bash
docker compose down -v
Projektstruktur
pensionat/
├── docker-compose.yml
├── InlamningsUppgiftFMP/     ← Booking Service
│   ├── Dockerfile
│   └── src/
└── customer-service/         ← Customer Service
├── Dockerfile
└── src/
Tekniker
Java 17
Spring Boot
Spring Data JPA / Hibernate
Thymeleaf
MySQL
Docker / Docker Compose
RestTemplate (kommunikation mellan tjänster)

Systemet är deployat på Railway: https://inlamningsuppgiftfmp-production.up.railway.app/booking/all

### Merge-konflikten löstes genom följande steg:

1/ Konflikten identifierades i filen BookingController.java där ändringar i felmeddelanden krockade mellan branches.

![](https://github.com/ngocmai-do/InlamningsUppgiftFMP-master/blob/master/documentation/merge-conflict1.png)

2/ I GitHubs webbeditor valdes att behålla och kombenera koden från båda brancherna så att både loggning (log.warn) och de uppdaterade felmeddelandena i model.addAttribute sparades.

![](https://github.com/ngocmai-do/InlamningsUppgiftFMP-master/blob/master/documentation/merge-conflict2.png)

3/ Ändringarna markerades som lösta (Mark as resolved) och genomfördes via Commit merge.

![](https://github.com/ngocmai-do/InlamningsUppgiftFMP-master/blob/master/documentation/merger-conflict3.png)

4/ Slutligen godkändes ändringarna (Changes approved) och alla automatiserade tester/checks passerade så att PR:en kunde mergas utan konflikter.

![](https://github.com/ngocmai-do/InlamningsUppgiftFMP-master/blob/master/documentation/merge-conflict4.png)

**Live application:**
---- här finns länken ----

Team workflow
We hold a daily stand-up every day. Each team member answers:

- What did I do since the last stand-up? Anything new that i've learned that i want to share?
- Am i stuck at anything and might need help?
- Is anything blocking me?
with this system we could both keep track on what everyone was doing aswell as learn from eachother.

We tracked our work via the kanban board built into github.
Backlog → To Do → In Progress → In Review → Done
A card moves to In Progress when work on it starts, to In Review when a pull request is opened, and to Done when the pull request is merged.

Branch strategy 
We use a trunk-based strategy. main is the trunk and should always be in a working, deployable state. All work happens in short-lived branches that are merged back into main quickly, ideally within a day or two, to avoid large and risky merges.
Branches are grouped in two categories Features and fixes
- features are new features added to the applikation
- fixes are bugfixes - grammar changes etc
always create new branches for each update from an up-to-date main

From branch to production

1. Create a card on the Kanban board with a clear description of what should be done and when it is considered done.
2. Create a branch from main using the naming convention above.
3. Make the change in small commits with short messages
4. Open a pull request to main. The description explains what changed, why
5. Code review: at least one other team member reviews and approves the pull request.
6. Merge into main once the checks pass and the review is approved. The branch is then deleted.
7. Deploy to production. The changes are merged to main


