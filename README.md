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
