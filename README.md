# AnyDiary (Any.)

**AnyDiary** คือแอปพลิเคชัน Android สำหรับบันทึกเรื่องราวในชีวิตประจำวัน ติดตามค่าใช้จ่าย และจัดการรายการสิ่งที่ต้องทำ เหมาะสำหรับผู้ใช้ที่ต้องการบันทึก daily memory — โดยเฉพาะประสบการณ์คอนเสิร์ตและศิลปินที่ชื่นชอบ — พร้อมกับจัดการการเงินและตารางงานในที่เดียว

---

## Project Overview

AnyDiary เป็นแอปไดอารี่ส่วนตัวที่รวม 3 ฟีเจอร์หลักเข้าด้วยกันในหน้าจอเดียว:

1. **Daily Memory (ไดอารี่)** — เขียนโพสต์เกี่ยวกับเรื่องราวประจำวัน แท็กสมาชิก (เพื่อน, ศิลปิน) แนบรูปภาพ และดูย้อนหลังบน Calendar
2. **Expense (ค่าใช้จ่าย)** — บันทึกการใช้จ่ายพร้อมชื่อรายการ จำนวนเงิน และเลือกสมาชิกหลายคน
3. **Todo / Remember List (สิ่งที่ต้องจำ)** — จัดการงานและกำหนดการ พร้อมระบบแจ้งเตือนสำหรับงานที่เลยกำหนด

---

## Feature

### โหมด Daily Memory
- สร้างโพสต์พร้อมข้อความและแนบรูปภาพ
- แท็ก member หลายคนต่อโพสต์ (ศิลปิน, เพื่อน ฯลฯ)
- เลือกรูปภาพจากอุปกรณ์ พร้อมบันทึกลง Internal Storage (รูปไม่หายแม้รีสตาร์ทแอป)
- แสดงเป็น Feed แบบ scrollable
- แถว **⭐ Favorites** — แสดงสมาชิกที่ถูกกดชื่นชอบเพื่อเข้าถึงอย่างรวดเร็ว

### โหมด Expense (ค่าใช้จ่าย)
- สลับระหว่างโหมด Daily Memory และ Expense บนหน้า Home
- บันทึกค่าใช้จ่ายพร้อม Title และ Amout (฿)
- เลือก **สมาชิกหลายคน** (multi-select) สำหรับแต่ละรายจ่าย ใช้ข้อมูลสมาชิกเดียวกับโหมด Diary
- แสดง avatar ซ้อนกันสำหรับสมาชิกหลายคน พร้อมตัวบอกจำนวนเกิน
- การ์ดแสดงชื่อรายการ, จำนวนเงิน, avatar สมาชิก, วันที่ และ badge "Today"
- ลบรายจ่ายได้ด้วยการแตะปุ่มลบ

### Calendar View
- ปฎิทินรายเดือนแบบ interactive พร้อมปุ่มเลื่อนเดือน
- วันที่มีโพสต์จะแสดงไอคอน ❤️
- แตะวันที่เพื่อดูโพสต์ของวันนั้นด้านล่าง
- กรองโพสต์ตามสมาชิกด้วย chip selector แนวนอน
- แสดง avatar สมาชิกพร้อมดาว favorite

### Todo / Remember List
- สร้างรายการพร้อมชื่อ, รายละเอียด และวันที่กำหนด
- เปลี่ยนสถานะเสร็จสิ้นด้วย checkbox พร้อม animation
- แบ่งหมวดหมู่อัตโนมัติ: **Pending** (ยังไม่เสร็จ) vs **Completed** (เสร็จแล้ว)
- Badge สถานะ: `Today`, `Upcoming`, `Overdue`, `Completed`
- ขีดฆ่าข้อความสำหรับรายการที่เสร็จแล้ว

