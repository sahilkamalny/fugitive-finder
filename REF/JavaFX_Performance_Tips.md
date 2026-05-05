# JavaFX Performance & Threading

## UI Thread Rule
All operations that modify JavaFX nodes (e.g. updating labels, adding chart data, swapping scenes) MUST occur on the JavaFX Application Thread. Use `Platform.runLater()` when dispatching updates from background API fetching threads.

## Animations
Avoid enabling animations on complex PieCharts or BarCharts (`animated="false"`) during initial bulk data loads to prevent severe layout thrashing and label clustering.

## Scene Swapping
When navigating between screens, avoid instantiating completely new `Scene` objects. Instead, replace the root node of the existing scene (`currentScene.setRoot(newRoot)`). This prevents OS-level window resets and preserves fullscreen/maximized bounds.
