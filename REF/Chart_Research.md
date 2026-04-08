# Chart Type Research & Selection — FugitiveFinder Analytics

> **Author:** Sahil Kamal  
> **Date:** April 8, 2026  
> **Purpose:** Document the chart types selected for visualizing FBI Most Wanted API data

---

## 1. Overview

The FBI Most Wanted API provides rich categorical data that can be visualized to give users insight into crime trends, demographic distributions, and geographic patterns. This document outlines the chart types chosen for each data dimension and the rationale behind each selection.

### Available JavaFX Chart Classes

| Class | Package | Best For |
|-------|---------|----------|
| `BarChart<X,Y>` | `javafx.scene.chart` | Comparing quantities across discrete categories |
| `PieChart` | `javafx.scene.chart` | Showing proportional composition of a whole |
| `LineChart<X,Y>` | `javafx.scene.chart` | Trends over time (continuous data) |
| `AreaChart<X,Y>` | `javafx.scene.chart` | Cumulative trends |
| `StackedBarChart<X,Y>` | `javafx.scene.chart` | Multi-dimensional categorical comparisons |
| `ScatterChart<X,Y>` | `javafx.scene.chart` | Correlation between two numeric variables |

---

## 2. Selected Visualizations

### Chart 1: Crimes by Category (Bar Chart)

**Data Source:** `subjects` field from API response  
**Chart Type:** `BarChart<String, Number>`  
**Orientation:** Horizontal bars  

**Rationale:**
- The `subjects` field contains 25 distinct crime categories
- Bar charts are ideal for comparing frequencies across many discrete categories
- Horizontal orientation allows long category names to be readable
- Categories are sorted by frequency (descending) for easy scanning

**Data Processing:**
```
For each WantedPerson:
    For each subject in person.subjects:
        Increment count for that subject
Sort by count descending, display top 10
```

**Expected Output:**
```
ViCAP Missing Persons        ████████████████████ 62
Cyber's Most Wanted           ███████████████ 35
Seeking Information           ████████████ 29
Additional Violent Crimes     ████████ 19
Criminal Enterprise           ██████ 16
Counterintelligence           ██████ 16
...
```

---

### Chart 2: Cases by Field Office (Pie Chart)

**Data Source:** `field_offices` field from API response  
**Chart Type:** `PieChart`  

**Rationale:**
- Shows proportional distribution of cases across FBI offices
- Highlights which offices handle the most cases
- Top 10 offices shown individually, rest grouped as "Other"
- Pie chart works well here because we're showing composition of a whole

**Data Processing:**
```
For each WantedPerson:
    For each office in person.field_offices:
        Increment count for that office
Sort by count descending
Show top 10, aggregate rest as "Other"
```

---

### Chart 3: Fugitives by Race/Ethnicity (Bar Chart)

**Data Source:** `race` field from API response  
**Chart Type:** `BarChart<String, Number>`  
**Orientation:** Vertical bars  

**Rationale:**
- Only 6 distinct categories (White, Unknown, Hispanic, Black, Asian, Native)
- Vertical bar chart fits well for small category sets
- Provides demographic insight into the wanted population
- Clean visualization since all categories can be displayed

**Data Processing:**
```
For each WantedPerson:
    race = person.race or "Unknown"
    Capitalize race label
    Increment count
```

---

### Chart 4: Sex Distribution (Pie Chart)

**Data Source:** `sex` field from API response  
**Chart Type:** `PieChart`  

**Rationale:**
- Only 3 categories (Male, Female, Unknown)
- Pie chart perfectly conveys the proportional breakdown
- Simple and immediately readable

---

### Chart 5: Poster Classification (Bar Chart)

**Data Source:** `poster_classification` field from API response  
**Chart Type:** `BarChart<String, Number>`  

**Rationale:**
- 5 categories: default, missing, information, ten, law-enforcement-assistance
- Shows the types of wanted notices the FBI issues
- Helps users understand the composition of the FBI's wanted list

---

## 3. Why Not These Alternatives?

### Histogram
- Histograms are for **continuous numerical data** (e.g., age distribution, reward amounts)
- Our primary data (`subjects`, `field_offices`) is **categorical**, not continuous
- Could be added in Sprint 3 if we visualize reward amount distributions

### Choropleth Map
- Excellent for geographic distribution by state/region
- Requires a mapping library and geographic boundary data  
- Ahmaed is handling the map integration separately (GluonHQ Maps)
- Can be added as a Sprint 3 enhancement once map infrastructure is ready

### Line Chart
- Best for time-series data (trends over time)
- The `publication` field could support this, but requires date parsing
- Potential Sprint 3 addition: "New cases published per month"

---

## 4. Design Considerations

### Color Palette (matching app theme)
- Background: `#081120` (dark navy)
- Chart background: `#111827` (dark gray)
- Accent colors for data series:
  - `#4fd1c5` (teal — primary accent from splash screen)
  - `#f59e0b` (amber — used for rewards)
  - `#ef4444` (red — danger/warning)
  - `#8b5cf6` (purple)
  - `#3b82f6` (blue)
  - `#10b981` (green)
  - `#f97316` (orange)
  - `#ec4899` (pink)

### Interactivity
- Tooltips on hover showing exact count and percentage
- Animated transitions when data loads
- Legend displayed for pie charts

### Responsive Layout
- 2×2 grid layout for 4 main charts
- Charts resize with window
- Minimum readable size enforced

---

## 5. Data Volume & Performance

| Metric | Value |
|--------|-------|
| Total FBI records | ~1,147 |
| Records fetched per load | 200 (4 pages × 50) |
| Unique subjects categories | ~25 |
| Unique field offices | ~56 |
| Unique race values | 6 |
| API response time (per page) | ~2-3 seconds |
| Total load time (4 pages) | ~8-12 seconds |

**Optimization:** Data fetching runs on a background thread (`javafx.concurrent.Task`) with a loading indicator, so the UI never freezes.
