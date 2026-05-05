# UI Standardization Notes

This document captures notes and findings regarding the synchronization of UI components across JavaFX screens.

## Challenges
- Maintaining identical heights for `BorderPane` `<top>` regions.
- Handling variable content sizes inside header HBoxes.
- Standardizing colors for Chart elements against dark themes.

## Solutions
- Apply fixed `prefHeight`, `minHeight`, and `maxHeight` properties to header regions.
- Use global CSS classes (e.g. `.chart-title`, `.axis-label`) for centralized font color control.
