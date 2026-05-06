src="https://upload.wikimedia.org/wikipedia/commons/d/d3/Seal_of_the_Federal_Bureau_of_Investigation.svg" alt="FBI Seal" width="120"/>
  <h1>Fugitive Finder</h1>
  <p><strong>A modern, data-driven intelligence dashboard for tracking the FBI's Most Wanted.</strong></p>
  <p><em>CSC325 — Software Engineering Capstone Project</em></p>
  <p><em>Farmingdale State College — Spring 2026</em></p>
</div>

---

## 🎯 Project Overview

**Fugitive Finder** is a JavaFX desktop application that pulls, processes, and analyzes live data from the Federal Bureau of Investigation's Most Wanted API. The application provides a comprehensive intelligence platform featuring interactive dashboards, real-time crime analytics, geographic mapping, a custom danger-scoring algorithm, and secure cloud-synced user accounts.

Our objective was to apply modern software engineering practices — including the MVVM design pattern, Agile/SCRUM methodology, and full-stack cloud integration — to build a professional, production-quality desktop application as a team of five developers over the course of a semester.

---

## ✨ Key Features & Capabilities

### 🔍 1. Live Intelligence Dashboard
* **Dynamic Card Grid:** Browse 100+ fugitive profiles loaded in real-time from the FBI API via our Django backend proxy.
* **Smart Sorting & Filtering:** Sort suspects by reward amount, name, or warning level. Filter by crime category using dropdown controls.
* **Deep Criminal Profiles:** Click any card to view a comprehensive dossier including aliases, physical characteristics, associated field offices, reward information, and known crimes.

### 📊 2. Advanced Crime Analytics
* **Four Interactive Charts** break down the current Most Wanted list by:
  * **Top Crime Categories** (Bar Chart) — Seeking Information, Kidnappings, Criminal Enterprise, etc.
  * **Field Office Jurisdiction** (Pie Chart) — Which FBI offices carry the most cases
  * **Race / Ethnicity Demographics** (Bar Chart) — Demographic breakdown of fugitives
  * **Sex Distribution** (Pie Chart) — Male vs. Female vs. Not Listed
* **Real-time Data Processing:** All aggregation, sorting, and chart data are computed in real-time using Java Streams and `LinkedHashMap` ordered collections.

### 🗺️ 3. Geographic Tracking & Interactive Map
* **Gluon Maps Integration:** A fully interactive, pannable, zoomable map plotting all 56 FBI Field Offices across the United States.
* **Interactive Office Markers:** Click any marker to reveal a side panel of local fugitives.
* **Zoom Controls:** Dedicated zoom in, zoom out, and reset buttons for easy navigation.
* **Region Statistics:** Detailed breakdown of crime concentration across 4 major US regions (Northeast, South, Midwest, West) with supporting bar and pie charts (integrated into the Leaderboard view).

### 🏆 4. "Danger Score" Leaderboard
* **Custom Scoring Algorithm:** A proprietary 0–100 Danger Score evaluates each fugitive based on:
  * 💰 Reward amount (higher reward = higher danger)
  * ⚠️ Warning classifications (e.g., "Armed and Dangerous", "Should be considered dangerous")
  * 🔪 Crime severity weighting (Murder, Terrorism, Cybercrime receive higher scores)
* **Ranked Leaderboard:** Instantly identifies the most critical threats in a sortable, color-coded table.

### 🔐 5. Secure User Accounts (Cloud Synced)
* **Firebase Authentication:** Secure email/password login and registration with proper session management.
* **Cloud Firestore:** Save, bookmark, and track specific fugitives to your personal user profile. All data is persisted securely in Google Cloud Firestore and syncs across sessions.

---

## 🎨 Design Process

Our UI/UX design followed a structured evolution from low-fidelity concepts to the final polished application.

### Figma Prototyping

**LoFi Wireframes** → **HiFi Mockups** → **Final JavaFX Implementation**

Our design lead (Darianne) created all wireframes and high-fidelity mockups in Figma before any FXML code was written. This allowed the team to review, iterate, and approve screen layouts before development began — saving significant rework time.

