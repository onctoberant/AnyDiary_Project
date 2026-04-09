# AnyDiary (Any.)

**AnyDiary** is a minimalist, warm-toned Android application for capturing daily life moments, tracking expenses, and managing personal to-do lists. Designed with a **"Soft & Cute"** aesthetic, it targets users who want to journal memories — especially concert and artist experiences — while keeping finances and schedules in one cozy place.

---

## Project Overview

AnyDiary serves as a comprehensive personal diary application that combines three core functionalities into a single, beautifully designed interface:

1. **Daily Memory (Diary)** — Write posts about daily experiences, tag relevant members (friends, artists), attach photos, and browse them on a calendar.
2. **Expense Tracking** — Log spending amounts linked to specific members, with a clean card-based list view.
3. **Todo / Remember List** — Manage tasks and schedules with due-date awareness, status tracking, and overdue alert notifications.

---

## Features

### Daily Memory Mode
- Create posts with rich text content and image attachments
- Tag multiple **Members** (artists, friends, etc.) per post
- Photo picker with persistent internal storage (images never disappear)
- Feed-style scrollable list of memory cards
- **Favorite Members** row — quick-access strip for starred members

### Expense Mode
- Toggle between Daily Memory and Expense modes on the Home screen
- Log expenses with amount (฿) and optional member association  
- Single-member selector for expenses (shared with Diary members)
- Clean card layout showing amount, member avatar, date, and "Today" badge
- Delete expenses with one tap

### Calendar View
- Interactive monthly calendar with navigation arrows
- Dates containing posts are marked with heart icons
- Tap any date to view that day's memory posts below
- Filter posts by member using horizontal chip selectors
- Member avatars with favorite star indicators

### Todo / Remember List
- Create tasks with title, detail text, and due date
- Toggle completion status with animated checkbox
- Automatic categorization: **Pending** vs **Completed** sections
- Status badges: `Today`, `Upcoming`, `Overdue`, `Completed`
- Strikethrough styling for completed items

### Smart Notifications
- Automatic detection of overdue and due-today tasks
- Badge counter on the Alerts tab in the bottom navigation
- Quick "Mark as Done" action directly from the alert card
- Visual differentiation: red for overdue, blue for due-today

### Design & UX
- **Warm cream background** with soft card shadows
- **Bouncy click animations** for interactive elements
- **Material 3** components with custom warm-toned theming
- Persistent internal image storage (survives app restarts)
- Branded **Splash Screen** with tap-to-start interaction

---

## Tech Stack

