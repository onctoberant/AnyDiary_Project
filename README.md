# AnyDiary (Any.) 📔✨

**AnyDiary** is a minimalist, warm-toned Android application designed for capturing daily life moments and special memories with artists or at concerts. It features a "Soft & Cute" aesthetic with smooth interactions and persistent storage.

## 🌟 Key Features

- **Daily Memories**: Write down your thoughts, stories, and concert experiences.
- **Image Attachments**: Support for adding photos to your posts via a built-in photo picker.
- **Member Tagging**: Organize your memories by tagging "Members" (Artists, Friends, or Yourself) with custom profile pictures.
- **Calendar View**: A visual roadmap of your memories. Dates with posts are marked with subtle heart icons.
- **To-do List (Remember)**: A simple yet effective way to track things you need to do or artists' schedules. 
- **Interactive Splash Screen**: A branded entry point with a "Tap to Start" interaction.
- **Smart Persistence**: All posts, members, and todos are saved locally using **Gson** and **SharedPreferences**. 
- **Media Safety**: Selected images are copied to the app's internal storage to ensure they never disappear, even after app restarts.

## 🎨 Design Philosophy

The app follows a **Minimal + Cute + Soft** design system:
- **Background**: Warm Cream (`#FFFDF6`)
- **Primary Text/Button**: Dark Brown (`#39231A`)
- **Accents**: Soft Blue (`#E6F0FF`) and Pink (`#FF98B9`)
- **Typography**: Clean hierarchy with bold headers and readable body text.
- **Visuals**: Deep card shadows and rounded corners (24dp) for a premium feel.

## 🛠 Tech Stack

- **Language**: Kotlin
- **Framework**: Jetpack Compose (Modern UI)
- **UI Components**: Material 3 (M3)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/) (with robust URI/Path parsing)
- **Serialization**: [Gson](https://github.com/google/gson) for local data persistence.
- **State Management**: Reactive state using `mutableStateListOf`.

## 📂 Project Structure

- `AppState.kt`: The single source of truth for the app's data and persistence logic.
- `HomeScreen.kt`: Contains the memory feed and member management.
- `CalendarScreen.kt`: Interactive calendar with post filtering.
- `TodolistScreen.kt`: The "Remember" list and task tracking.
- `SplashScreen.kt`: The branded welcome screen.
- `UIUtils.kt`: Centralized design tokens (Colors, Shadows, Common Composables).
- `Models.kt`: Data structures for Posts, Members, and Todos.

## 🚀 Getting Started

1. Clone the repository into **Android Studio**.
2. Sync with Gradle.
3. Run the `app` module on an emulator or physical device (Min SDK 30).
4. Tap the arrow on the Splash Screen to begin your journey!

---
*Created for capturing moments that matter.* 🤎

----
## FIGMA
link : https://www.figma.com/design/YIdboCghRU0kNstAchYMrE/Mobile-Application?node-id=0-1&t=la0BPLjHZdavx0aE-1

<img width="1405" height="505" alt="image" src="https://github.com/user-attachments/assets/a4a3f59e-4ad3-476d-bafc-aa71d62e5813" />
<img width="956" height="505" alt="image" src="https://github.com/user-attachments/assets/c022d26d-47d0-4e96-8efb-3351effa3426" />
<img width="1142" height="496" alt="image" src="https://github.com/user-attachments/assets/51d6919f-aac0-424f-b126-70456aa99921" />