### Notifications
- ตรวจจับอัตโนมัติสำหรับงานที่เลยกำหนดและงานที่ครบกำหนดวันนี้
- ตัวนับ badge บน tab แจ้งเตือนใน bottom navigation
- ปุ่ม "Mark as Done" ได้โดยตรงจากการ์ดแจ้งเตือน
- แยกสีชัดเจน: แดงสำหรับเลยกำหนด, ฟ้าสำหรับครบกำหนดวันนี้

### Design & UX
- **พื้นหลัง warm cream** พร้อมเงาการ์ดแบบนุ่มนวล
- **Bouncy click animation** สำหรับองค์ประกอบที่กดได้
- ใช้ **Material 3** components พร้อมธีมโทนอุ่นที่กำหนดเอง
- บันทึกรูปภาพลง Internal Storage (อยู่ได้แม้รีสตาร์ทแอป)
- หน้า **Splash Screen** พร้อมโลโก้และปุ่มเริ่มต้นใช้งาน

---

## Technology

| หมวดหมู่ | เทคโนโลยี | รายละเอียด |
|---|---|---|
| **ภาษา** | Kotlin | โค้ดเบส Kotlin 100% |
| **UI Framework** | Jetpack Compose | Declarative UI แบบสมัยใหม่ของ Android |
| **UI Components** | Material 3 (M3) | คอมโพเนนต์และธีม Material Design 3 |
| **โหลดรูปภาพ** | [Coil](https://coil-kt.github.io/coil/) | โหลดรูปแบบ async รองรับ URI/File path |
| **Serialization** | [Gson](https://github.com/google/gson) | แปลงข้อมูลเป็น JSON สำหรับจัดเก็บในเครื่อง |
| **State Management** | `mutableStateListOf` | Reactive state แบบ Compose (trigger recomposition อัตโนมัติ) |
| **จัดเก็บข้อมูล** | SharedPreferences | Key-value storage สำหรับข้อมูล JSON ในเครื่อง |
| **Navigation** | State-based routing | ใช้ `currentPage` index ควบคุม bottom nav |
| **Min SDK** | 30 (Android 11) | ใช้ `java.time.LocalDate` APIs |
| **Architecture** | Singleton State Object | `AppState` เป็น single source of truth |

---

## Project structure

```
app/src/main/java/com/example/anydiaryproject/
├── MainActivity.kt          # จุดเริ่มต้น — เรียก AppState.init, จัดการ Splash → Home
├── SplashScreen.kt          # หน้าจอต้อนรับ พร้อมโลโก้และปุ่ม "Start"
├── HomeScreen.kt            # หน้าหลัก — Daily Memory feed, Expense list + dialog ทั้งหมด
│                            #   └── RobustImage, HomeScreen, NavItem, ModeToggle
│                            #   └── HomeContent, DailyMemoryContent, PostCard
│                            #   └── AddPostDialog, MemberSelectorDialog, AddMemberDialog
│                            #   └── ExpenseContent, ExpenseCard, AddExpenseDialog
│                            #   └── ExpenseMemberSelectorDialog
├── CalendarScreen.kt        # ปฎิทินแบบ interactive + กรองสมาชิก + ดูโพสต์ตามวัน
├── TodolistScreen.kt        # รายการ Todo แบ่ง Pending/Completed + dialog เพิ่มรายการ
├── NotificationScreen.kt    # การ์ดแจ้งเตือนสำหรับงานเลยกำหนด + ปุ่ม done
├── AppState.kt              # ตัวจัดการ state แบบ Singleton — CRUD + SharedPreferences I/O
│                            #   └── LocalDateAdapter (Gson custom serializer)
├── Models.kt                # Data classes: Member, Post, Expense, Todo
├── UIUtils.kt               # ระบบออกแบบ: ชุดสี, เงา, bouncyClick, AppLogo
└── ui/
    └── theme/               # การตั้งค่าธีม Material (auto-generated)
```

---

## Design System

### Color Palette

| Token | Hex | การใช้งาน |
|---|---|---|
| `BgWarm` | `#FFFDF6` | พื้นหลังแอป (warm cream) |
| `CardWhite` | `#FFFFFF` | พื้นผิวการ์ด |
| `BrownDark` | `#39231A` | ปุ่มหลัก, nav ที่ active |
| `BrownLight` | `#C8B6A6` | สีรองแบบ accent |
| `BlueSoft` | `#E6F0FF` | พื้นหลัง avatar |
| `BlueBright` | `#5581C3` | Calendar selected, badge |
| `StatusRed` | `#FF98B9` | Pink accent, badge เลยกำหนด |
| `StatusGreen` | `#90C290` | สถานะสำเร็จ |
| `FavoriteStar` | `#FFD54F` | ดาวทอง สำหรับ favorites |
| `ExpenseAmountColor` | `#2D2016` | จำนวนเงินในการ์ดค่าใช้จ่าย |
| `PastelPeach` | `#FDE8DC` | พื้นหลังวงกลม avatar (Expense) |
| `PastelMint` | `#D4EDDA` | Mint accent |
| `PastelLavender` | `#E8DEF8` | Lavender accent (Expense member chip) |
| `PastelYellow` | `#FFF3CD` | Yellow accent |
| `TextDark` | `#2A2A2A` | ข้อความหลัก |
| `TextGrey` | `#8C8C8C` | ข้อความรอง |
| `FieldBg` | `#F5F2EC` | พื้นหลังช่อง input |

---

## Data Models

### Member (สมาชิก)
```kotlin
data class Member(
    val id: Int,
    val name: String,
    val imageUri: String? = null,    // URI ภาพโปรไฟล์ใน internal storage
    val isFavorite: Boolean = false  // แสดงในแถว ⭐ Favorites
)
```

### Post (โพสต์ / ความทรงจำ)
```kotlin
data class Post(
    val id: Int,
    val memberIds: List<Int>,        // แท็กสมาชิกหลายคน
    val content: String,
    val date: LocalDate,
    val imageUri: String? = null     // URI รูปภาพที่แนบ
)
```

### Expense (ค่าใช้จ่าย)
```kotlin
data class Expense(
    val id: Int,
    val title: String = "",          // ชื่อรายการ เช่น "กินข้าว", "ค่าเดินทาง"
    val amount: Double,              // จำนวนเงิน (฿)
    val memberIds: List<Int> = emptyList(), // เลือกสมาชิกหลายคน (multi-select)
    val date: LocalDate = LocalDate.now()
)
```

### Todo (สิ่งที่ต้องจำ)
```kotlin
data class Todo(
    val id: Int,
    val title: String,
    val detail: String = "",
    val date: LocalDate = LocalDate.now(),  // วันที่กำหนด
    val isDone: Boolean = false             // สถานะเสร็จสิ้น
)
```

---

## Documentation

### App Architecture

```
┌──────────────────────────────────────────────────┐
│                  MainActivity                     │
│      - เริ่มต้น AppState.init(context)            │
│      - จัดการ SplashScreen → HomeScreen           │
└──────────┬───────────────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────────────┐
│               AppState (Singleton)                │
│  ┌────────────────────────────────────────────┐   │
│  │  members: MutableStateList<Member>         │   │
│  │  posts: MutableStateList<Post>             │   │
│  │  todos: MutableStateList<Todo>             │   │
│  │  expenses: MutableStateList<Expense>       │   │
│  └────────────────────────────────────────────┘   │
│                                                   │
│  CRUD Operations:                                 │
│  - addMember / deleteMember / toggleFavorite      │
│  - addPost / deletePost                           │
│  - addExpense / deleteExpense                     │
│  - addTodo / toggleTodo / deleteTodo              │
│  - getDueNotifications()                          │
│                                                   │
│  Persistence: SharedPreferences + Gson            │
│  - loadData() ← อ่านจาก SharedPreferences         │
│  - saveData() ← เขียนลง SharedPreferences         │
│  - LocalDateAdapter สำหรับ serialize LocalDate     │
└──────────────────────────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────────────┐
│                    UI Layer                       │
│                                                   │
│  HomeScreen ───┬── DailyMemoryContent             │
│     (page 0)   │    └── PostCard                  │
│                │    └── AddPostDialog              │
│                │    └── MemberSelectorDialog       │
│                │    └── AddMemberDialog            │
│                │                                   │
│                └── ExpenseContent                  │
│                     └── ExpenseCard                │
│                     └── AddExpenseDialog           │
│                     └── ExpenseMemberSelectorDialog│
│                                                   │
│  CalendarContent (page 1)                         │
│     └── ปฎิทินรายเดือน + กรองสมาชิก + ดูโพสต์ตามวัน │
│                                                   │
│  TodoContent (page 2)                             │
│     └── TodoRow + AddTodoDialog                   │
│                                                   │
│  NotificationContent (page 3)                     │
│     └── AlertCard                                 │
└──────────────────────────────────────────────────┘
```

### Data Flow

1. **เริ่มต้นแอป**: `MainActivity.onCreate()` → `AppState.init(context)` → `loadData()` อ่านข้อมูลจาก SharedPreferences
2. **สร้างข้อมูลใหม่**: User กดปุ่ม + → แสดง Dialog → กรอกข้อมูล → กด Save → `AppState.addXxx()` → เพิ่มลง `mutableStateListOf` → `saveData()` → Compose recompose อัตโนมัติ
3. **ลบข้อมูล**: User กดปุ่มลบ → `AppState.deleteXxx()` → ลบออกจาก list → `saveData()` → UI อัปเดตอัตโนมัติ
4. **การจัดเก็บรูปภาพ**: เลือกรูปจาก Photo Picker → คัดลอกไฟล์ลง `context.filesDir` → บันทึก URI แบบ `file://...` เพื่อให้ persistence ข้ามเซสชัน

### Member Management

สมาชิก (`Member`) เป็นข้อมูลที่ **ใช้ร่วมกัน** ระหว่างทุกฟีเจอร์:
- **Daily Memory**: เลือกสมาชิกหลายคนเพื่อแท็กในโพสต์
- **Expense**: เลือกสมาชิกหลายคนเพื่อระบุว่าใช้จ่ายกับใคร
- **Calendar**: กรองโพสต์ตามสมาชิก
- **Favorites**: กดดาวเพื่อปักหมุดสมาชิกไว้ด้านบน feed

เมื่อ **ลบสมาชิก** ระบบจะอัปเดตทุก Post และ Expense ที่เกี่ยวข้องโดยอัตโนมัติ (ลบ ID ของสมาชิกออกจาก `memberIds`)

### การเก็บข้อมูล (Data Persistence)

ข้อมูลทั้งหมดจัดเก็บใน **SharedPreferences** ด้วย key ดังนี้:

| Key | ข้อมูล | รูปแบบ |
|---|---|---|
| `members` | รายชื่อสมาชิก | JSON Array ของ `Member` |
| `posts` | โพสต์ทั้งหมด | JSON Array ของ `Post` |
| `expenses` | ค่าใช้จ่ายทั้งหมด | JSON Array ของ `Expense` |
| `todos` | รายการ Todo ทั้งหมด | JSON Array ของ `Todo` |

- ใช้ **Gson** สำหรับ serialize/deserialize JSON
- ใช้ **LocalDateAdapter** (custom TypeAdapter) เพื่อแปลง `LocalDate` เป็น `String` (format: `yyyy-MM-dd`)
- ข้อมูลจะถูก save ทุกครั้งที่มีการเปลี่ยนแปลง (add, delete, toggle)

### Bottom Navigation

| Tab | หน้าจอ | คอมโพเนนต์ |
|---|---|---|
| 🏠 Home | หน้าหลัก (Daily Memory / Expense) | `HomeContent` |
| 📅 Calendar | ปฎิทินรายเดือน | `CalendarContent` |
| ✅ Todo | รายการสิ่งที่ต้องจำ | `TodoContent` |
| 🔔 Alerts | การแจ้งเตือนงานครบกำหนด | `NotificationContent` |

### Composable Components หลัก

| Component | ไฟล์ | หน้าที่ |
|---|---|---|
| `HomeScreen` | HomeScreen.kt | Scaffold หลัก + bottom nav + FAB |
| `ModeToggle` | HomeScreen.kt | Toggle สลับ Daily Memory ↔ Expense |
| `PostCard` | HomeScreen.kt | แสดงโพสต์พร้อม avatar, เนื้อหา, รูป |
| `ExpenseCard` | HomeScreen.kt | แสดงค่าใช้จ่ายพร้อมชื่อรายการ, จำนวนเงิน, avatar |
| `MemberSelectorDialog` | HomeScreen.kt | เลือกสมาชิกหลายคน (สำหรับ Post) |
| `ExpenseMemberSelectorDialog` | HomeScreen.kt | เลือกสมาชิกหลายคน (สำหรับ Expense) |
| `AddMemberDialog` | HomeScreen.kt | เพิ่มสมาชิกใหม่ + เลือกรูปโปรไฟล์ |
| `CalendarContent` | CalendarScreen.kt | ปฎิทิน + กรองสมาชิก + ดูโพสต์ |
| `TodoContent` | TodolistScreen.kt | รายการ Todo + TodoRow |
| `NotificationContent` | NotificationScreen.kt | การ์ดแจ้งเตือน overdue/today |
| `SplashScreen` | SplashScreen.kt | หน้าจอเริ่มต้นพร้อมโลโก้ |
| `AppLogo` | UIUtils.kt | โลโก้แอปด้านบน |

---

## เริ่มต้นใช้งาน

### สิ่งที่ต้องมี
- **Android Studio** (Arctic Fox ขึ้นไป)
- **JDK 17** หรือสูงกว่า
- อุปกรณ์ Android หรือ Emulator ที่ใช้ **API 30+** (Android 11)

### ขั้นตอนการติดตั้ง
1. Clone repository:
   ```bash
   git clone <repository-url>
   ```
2. เปิดโปรเจ็กต์ใน **Android Studio**
3. รอ Gradle sync ให้เสร็จสมบูรณ์
4. รันโมดูล `app` บน emulator หรืออุปกรณ์จริง
5. แตะปุ่ม **ลูกศร (→)** บน Splash Screen เพื่อเริ่มใช้งาน!

### Dependencies (ใน `build.gradle.kts`)
- `androidx.compose.*` — Jetpack Compose UI
- `androidx.material3:material3` — Material 3 components
- `androidx.compose.material:material-icons-extended` — ไอคอนเพิ่มเติม
- `io.coil-kt:coil-compose` — โหลดรูปภาพ
- `com.google.code.gson:gson` — JSON serialization
- `androidx.navigation:navigation-compose` — Navigation (import ไว้แต่ routing เป็นแบบ state-based)
- `androidx.activity:activity-compose` — Activity integration กับ Compose

---

## Figma Design

Link: [Figma — Mobile Application Design](https://www.figma.com/design/YIdboCghRU0kNstAchYMrE/Mobile-Application?node-id=0-1&t=la0BPLjHZdavx0aE-1)

<img width="1405" height="505" alt="image" src="https://github.com/user-attachments/assets/a4a3f59e-4ad3-476d-bafc-aa71d62e5813" />
<img width="956" height="505" alt="image" src="https://github.com/user-attachments/assets/c022d26d-47d0-4e96-8efb-3351effa3426" />
<img width="1142" height="496" alt="image" src="https://github.com/user-attachments/assets/51d6919f-aac0-424f-b126-70456aa99921" />
