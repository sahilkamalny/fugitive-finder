# FBI Most Wanted API — Endpoint Reference

> **Author:** Sahil Kamal  
> **Date:** April 8, 2026  
> **Purpose:** Team reference for all API endpoints used in FugitiveFinder

---

## 1. Base URL

```
https://api.fbi.gov/wanted/v1/list
```

- **Authentication:** None required (public API)
- **Rate Limiting:** No documented limit, but best practice is to cache responses and avoid excessive polling
- **Response Format:** JSON

---

## 2. Query Parameters

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `page` | int | Page number for pagination (1-indexed) | `?page=1` |
| `pageSize` | int | Records per page (default: 20, max: 50) | `?pageSize=50` |
| `sort_on` | string | Field to sort by | `?sort_on=modified` |
| `sort_order` | string | Sort direction: `asc` or `desc` | `?sort_order=desc` |
| `title` | string | Filter by person's name | `?title=John` |
| `field_offices` | string | Filter by FBI field office (lowercase) | `?field_offices=newyork` |
| `person_classification` | string | Filter: `Main` or `Victim` | `?person_classification=Main` |
| `poster_classification` | string | Filter by poster type | `?poster_classification=ten` |
| `status` | string | Filter by status | `?status=captured` |

### Example Requests

```
# Get first 50 wanted persons
GET https://api.fbi.gov/wanted/v1/list?page=1&pageSize=50

# Get Ten Most Wanted
GET https://api.fbi.gov/wanted/v1/list?poster_classification=ten

# Get cases from New York field office
GET https://api.fbi.gov/wanted/v1/list?field_offices=newyork&pageSize=50

# Get page 3 sorted by most recently modified
GET https://api.fbi.gov/wanted/v1/list?page=3&pageSize=50&sort_on=modified&sort_order=desc
```

---

## 3. Response Structure

### Top-Level Response

```json
{
  "total": 1147,
  "page": 1,
  "items": [ ... ]
}
```

| Field | Type | Description |
|-------|------|-------------|
| `total` | int | Total number of records matching the query |
| `page` | int | Current page number |
| `items` | array | Array of Wanted Person objects |

---

## 4. Wanted Person Object — Complete Field Reference

### Identity Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `uid` | string | Unique identifier | `"525c64c8a82940e3aaae18c532cc3019"` |
| `title` | string | Full name (uppercase) | `"JOHN DOE"` |
| `aliases` | string[] \| null | Known aliases | `["John Smith", "J. Doe"]` |
| `description` | string \| null | Brief case description | `"Unlawful Flight..."` |
| `sex` | string \| null | Gender | `"Male"`, `"Female"` |
| `race` | string \| null | Race (normalized) | `"white"`, `"black"`, `"hispanic"`, `"asian"`, `"native"`, `"unknown"` |
| `race_raw` | string \| null | Race (original text) | `"White (Hispanic)"` |
| `nationality` | string \| null | Nationality | `"American"` |
| `dates_of_birth_used` | string[] \| null | Known DOBs | `["November 12, 2007"]` |
| `place_of_birth` | string \| null | Birthplace | `"Garland, Texas"` |
| `age_min` | int \| null | Minimum estimated age | `30` |
| `age_max` | int \| null | Maximum estimated age | `35` |
| `age_range` | string \| null | Age range text | `null` (often empty) |

### Physical Description Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `hair` | string \| null | Hair color (normalized) | `"brown"` |
| `hair_raw` | string \| null | Hair color (original) | `"Brown, Curly"` |
| `eyes` | string \| null | Eye color (normalized) | `"brown"` |
| `eyes_raw` | string \| null | Eye color (original) | `"Brown"` |
| `height_min` | int \| null | Min height in inches | `64` |
| `height_max` | int \| null | Max height in inches | `65` |
| `weight` | string \| null | Weight description | `"130 to 140 pounds"` |
| `weight_min` | int \| null | Min weight in pounds | `130` |
| `weight_max` | int \| null | Max weight in pounds | `140` |
| `build` | string \| null | Body build | `"Medium"` |
| `complexion` | string \| null | Complexion | `"Light"` |
| `scars_and_marks` | string \| null | Identifying marks | `"Tattoo on left arm"` |

### Classification Fields

| Field | Type | Description | Values |
|-------|------|-------------|--------|
| `person_classification` | string | Role in case | `"Main"`, `"Victim"` |
| `poster_classification` | string | FBI poster category | `"default"`, `"missing"`, `"information"`, `"ten"`, `"law-enforcement-assistance"` |
| `status` | string \| null | Current status | `"na"`, `"captured"`, `"recovered"` |
| `warning_message` | string \| null | Danger warning | `"SHOULD BE CONSIDERED ARMED AND DANGEROUS"` |

### Crime & Case Fields

