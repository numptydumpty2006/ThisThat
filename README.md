# This & That — Cafe Finder

A cafe discovery web app. Find nearby cafes using your real-time location, filter by what's open, price, and rating, then pick the right spot from a list or map.

## Stack

| Layer    | Tech                                                   |
|----------|--------------------------------------------------------|
| Frontend | Vue 3 + Vite + TypeScript, Vuetify 3, Pinia, Axios     |
| Backend  | Java 17, Spring Boot 4.1, Spring Data JPA, Security    |
| DB       | PostgreSQL 16 (Flyway migrations)                      |
| Cache    | Redis 7 (1-hour TTL on cafe searches)                  |
| API      | Google Places API (Nearby Search + Place Details)      |