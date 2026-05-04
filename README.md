<div align="center">
  <img src="https://upload.wikimedia.org/wikipedia/commons/d/d3/Seal_of_the_Federal_Bureau_of_Investigation.svg" alt="FBI Seal" width="120"/>
  <h1>Fugitive Finder</h1>
  <p><strong>A modern, data-driven intelligence dashboard for tracking the FBI's Most Wanted.</strong></p>
  <p><i>Developed for Software Engineering — Farmingdale State College</i></p>
</div>

---

## 🎯 Project Overview

**Fugitive Finder** is a JavaFX-based desktop application designed to pull, process, and analyze live data directly from the Federal Bureau of Investigation's Most Wanted API. 

Our goal was to build a highly responsive, scalable, and visually engaging application that not only displays data but provides **meaningful analytics** and **geographic intelligence** on fugitive patterns across the United States.

---

## ✨ Key Features & Capabilities

### 🔍 1. Live Intelligence Dashboard
* **Dynamic Grid:** Browse 100+ fugitive profiles loaded dynamically from our backend proxy.
* **Smart Filtering:** Sort and filter suspects by reward amount, name, or warning level.
* **Deep Profiles:** Click any card to view a comprehensive dossier including aliases, physical characteristics, associated field offices, and known crimes.

### 📊 2. Advanced Crime Analytics
* **Data Visualization:** Four interactive charts break down the current Most Wanted list by:
  * Top Crime Categories (Bar Chart)
  * Field Office Jurisdiction (Pie Chart)
  * Race / Ethnicity Demographics (Bar Chart)
  * Sex Distribution (Pie Chart)
* **Real-time Processing:** Data is aggregated and sorted in real-time using Java Streams.

### 🗺️ 3. Geographic Tracking & Interactive Map
* **Gluon Maps Integration:** A fully interactive map plotting all 56 FBI Field Offices.
* **Region Statistics:** A detailed breakdown of crime concentration across 4 major US regions (Northeast, South, Midwest, West).

### 🏆 4. The "Danger Score" Leaderboard
* **Custom Algorithm:** We developed a proprietary 0-100 Danger Score algorithm that evaluates fugitives based on:
  * Reward amount
  * Warning classifications (e.g., "Armed and Dangerous")
  * Crime severity (e.g., Murder, Terrorism, Cybercrime)
* Ranked leaderboard identifies the most critical threats at a glance.

### 🔐 5. Secure User Accounts (Cloud Synced)
* **Firebase Authentication:** Secure login and registration.
* **Cloud Firestore:** Save, bookmark, and track specific fugitives to your personal user profile, synced securely to the cloud.

---

## 🏗️ Software Architecture

We engineered Fugitive Finder using modern software design principles to ensure the codebase remains maintainable, scalable, and decoupled.

### The MVVM Pattern (Model-View-ViewModel)
We completely decoupled our UI from our business logic:
* **Model:** Data classes (`WantedPerson`, `AppUser`) representing the raw state.
* **View:** FXML layouts and lightweight Controllers that strictly handle UI binding.
* **ViewModel:** The "brain" of the screens. Handles data loading, state management, and exposes JavaFX `ObservableProperties` that the Views bind to.

### Data Flow & API Pipeline
```text
[ FBI API (fbi.gov) ] 
       ↓ 
[ Django Backend Proxy (Render) ]  <-- Bypasses strict CORS & Rate Limits
       ↓ 
[ FbiApiService (Java HTTP Client) ] 
       ↓ 
[ ViewModels (Data Aggregation & Sorting) ] 
       ↓ 
[ JavaFX Controllers (UI Rendering) ]
```

---

## 💻 Technology Stack

| Layer | Technology Used |
|-------|----------------|
| **Frontend UI** | JavaFX 21, FXML, CSS |
| **Mapping Engine** | Gluon Maps |
| **Data Visualization** | JavaFX Charts API |
| **HTTP Client** | `java.net.http.HttpClient` |
| **JSON Parsing** | Jackson Databind, `org.json` |
| **Backend / Proxy** | Django REST Framework (Deployed on Render) |
| **Auth & Database**| Firebase Authentication, Cloud Firestore |
| **Build Tool** | Maven (`pom.xml`) |

---

## 📸 Application Demo

| Screen | Screenshot |
|--------|------------|
| **Dashboard** | `[screenshot]` |
| **Analytics** | `[screenshot]` |
| **Leaderboard** | `[screenshot]` |
| **Criminal Profile** | `[screenshot]` |
| **Map View** | `[screenshot]` |
| **User Profile** | `[screenshot]` |

---

## 👥 The Team & Contributions

This project was a collaborative effort by our agile development team. Each member took ownership of critical vertical slices of the application.

| Member | Role & Key Contributions |
|--------|--------------------------|
| **Sahil Kamal** | **Project Manager & Analytics Lead**<br>Sprint planning, MVP architecture, `ChartDataService`, `LeaderboardService`, Danger Score algorithm, Region Stats parsing, Analytics & Leaderboard UI/UX, Navigation integration, Documentation. |
| **Ahmaed Thomas** | **Map & Navigation Lead**<br>`MapController`, `MapsViewController`, `offices.json` integration, coordinate mapping, zoom controls, and core scene routing. |
| **Derek Mendez** | **API & Data Lead**<br>`FbiApiService`, raw JSON parsing, `WantedPerson` data modeling, sort/filter logic, and image loading proxy. |
| **Armaan Arora** | **Backend & Cloud Lead**<br>Django backend deployment on Render, Firebase Auth integration, Firestore CRUD operations, and saved target persistence. |
| **Darianne Ramos** | **UI/UX Lead**<br>FXML layouts, Figma mockups, Custom CSS styling, Criminal Profile design, Dashboard layout, and filter dropdowns. |

---

## 🚀 Setup & Run Instructions

*For grading and local testing purposes.*

1. **Clone the repository**
   ```bash
   git clone https://github.com/sahilkamalny/fugitive-finder.git
   cd fugitive-finder
   ```

2. **Run the application (Java 21+ Required)**
   ```bash
   chmod +x mvnw
   ./mvnw clean javafx:run
   ```

3. **Explore** — Create an account on the splash screen to gain access to the secure intelligence database!