| Field | Type | Description | Example Values |
|-------|------|-------------|----------------|
| `subjects` | string[] \| null | **Crime categories** | See Section 5 below |
| `field_offices` | string[] \| null | Responsible FBI offices | `["newyork", "miami"]` |
| `caution` | string \| null | Caution text (HTML) | `"<p>Wanted for...</p>"` |
| `details` | string \| null | Case details (HTML) | `"<p>Last seen at...</p>"` |
| `remarks` | string \| null | Additional remarks (HTML) | `"<p>Has ties to...</p>"` |

### Reward Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `reward_text` | string \| null | Reward description | `"Up to $50,000"` |
| `reward_min` | int | Minimum reward amount | `0` |
| `reward_max` | int | Maximum reward amount | `0` |

### Media & Links

| Field | Type | Description |
|-------|------|-------------|
| `images` | object[] | Array of image objects with `large`, `thumb`, `original`, `caption` |
| `files` | object[] | PDF downloads with `url` and `name` |
| `url` | string | FBI website profile URL |
| `path` | string | Relative path on FBI site |
| `pathId` | string | Full API path to individual record |

### Location & Geographic Fields

| Field | Type | Description |
|-------|------|-------------|
| `coordinates` | array | GPS coordinates (usually empty) |
| `locations` | string \| null | Known locations |
| `possible_countries` | string \| null | Countries they may be in |
| `possible_states` | string \| null | States they may be in |
| `legat_names` | string \| null | Legal attaché offices |

### Metadata Fields

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| `publication` | string | Date first published | `"2024-10-22T11:00:00"` |
| `modified` | string | Date last modified | `"2026-04-08T13:00:18+00:00"` |
| `ncic` | string \| null | NCIC number | `null` |
| `occupations` | string \| null | Known occupations | `null` |
| `languages` | string \| null | Languages spoken | `null` |

---

## 5. Subjects Taxonomy (Crime Categories)

These are the values found in the `subjects` array, aggregated from 250 live records:

| Subject | Count | Description |
|---------|-------|-------------|
| ViCAP Missing Persons | 62 | Violent Crimes Apprehension Program - missing |
| Cyber's Most Wanted | 35 | Cybercrime fugitives |
| Seeking Information | 29 | FBI seeking public tips |
| Additional Violent Crimes | 19 | Other violent offenses |
| Criminal Enterprise Investigations | 16 | Organized crime / gangs |
| Counterintelligence | 16 | Espionage / foreign agents |
| China Threat | 15 | Chinese espionage cases |
| ViCAP Homicides and Sexual Assaults | 14 | Violent crimes database matches |
| Kidnappings and Missing Persons | 13 | Kidnapping cases |
| ViCAP Unidentified Persons | 11 | Unidentified remains |
| Domestic Terrorism | 10 | Domestic terror cases |
| Indian Country | 7 | Crimes on tribal land |
| ECAP | 7 | Endangered Children Alert |
| Seeking Information - Terrorism | 6 | Terror-related tips |
| Violent Crime - Murders | 5 | Murder fugitives |
| White-Collar Crime | 3 | Financial crimes |
| Navajo | 2 | Navajo Nation cases |
| John Doe | 2 | Unidentified suspects |
| Ten Most Wanted Fugitives | 1 | Top 10 list |
| Crimes Against Children | 1 | Child exploitation |
| Human Trafficking | 1 | Trafficking cases |
| Iran | 1 | Iran-related cases |
| Case of the Week | 1 | Featured case |
| Law Enforcement Assistance | 1 | LEA cooperation |

---

## 6. Field Offices Directory (Top 30)

| Office | Cases | | Office | Cases |
|--------|-------|-|--------|-------|
| newyork | 29 | | kansascity | 3 |
| washingtondc | 11 | | albany | 3 |
| chicago | 9 | | tampa | 2 |
| pittsburgh | 9 | | boston | 2 |
| portland | 8 | | sandiego | 2 |
| omaha | 7 | | indianapolis | 2 |
| miami | 6 | | losangeles | 2 |
| sacramento | 6 | | billings | 1 |
| philadelphia | 5 | | dallas | 1 |
| honolulu | 4 | | sanjuan | 1 |
| atlanta | 4 | | saltlakecity | 1 |
| seattle | 4 | | charlotte | 1 |
| newark | 4 | | oklahomacity | 1 |
| sanfrancisco | 4 | | richmond | 1 |
| houston | 4 | | minneapolis | 1 |

---

## 7. Data Statistics (as of April 2026)

- **Total records:** 1,147
- **Records with reward text:** 78 / 250 sampled (31%)
- **Records with coordinates:** 0 / 250 (GPS data generally not provided)
- **Race distribution:** White (44%), Unknown (25%), Hispanic (15%), Black (8%), Asian (7%), Native (2%)
- **Sex distribution:** Male (57%), Female (26%), Unknown (17%)
- **Person classification:** Main (92%), Victim (8%)
