# FugitiveFinder: Agile/Scrum Sprint Plan

> **Project Manager:** Sahil Kamal  
> **Last Updated:** April 14, 2026  
> **Team:** Sahil, Darianne, Derek, Ahmaed, Armaan

---

## 📋 Product Backlog (User Stories)

| ID | User Story | Status |
|---|---|---|
| US1 | As a user, I can sign up/login so that my data is saved | ✅ Done |
| US2 | As a user, I can search/filter for targets to find specific fugitives | 🔄 In Progress |
| US3 | As a user, I can see crime statistics via graphs and heatmaps | 🔄 In Progress |
| US4 | As a user, I can view detailed info on a criminal to understand their crimes | ✅ Done |
| US5 | As a user, I can bookmark targets to receive updates on their status | 🔄 In Progress |

---

## 🏃 Sprint 1: Foundation & Discovery (Weeks 1-2) ✅ COMPLETE

**Goal:** Establish the technical "Steel Thread" (Frontend → API → Database)

| Task | Assigned To | Status |
|---|---|---|
| UX/UI: Finalize Figma & Project FXML Structure | Darianne | ✅ Done |
| API: FBI API Research & Service Layer Init | Derek | ✅ Done |
| Backend: Firebase Auth & Firestore Setup | Armaan | ✅ Done |
| Data: GitHub Repo & Initial Chart Research | Sahil | ✅ Done |
| Navigation: Scene Manager & Routing Logic | Ahmaed | ✅ Done |

---

## 🏃 Sprint 2: Core Connectivity (Weeks 3-5) ✅ COMPLETE

**Goal:** Build the two biggest features — Dashboard/Charts and Profiles/API integration.

### Feature Group A: Criminal Data & Profiles (US2, US4, US5)

| Task | Assigned To | Status |
|---|---|---|
| JSON Parsing for WantedPerson objects and filtering logic | Derek | ✅ Done |
| CriminalProfile.fxml and Rewards.fxml views | Darianne | ✅ Done |
| Linking "Bookmark" button to Firestore (Watchlist CRUD) | Armaan | ✅ Done |

### Feature Group B: Data Visuals & Mapping (US3)

| Task | Assigned To | Status |
|---|---|---|
| Connecting API data to JavaFX Charts (crimes by category) | Sahil | ✅ Done |
| Integrating Map library and placing markers | Ahmaed | ✅ Done |

### What merged to `main` during Sprint 2:
- PR #83: Darianne's ViewModel classes + all FXML screens
- PR #85: Ahmaed's map + dashboard fixes
- PR #86: Sahil's chart analytics (ChartDataService, analytics.fxml)
- PR #87: Ahmaed's MapController + offices integration

---

## 🏃 Sprint 3: Advanced Features (Weeks 6-7) — CURRENT

**Goal:** Enhance user experience with interactivity, advanced search, and deeper analytics.

### Week 6 (April 14-20) — Core Implementation

| Task | Assigned To | Type | Status |
|---|---|---|---|
| "Most Dangerous" leaderboard — danger score algorithm + ranked table | Sahil | Visuals | ✅ Done |
| Region stats — crime breakdown by US region (NE/South/MW/West) | Sahil | Visuals | ✅ Done |
| Map interactivity — link map markers to field office data from offices.json | Ahmaed | Map | 🔄 In Progress |
| Advanced search — multi-filter by field office | Derek | Logic | 🔄 In Progress |
| Dashboard filtering bar — category/office filter UI | Darianne | UI | 🔄 In Progress |
| Status change alerts — backend notification endpoint | Armaan | Backend | 🔄 In Progress |

### Week 7 (April 21-27) — Integration & Polish

| Task | Assigned To | Type | Status |
|---|---|---|---|
| Leaderboard integration — connect leaderboard to app navigation/sidebar | Sahil | Visuals | ⬜ To Do |
| Connect analytics navigation stubs to SceneManager | Sahil | Integration | ⬜ To Do |
| Map markers → profile view — click marker to open fugitive profile | Ahmaed & Darianne | Collaborative | ⬜ To Do |
| Advanced search — add crime type and reward range filters | Derek | Logic | ⬜ To Do |
| Filter results binding — connect Darianne's filter bar to Derek's search logic | Darianne & Derek | Collaborative | ⬜ To Do |
| Watchlist notifications — connect alerts to JavaFX UI | Armaan | Backend/UI | ⬜ To Do |

---

## 🏃 Sprint 4: Hardening & Handover (Weeks 8-9)

**Goal:** Bug fixing, performance tuning, final polish, and documentation.

### Week 8 (April 28 - May 4) — Stability & Testing

| Task | Assigned To | Type | Status |
|---|---|---|---|
| UI polish — loading spinners, error states, empty state messages | Darianne | UI | ⬜ To Do |
| API caching — prevent redundant API calls across screens | Derek | Performance | ⬜ To Do |
| Security rules — Firestore access rules and input validation | Armaan | Backend | ⬜ To Do |
| Chart verification — validate all chart data against live API | Sahil | Testing | ⬜ To Do |
| Map performance — optimize marker rendering and pan/zoom | Ahmaed | Performance | ⬜ To Do |

### Week 9 (May 5-11) — Documentation & Final Delivery

| Task | Assigned To | Type | Status |
|---|---|---|---|
| Final integration testing — full app walkthrough all screens | Everyone | Testing | ⬜ To Do |
| UML diagram generation — class diagram + sequence diagrams | Sahil | Docs | ⬜ To Do |
| README update — setup instructions, screenshots, architecture | Sahil | Docs | ⬜ To Do |
| Code cleanup — remove dead code, add JavaDoc comments | Derek | Cleanup | ⬜ To Do |
| Final database indexing and backup | Armaan | Backend | ⬜ To Do |
| Navigation audit — verify all sidebar links work across screens | Ahmaed | Testing | ⬜ To Do |
| Final UI review — consistent theming, responsive layout check | Darianne | UI | ⬜ To Do |

---

## 🛠 Kanban Board (Current View — Week 6)

| To-Do (Week 7) | In-Progress (Week 6) | Done (Sprint 2) |
|---|---|---|
| Map → Profile linking | Map marker ↔ offices.json (Ahmaed) | Chart analytics (Sahil) |
| Filter bar → search logic | Dashboard filtering bar (Darianne) | MapController (Ahmaed) |
| Navigation integration | Advanced search filters (Derek) | ViewModel classes (Darianne) |
| Watchlist alerts UI | Backend notification API (Armaan) | JSON parsing (Derek) |
| | **Leaderboard + Region Stats (Sahil) ✅** | Splash/Login/Profile screens |

---

## 📊 Branch Strategy

| Branch | Owner | Purpose |
|---|---|---|
| `main` | Team | Protected — only merged via PR |
| `sahil-sprint3` | Sahil | Sprint 3: Leaderboard + Region Stats |
| `Derek's-branch` | Derek | Advanced search + navigation fixes |
| `darianne_2` | Darianne | Dashboard filtering bar |
| `Ahmaed's-Branch` | Ahmaed | Map interactivity + offices.json |
| `Armaan's-Branch` | Armaan | Django backend + notifications |

> ⚠️ **`backend_integration_Armaan`**: This branch deletes the Java project. DO NOT merge to main. Armaan's Django work should stay in the `fbi_backend/` folder only.