> 🔗 **Figma Project:** (https://www.figma.com/proto/qlJXVoaGMKlFwUUZfTIQVL/Most-Wanted-Tracker?node-id=1-2&t=pveoo4opSEP8qbwi-1)

### Design Evolution

| Stage | Description |
|-------|-------------|
| **LoFi Wireframes** | Initial hand-drawn and Figma wireframes mapping out screen flow, navigation sidebar, and card layouts |
| **HiFi Mockups** | Polished Figma screens with the dark theme color palette, typography, chart placements, and responsive grid |
| **FXML Implementation** | Final JavaFX screens built from the HiFi designs using FXML + CSS, maintaining visual fidelity to the mockups |

`[INSERT FIGMA SCREENSHOT: LoFi vs HiFi comparison here]`<img width="1400" height="839" alt="Screenshot 2026-05-06 at 11 05 20 AM" src="https://github.com/user-attachments/assets/9e0916da-49cb-4223-9091-d4bb917e7652" /><img width="1409" height="797" alt="Screenshot 2026-05-06 at 11 06 14 AM" src="https://github.com/user-attachments/assets/6d0fa31f-583b-423f-9625-5d26f5be961d" />
<img width="1319" height="789" alt="Screenshot 2026-05-06 at 11 05 44 AM" src="https://github.com/user-attachments/assets/386fd8bb-29aa-4b59-b603-735fd105fa4a" />

<img width="1440" height="811" alt="Screenshot 2026-05-06 at 11 06 26 AM" src="https://github.com/user-attachments/assets/2cb17f5a-0401-4267-8124-f24c0aa888f3" />


---

## 🏗️ Software Architecture

We engineered Fugitive Finder using modern software design principles to ensure the codebase remains maintainable, scalable, and fully decoupled.

### The MVVM Pattern (Model-View-ViewModel)

We adopted the **Model-View-ViewModel** design pattern to completely separate our UI layer from our business logic:

* **Model** — Pure data classes (`WantedPerson`, `AppUser`, `FirebaseUser`, `WantedResponse`) representing the application state. Repository interfaces (`FbiRepository`, `UserRepository`) define data access contracts.
* **View** — FXML layout files paired with lightweight Controller classes. Controllers contain zero business logic — they only bind FXML elements to ViewModel properties and handle navigation events.
* **ViewModel** — The "brain" of each screen. ViewModels handle all data loading, aggregation, sorting, and transformation. They expose JavaFX `ObservableProperties` and `ObservableLists` that the Views automatically bind to, enabling reactive UI updates.

### Application Structure (36 Java Files, 9 FXML Views)

```
src/main/java/org/example/fugitivefinder/
├── model/                  # Data models & repository interfaces
│   ├── WantedPerson.java        — FBI fugitive data model (25+ fields)
│   ├── WantedResponse.java      — API response wrapper
│   ├── AppUser.java             — Application user model
│   ├── FirebaseUser.java        — Firebase auth user model
│   └── repository/              — Data access interfaces
│       ├── FbiRepository.java
│       └── UserRepository.java
│
├── service/                # Business logic & external integrations
│   ├── FbiApiService.java       — HTTP client for FBI API
│   ├── ChartDataService.java    — Chart data aggregation
│   ├── LeaderboardService.java  — Danger score algorithm
│   ├── RegionStatsService.java  — Geographic grouping (56 offices → 4 regions)
│   ├── FirebaseAuthService.java — Firebase Authentication
│   ├── FirestoreService.java    — Firestore CRUD operations
│   └── UserService.java         — User session management
│
├── session/                # Application state
│   └── Session.java             — Singleton session manager
│
├── view/                   # FXML Controllers (View layer)
│   ├── App.java                 — Application entry point
│   ├── SplashController.java
│   ├── LoginPageController.java
│   ├── CreateAccountPageController.java
│   ├── DashboardController.java
│   ├── AnalyticsController.java
│   ├── LeaderboardController.java
│   ├── CriminalProfileController.java
│   ├── MapsViewController.java
│   ├── MapMarkerLayer.java
│   └── UserProfileController.java
│
└── viewModel/              # ViewModels (Logic layer)
    ├── SceneManager.java        — Navigation utility
    ├── DashboardViewModel.java
    ├── AnalyticsViewModel.java
    ├── LeaderboardViewModel.java
    ├── CriminalProfileViewModel.java
    ├── MapsViewModel.java
    ├── LoginViewModel.java
    ├── CreateAccountViewModel.java
    ├── SplashViewModel.java
    └── UserProfileViewModel.java
```

### Data Flow & API Pipeline

```text
┌─────────────────────────┐
│   FBI API (fbi.gov)     │  ← Official FBI Most Wanted database
└───────────┬─────────────┘
            ↓
┌─────────────────────────┐
│  Django Backend Proxy   │  ← Deployed on Render; handles CORS,
│  (Render Cloud)         │     rate limiting, field filtering
└───────────┬─────────────┘
            ↓
┌─────────────────────────┐
│  FbiApiService.java     │  ← Java HttpClient; fetches & deserializes
└───────────┬─────────────┘
            ↓
┌─────────────────────────┐
│  ViewModels             │  ← Data aggregation, sorting, scoring
│  (Analytics, Dashboard, │     via Java Streams & ObservableLists
│   Leaderboard, Maps)    │
└───────────┬─────────────┘
            ↓
┌─────────────────────────┐
│  JavaFX Controllers     │  ← Reactive UI binding via FXML
│  + FXML Views           │
└─────────────────────────┘
```

---

## 💻 Technology Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend UI** | JavaFX 21, FXML, CSS | Desktop application framework & styling |
| **Mapping** | Gluon Maps 2.0 | Interactive map with markers & zoom |
| **Charts** | JavaFX Charts API | BarChart, PieChart for analytics |
| **HTTP Client** | `java.net.http.HttpClient` | REST API communication |
| **JSON Parsing** | Jackson Databind, `org.json` | Model deserialization & raw parsing |
| **Backend Proxy** | Django REST Framework | API proxy deployed on Render |
| **Authentication** | Firebase Authentication | Secure login/registration |
| **Database** | Google Cloud Firestore | Cloud-synced saved targets |
| **Build System** | Apache Maven | Dependency management & builds |
| **Version Control** | Git & GitHub | Collaborative development |
| **Design Tool** | Figma | LoFi/HiFi prototyping |
| **Project Management** | GitHub Projects, SCRUM | Agile sprint planning |

---

## 📸 Application Screenshots

### Dashboard
![Dashboard](docs/screenshots/dashboard.png)

### Crime Analytics
![Crime Analytics](docs/screenshots/analytics.png)

### Interactive Map
![Interactive Map](docs/screenshots/map.png)
### Leaderboard
![Leaderboard](docs/screenshots/leaderboard.png)

### Additional Screens
| Screen | Screenshot |
|--------|------------|
| **User Profile** | ![User Profile](docs/screenshots/profile.png) |

---

## 📋 Project Management & SCRUM Process

### Agile Methodology

We followed the **SCRUM** framework throughout the entire semester, organizing our work into **4 Sprints** across **9 weeks** of active development.

| Sprint | Duration | Focus |
|--------|----------|-------|
| **Sprint 1** (Weeks 1–2) | Project Setup | Figma mockups, API research, Firebase setup, GitHub repo, routing |
| **Sprint 2** (Weeks 3–5) | Core Features | JSON parsing, FXML views, Firestore bookmarks, charts, map integration |
| **Sprint 3** (Weeks 6–7) | Advanced Features | Danger leaderboard, region stats, map markers, backend expansion, navigation |
| **Sprint 4** (Weeks 8–9) | Polish & Presentation | Bug fixes, analytics chart rendering, code cleanup, documentation, final testing |

### Sprint Ceremonies

* **Sprint Planning:** At the start of each sprint, the Project Manager (Sahil) assigned tasks based on each member's vertical slice (see Team Contributions below).
* **Weekly Standups:** Bi-weekly SCRUM video check-ins where each member reported what they completed, what they're working on, and any blockers.
* **Sprint Reviews:** End-of-sprint demos where completed features were shown to the team and merged into `main`.
* **Retrospectives:** After each sprint, the team discussed what went well and what could be improved for the next cycle.

### Tools Used

| Tool | Purpose |
|------|---------|
| **GitHub** | Version control, feature branches, pull requests, code reviews |
| **GitHub Projects** | Task tracking and sprint board |
| **Figma** | UI/UX wireframing and prototyping |
| **Render** | Cloud deployment for Django backend proxy |
| **Firebase Console** | Auth and Firestore database management |
| **IntelliJ IDEA** | Primary IDE for Java/JavaFX development |
| **Maven** | Build automation and dependency management |

### Branch Strategy

We used a **feature-branch workflow** where each developer worked on their own branch and submitted **Pull Requests** for code review before merging to `main`:

* `sahil-sprint3`, `sahil-sprint3-week7`, `sahil-sprint4-week8`, `sahil-charts`
* `Ahmaed's-Branch`
* `Armaan's-Branch`, `backend_integration_Armaan`
* `Derek's-Branch`, `derek`
* `darianne_1`, `darianne_2`, `darianne_4`, `5_darianne`

> **Total Commits:** 255+ across all branches  
> **Pull Requests:** 118+ merged PRs with code review

### Full Sprint Plan

Our detailed week-by-week sprint breakdown with task assignments is documented in [`REF/Sprint_Plan.md`](REF/Sprint_Plan.md).

---

## 👥 The Team & Contributions

This project was a collaborative effort by our agile development team. Each member took ownership of critical vertical slices of the application.

| Member | Role & Key Contributions |
|--------|--------------------------|
| **Sahil Kamal** | **Project Manager & Analytics Lead**<br>Sprint planning & SCRUM facilitation, MVVM architecture design, `ChartDataService`, `LeaderboardService`, Danger Score algorithm, `RegionStatsService`, `AnalyticsViewModel` & `AnalyticsController`, `LeaderboardViewModel` & `LeaderboardController`, navigation sidebar integration, dependency conflict resolution (`javafx-base` version fix), code cleanup & dead code removal, `README.md` documentation, Sprint Plan. |
| **Ahmaed Thomas** | **Map & Navigation Lead**<br>`MapMarkerLayer`, `MapsViewController`, `MapsViewModel`, `offices.json` coordinate data integration, Gluon Maps marker plotting, zoom in/out/reset controls, `SceneManager` scene routing, and sidebar navigation wiring. |
| **Derek Mendez** | **API & Data Lead**<br>`FbiApiService` HTTP client, raw JSON parsing & deserialization, `WantedPerson` data model (25+ fields), `WantedResponse` wrapper, sort/filter logic for dashboard, image loading proxy, and API `pageSize` tuning. |
| **Armaan Arora** | **Backend & Cloud Lead**<br>Django REST Framework backend deployed on Render, FBI API proxy endpoint, Firebase Authentication integration (`FirebaseAuthService`), Cloud Firestore CRUD operations (`FirestoreService`), `UserService` session management, saved target persistence, and backend field expansion (race, sex, 100-record support). |
| **Darianne Ramos** | **UI/UX Lead**<br>Figma LoFi wireframes & HiFi mockups, all FXML layout files, custom CSS dark theme styling, `CriminalProfileController` & view, `DashboardController` card layout, dashboard filter dropdowns, and UI consistency reviews. |

---

## 🚀 Setup & Run Instructions

### Prerequisites
- **Java 21+** (JDK) — [Download](https://jdk.java.net/21/)
- **Internet connection** — The app fetches live data from the FBI API
- Maven is included via the `mvnw` wrapper (no separate install needed)

### Quick Start

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

3. **Explore** — Create an account on the splash screen, then navigate using the sidebar to access the Dashboard, Analytics, Map, Leaderboard, and Profile screens.

### API Details

| Property | Value |
|----------|-------|
| **Backend Proxy URL** | `https://fbi-backend-wilt.onrender.com/api/wanted/` |
| **Source API** | `https://api.fbi.gov/wanted/v1/list` |
| **Records Fetched** | 100 fugitive profiles with full metadata |
| **Response Format** | JSON |

---

<div align="center">
  <p><em>Built with ☕ Java, 🔥 Firebase, and 🛡️ FBI data by Team Fugitive Finder</em></p>
  <p><em>CSC325 — Software Engineering — Farmingdale State College — Spring 2026</em></p>
</div>