| Category | Technology | Details |
|---|---|---|
| **Language** | Kotlin | 100% Kotlin codebase |
| **UI Framework** | Jetpack Compose | Declarative, modern Android UI |
| **UI Components** | Material 3 (M3) | Material Design 3 components and theming |
| **Image Loading** | [Coil](https://coil-kt.github.io/coil/) | Async image loading with URI/File path support |
| **Serialization** | [Gson](https://github.com/google/gson) | JSON serialization for local data persistence |
| **State Management** | `mutableStateListOf` | Compose-native reactive state (triggers recomposition) |
| **Persistence** | SharedPreferences | Key-value local storage for JSON data |
| **Navigation** | In-app state-based | `currentPage` index drives bottom nav routing |
| **Min SDK** | 30 (Android 11) | Uses `java.time.LocalDate` APIs |
| **Architecture** | Singleton State Object | `AppState` as single source of truth |

---

## Project Structure

```
app/src/main/java/com/example/anydiaryproject/
├── MainActivity.kt          # Entry point — initializes AppState, manages Splash→Home routing
├── SplashScreen.kt          # Branded welcome screen with logo and "Start" button
├── HomeScreen.kt            # Main content hub — Daily Memory feed, Expense list, all dialogs
│                            #   └── RobustImage, HomeScreen, NavItem, ModeToggle
│                            #   └── HomeContent, DailyMemoryContent, PostCard
│                            #   └── AddPostDialog, MemberSelectorDialog, AddMemberDialog
│                            #   └── ExpenseContent, ExpenseCard, AddExpenseDialog
│                            #   └── ExpenseMemberSelectorDialog
├── CalendarScreen.kt        # Interactive calendar with member filtering + day post viewer
├── TodolistScreen.kt        # Todo list with Pending/Completed sections + Add Todo dialog
├── NotificationScreen.kt    # Overdue & due-today alert cards with quick-done action
├── AppState.kt              # Singleton state manager — CRUD operations + SharedPreferences I/O
│                            #   └── LocalDateAdapter (Gson custom serializer)
├── Models.kt                # Data classes: Member, Post, Expense, Todo
├── UIUtils.kt               # Design system: color palette, shadows, bouncyClick, AppLogo
└── ui/
    └── theme/               # Material theme configuration (auto-generated)
```

---

## Design System

### Color Palette

| Token | Hex | Usage |
|---|---|---|
| `BgWarm` | `#FFFDF6` | App background (warm cream) |
| `CardWhite` | `#FFFFFF` | Card surfaces |
| `BrownDark` | `#39231A` | Primary buttons, nav active |
| `BrownLight` | `#C8B6A6` | Secondary accents |
| `BlueSoft` | `#E6F0FF` | Avatar backgrounds |
| `BlueBright` | `#5581C3` | Calendar selected, badges |
| `StatusRed` | `#FF98B9` | Pink accent, overdue badges |
| `StatusGreen` | `#90C290` | Success states |
| `FavoriteStar` | `#FFD54F` | Gold star for favorites |
| `TextDark` | `#2A2A2A` | Primary text |
| `TextGrey` | `#8C8C8C` | Secondary text |
| `FieldBg` | `#F5F2EC` | Input field backgrounds |

### Design Principles
- **Rounded Corners**: 20dp for cards, 24dp for dialogs, 12dp for buttons
- **Soft Shadows**: `cardShadow()` with BrownDark-tinted ambient/spot colors
- **Bouncy Clicks**: 0.96x scale-down animation on press (80ms tween)
- **Typography**: Bold headers (16sp), medium body (14sp), small captions (11sp)

---

## Data Models

### Member
```kotlin
data class Member(
    val id: Int,
    val name: String,
    val imageUri: String? = null,    // Internal file URI for profile picture
    val isFavorite: Boolean = false  // Shown in ⭐ Favorites row
)
```

### Post (Daily Memory)
```kotlin
data class Post(
    val id: Int,
    val memberIds: List<Int>,        // Multi-member tagging
    val content: String,
    val date: LocalDate,
    val imageUri: String? = null     // Attached photo URI
)
```

### Expense
```kotlin
data class Expense(
    val id: Int,
    val amount: Double,
    val memberId: Int? = null,       // Single member (null = "personal")
    val date: LocalDate = LocalDate.now()
)
```

### Todo
```kotlin
data class Todo(
    val id: Int,
    val title: String,
    val detail: String = "",
    val date: LocalDate = LocalDate.now(),
    val isDone: Boolean = false
)
```

---

## Getting Started

### Prerequisites
- **Android Studio** (Arctic Fox or newer recommended)
- **JDK 17** or higher
- Android device or emulator with **API 30+** (Android 11)

### Setup
1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```
2. Open the project in **Android Studio**.
3. Wait for Gradle sync to complete.
4. Run the `app` module on an emulator or physical device.
5. Tap the **arrow (→)** on the Splash Screen to begin!

### Dependencies (in `build.gradle.kts`)
- `androidx.compose.*` — Jetpack Compose UI
- `androidx.material3:material3` — Material 3 components
- `io.coil-kt:coil-compose` — Image loading
- `com.google.code.gson:gson` — JSON serialization
- `androidx.navigation:navigation-compose` — Navigation (imported but routing is state-based)

---

## Figma Design 

Link: [Figma — Mobile Application Design](https://www.figma.com/design/YIdboCghRU0kNstAchYMrE/Mobile-Application?node-id=0-1&t=la0BPLjHZdavx0aE-1)

<img width="1405" height="505" alt="image" src="https://github.com/user-attachments/assets/a4a3f59e-4ad3-476d-bafc-aa71d62e5813" />
<img width="956" height="505" alt="image" src="https://github.com/user-attachments/assets/c022d26d-47d0-4e96-8efb-3351effa3426" />
<img width="1142" height="496" alt="image" src="https://github.com/user-attachments/assets/51d6919f-aac0-424f-b126-70456aa99921" />

