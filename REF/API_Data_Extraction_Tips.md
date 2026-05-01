# API Data Extraction Tips

When interacting with the FBI Wanted API, some fields exhibit inconsistent data types that require robust error handling.

## Known Issues
- `images`: Can return an array of JSON Objects or occasionally an array of Strings depending on the specific fugitive entry.
- `fieldOffices`: Uses CamelCase formatting (e.g. `washingtondc`) which requires regex manipulation to convert back to human-readable format.
- `reward_text`: Usually contains long descriptive paragraphs rather than a raw numeric value. Numeric extraction requires pattern matching `\$\d{1,3}(,\d{3})*`.

## Best Practices
Always wrap array parsing in try/catch blocks that specifically intercept `JSONException` or `Throwable` to prevent thread crashes during bulk data loading.
