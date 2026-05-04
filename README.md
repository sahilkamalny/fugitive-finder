# FugitiveFinder

A JavaFX desktop application for tracking and analyzing FBI Most Wanted fugitives. Built with the MVVM architecture pattern, live FBI API data, and Firebase backend integration.

## Features

- **Dashboard** — Browse 100+ fugitive cards with sorting (reward, name), pagination, and save-to-profile functionality
- **Criminal Profiles** — Detailed view with aliases, field offices, reward info, race, sex, and subject data
- **Crime Analytics** — 4 interactive charts: crime categories, field office distribution, race/ethnicity breakdown, sex distribution
- **Most Dangerous Leaderboard** — Danger score algorithm (0-100) ranking fugitives by reward, warning level, classification, and crime type
- **Region Statistics** — US geographic crime breakdown across 4 regions (Northeast, South, Midwest, West) with bar and pie charts
- **Interactive Map** — Gluon Maps with FBI field office markers, zoom controls, and click-to-view functionality
- **User Accounts** — Firebase Authentication for login/signup with Firestore-backed saved targets
- **User Profile** — View and manage saved/bookmarked criminal profiles

## Screenshots

> Replace these placeholders with actual screenshots of the running app.

| Screen | Screenshot |
|--------|------------|
| Dashboard | `[screenshot]` |
| Analytics | `[screenshot]` |
| Leaderboard | `[screenshot]` |
| Criminal Profile | `[screenshot]` |
| Map View | `[screenshot]` |
| User Profile | `[screenshot]` |

## Architecture

```
src/main/java/org/example/fugitivefinder/
├── model/              # Data models (WantedPerson, AppUser, FirebaseUser)
├── model/repository/   # Repository interfaces (FbiRepository, UserRepository)
├── service/            # Business logic services
│   ├── FbiApiService       — FBI API HTTP client
│   ├── ChartDataService    — Chart data aggregation
│   ├── LeaderboardService  — Danger score algorithm
│   ├── RegionStatsService  — Geographic grouping (56 offices → 4 regions)
│   ├── FirebaseAuthService — Firebase Authentication
│   ├── FirestoreService    — Firestore CRUD operations
│   └── UserService         — User session management
├── session/            # Session singleton for app state
├── view/               # FXML Controllers (MVVM View layer)
│   ├── App                     — Application entry point
│   ├── SplashController        — Splash screen
│   ├── LoginPageController     — Login screen
│   ├── CreateAccountPageController — Registration screen
│   ├── DashboardController     — Main dashboard with cards + sorting
│   ├── AnalyticsController     — Crime analytics charts
│   ├── LeaderboardController   — Danger leaderboard + region stats
│   ├── CriminalProfileController — Detailed fugitive view
│   ├── MapsViewController      — Interactive FBI field office map
│   └── UserProfileController   — Saved targets + profile
└── viewModel/          # ViewModels (MVVM logic layer)
    ├── SceneManager            — Navigation utility
    ├── DashboardViewModel      — Dashboard data + navigation
    ├── AnalyticsViewModel      — Chart data loading + distribution
    ├── LeaderboardViewModel    — Leaderboard + region stats data
    ├── CriminalProfileViewModel — Profile detail bindings
    ├── MapsViewModel           — Map marker + coordinate data
    ├── LoginViewModel          — Firebase login logic
    ├── CreateAccountViewModel  — Firebase registration logic
    └── UserProfileViewModel    — Saved targets retrieval
```

### Design Pattern: MVVM (Model-View-ViewModel)

- **Model** — `WantedPerson`, `AppUser`, `FirebaseUser` data classes
- **View** — FXML layouts + Controller classes handle UI binding
- **ViewModel** — Observable properties, data loading, business logic

### Data Flow

```
FBI API (fbi.gov) → Django Backend (Render) → FbiApiService → ViewModel → Controller → FXML
                                              ↓
                                    ChartDataService / LeaderboardService / RegionStatsService
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | JavaFX 21, FXML |
| Maps | Gluon Maps |
| Charts | JavaFX Charts (BarChart, PieChart) |
| HTTP | java.net.http.HttpClient |
| JSON | Jackson (model parsing), org.json (raw API) |
| Backend API | Django REST Framework (deployed on Render) |
| Auth | Firebase Authentication |
| Database | Cloud Firestore |
| Build | Maven, JavaFX Maven Plugin |

## Prerequisites

- **Java 21+** (JDK)
- **Maven** (included via `mvnw` wrapper)
- Internet connection (live FBI API data)

## Setup & Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/sahilkamalny/fugitive-finder.git
   cd fugitive-finder
   ```

2. **Run the application**
   ```bash
   chmod +x mvnw
   ./mvnw clean javafx:run
   ```

3. **The app window will open** — create an account or log in to access all features.

## API

The app fetches live data from the FBI Most Wanted API through a Django backend proxy:

- **Backend URL:** `https://fbi-backend-wilt.onrender.com/api/wanted/`
- **Source API:** `https://api.fbi.gov/wanted/v1/list`
- **Records:** 100 fugitive profiles with full metadata

## Team

| Member | Role | Key Contributions |
|--------|------|-------------------|
| **Sahil Kamal** | Project Manager, Analytics | Sprint planning, ChartDataService, LeaderboardService, RegionStatsService, analytics/leaderboard UI, navigation integration, README |
| **Ahmaed Thomas** | Map & Navigation | MapController, MapsViewController, offices.json integration, zoom controls, scene routing |
| **Derek Mendez** | API & Data | FbiApiService, JSON parsing, WantedPerson model, sort/filter logic, image proxy |
| **Armaan Arora** | Backend & Auth | Django backend on Render, Firebase Auth, Firestore integration, saved targets |
| **Darianne Ramos** | UI/UX | FXML layouts, Figma mockups, CriminalProfile/Dashboard UI, dashboard filters |

## License

This project was developed as part of a university Software Engineering course.
