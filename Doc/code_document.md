# Code Documentation
## Table of Contents

1. [Models.kt — Data Classes](#1-modelskt--data-classes)
2. [UIUtils.kt — Design System & Utilities](#2-uiutilskt--design-system--utilities)
3. [AppState.kt — State Management & Persistence](#3-appstatekt--state-management--persistence)
4. [MainActivity.kt — App Entry Point](#4-mainactivitykt--app-entry-point)
5. [SplashScreen.kt — Welcome Screen](#5-splashscreenkt--welcome-screen)
6. [HomeScreen.kt — Main Content Hub](#6-homescreenkt--main-content-hub)
7. [CalendarScreen.kt — Calendar View](#7-calendarscreenkt--calendar-view)
8. [TodolistScreen.kt — Todo List](#8-todolistscreenkt--todo-list)
9. [NotificationScreen.kt — Alert Notifications](#9-notificationscreenkt--alert-notifications)

---

## 1. Models.kt — Data Classes

ไฟล์นี้เก็บ data class ทั้งหมดที่ใช้ในแอป เป็น "โครงสร้างข้อมูล" หลักของระบบ

| Class | หน้าที่ | Fields |
|---|---|---|
| `Member` | เก็บข้อมูลสมาชิก (ศิลปิน, เพื่อน) | `id`, `name`, `imageUri`, `isFavorite` |
| `Post` | เก็บ memory/diary post | `id`, `memberIds` (หลายคน), `content`, `date`, `imageUri` |
| `Expense` | เก็บรายจ่าย | `id`, `amount`, `memberId` (คนเดียว/null), `date` |
| `Todo` | เก็บ task/to-do item | `id`, `title`, `detail`, `date`, `isDone` |

> **Note**: `Member` ถูกใช้ร่วมกัน (shared) ระหว่างโหมด Daily Memory และ Expense — ข้อมูลสมาชิกเป็น pool เดียวกัน

---

## 2. UIUtils.kt — Design System & Utilities

ไฟล์นี้เป็น **Design System กลาง** ที่เก็บค่าทั้งหมดเกี่ยวกับ สี, เงา, animation, และ component ที่ใช้ร่วม

### Color Palette (ค่าสีทั้งหมดของแอป)

```
Primary Colors:
  BlueSoft   (#E6F0FF) — พื้นหลัง avatar, badge อ่อน
  BlueBright (#5581C3) — ปุ่ม active, calendar selected

Secondary Colors:
  BrownLight (#C8B6A6) — accent สีน้ำตาลอ่อน
  BrownDark  (#39231A) — ปุ่มหลัก, text สำคัญ

Background & Surface:
  BgWarm     (#FFFDF6) — พื้นหลังแอปทั้งหมด (cream อุ่น)
  CardWhite  (#FFFFFF) — พื้นหลัง card
  FieldBg    (#F5F2EC) — พื้นหลัง input field

Text Colors:
  TextDark   (#2A2A2A) — ข้อความหลัก
  TextGrey   (#8C8C8C) — ข้อความรอง
  TextLight  (#CCCCCC) — ข้อความจางมาก, icon ลบ

Status Colors:
  StatusRed  (#FF98B9) — overdue, ลบ
  StatusGreen(#90C290) — สำเร็จ
  FavoriteStar(#FFD54F) — ดาว favorite

Expense-specific:
  ExpenseCardBg    — พื้นหลัง expense card
  ExpenseBadgeBg   — badge "Today"
  PastelPeach/Mint/Lavender/Yellow — สี avatar วงกลม
```

### Utility Functions

| Function/Component | หน้าที่ |
|---|---|
| `Modifier.cardShadow()` | เพิ่มเงาอ่อนๆ ให้ card (BrownDark tint, 8dp default) |
| `Modifier.bouncyClick()` | เพิ่ม press animation (scale 0.96x, 80ms) ให้ clickable |
| `AppLogo()` | แสดงโลโก้แอปตรงกลาง (56dp, วงกลม, มีเงา) |

---

## 3. AppState.kt — State Management & Persistence

ไฟล์นี้เป็น **หัวใจของแอป** — จัดการ state ทั้งหมดและบันทึก/โหลดข้อมูลจาก SharedPreferences

### Class: `LocalDateAdapter`
- **หน้าที่**: แปลง `LocalDate` ↔ JSON string สำหรับ Gson
- **วิธีทำงาน**: serialize เป็น ISO string (`"2026-01-15"`), deserialize กลับเป็น `LocalDate`

### Object: `AppState` (Singleton)

| Property | Type | หน้าที่ |
|---|---|---|
| `members` | `mutableStateListOf<Member>` | รายการสมาชิกทั้งหมด (reactive) |
| `posts` | `mutableStateListOf<Post>` | รายการ post ทั้งหมด (reactive) |
| `todos` | `mutableStateListOf<Todo>` | รายการ todo ทั้งหมด (reactive) |
| `expenses` | `mutableStateListOf<Expense>` | รายการรายจ่ายทั้งหมด (reactive) |
| `nextMemberId/PostId/...` | `Int` | Auto-increment ID counters |
| `prefs` | `SharedPreferences?` | Instance สำหรับอ่าน/เขียนข้อมูล |
| `gson` | `Gson` | Instance + LocalDateAdapter |

| Function | หน้าที่ |
|---|---|
| `init(context)` | เริ่มต้น — สร้าง SharedPreferences instance + โหลดข้อมูล |
| `loadData()` | อ่าน JSON จาก SharedPreferences → แปลงเป็น list → ใส่ใน state |
| `saveData()` | แปลง list เป็น JSON → เขียนลง SharedPreferences |
| `addMember(name, imageUri)` | สร้าง Member ใหม่ → เพิ่มเข้า list → save |
| `deleteMember(member)` | ลบ Member + ลบ reference จาก posts/expenses → save |
| `addPost(...)` | สร้าง Post ใหม่ → เพิ่มที่ตำแหน่ง 0 (ใหม่อยู่บน) → save |
| `deletePost(post)` | ลบ Post → save |
| `addExpense(...)` | สร้าง Expense ใหม่ → เพิ่มที่ตำแหน่ง 0 → save |
| `deleteExpense(expense)` | ลบ Expense → save |
| `addTodo(...)` | สร้าง Todo ใหม่ → save |
| `toggleTodo(todo)` | สลับ isDone → save |
| `deleteTodo(todo)` | ลบ Todo → save |
| `toggleFavorite(member)` | สลับ isFavorite → save |
| `getDueNotifications()` | คืน todo ที่ !isDone && date ≤ วันนี้ |

> **สำคัญ**: ทุกครั้งที่ข้อมูลเปลี่ยน จะ `saveData()` ทันที — และเพราะใช้ `mutableStateListOf` UI จะ recompose อัตโนมัติ

---

## 4. MainActivity.kt — App Entry Point

| ส่วน | หน้าที่ |
|---|---|
| `onCreate()` | เรียก `AppState.init()` เพื่อโหลดข้อมูลจาก SharedPreferences |
| `setContent { }` | จัดการ routing: ถ้า `showSplash=true` → แสดง SplashScreen, ถ้า `false` → แสดง HomeScreen |

> **Flow**: App Start → `AppState.init()` → SplashScreen → (Tap) → HomeScreen

---

## 5. SplashScreen.kt — Welcome Screen

| ส่วน | หน้าที่ |
|---|---|
| Background | พื้นหลังสีน้ำตาลเข้ม (`BrownDark`) |
| Logo Image | แสดงโลโก้แอป 300dp ตรงกลาง |
| Arrow Button | ปุ่มลูกศร (→) 64dp ที่ด้านล่าง — กด → เรียก `onTimeout()` เพื่อข้ามไป HomeScreen |

---

## 6. HomeScreen.kt — Main Content Hub

### Top-Level Components

| Component | หน้าที่ |
|---|---|
| `RobustImage(uri)` | แสดงรูปภาพอย่างปลอดภัย — รองรับ `file://`, absolute path, content URI |
| `HomeScreen()` | **หน้าหลัก** — จัดการ bottom nav, FAB, routing ไปแต่ละ tab |
| `NavItem(...)` | ปุ่ม bottom navigation แต่ละอัน — มี icon, label, badge, animated color |
| `ModeToggle(...)` | Toggle switch แบบ pill ระหว่าง "Daily Memory" / "Expense" |

### Home Content

| Component | หน้าที่ |
|---|---|
| `HomeContent(...)` | ตัวแม่ — แสดง ModeToggle + switch content ตาม mode |
| `DailyMemoryContent()` | Feed ของ Post cards + Favorites row |
| `PostCard(post)` | Card แต่ละ post — แสดง avatar, member names, date, content, image, delete |

### Dialog Components

| Component | หน้าที่ |
|---|---|
| `AddPostDialog(onDismiss)` | Dialog สร้าง post ใหม่ — เลือก date, member, พิมพ์ content, แนบรูป |
| `MemberSelectorDialog(...)` | Dialog เลือกสมาชิก **หลายคน** (multi-select) — มีปุ่ม + เพิ่มสมาชิกใหม่ |
| `AddMemberDialog(...)` | Dialog เพิ่มสมาชิกใหม่ — ใส่ชื่อ + เลือกรูปโปรไฟล์ |
| `AddExpenseDialog(onDismiss)` | Dialog เพิ่มรายจ่าย — ใส่จำนวนเงิน, เลือก date, เลือก member |
| `ExpenseMemberSelectorDialog(...)` | Dialog เลือกสมาชิก **คนเดียว** (single-select) — มี "Personal" option |

### Expense Components

| Component | หน้าที่ |
|---|---|
| `ExpenseContent()` | LazyColumn ของ Expense cards + empty state |
| `ExpenseCard(expense)` | Card แต่ละรายจ่าย — avatar (pastel สี), amount, member name, date, delete |

### Image Handling Pattern

```
Photo Picker → contentResolver.openInputStream()
  → คัดลอกไฟล์ไปที่ context.filesDir (internal storage)
  → เก็บ URI เป็น "file:///data/.../post_123.jpg"
  → RobustImage รองรับทั้ง file://, /, content:// URIs
```

> **ทำไมต้อง copy**: เพราะ content:// URI จาก gallery อาจหมดอายุ — copy ไป internal storage ทำให้รูปไม่หาย

---

## 7. CalendarScreen.kt — Calendar View

| Component / ส่วน | หน้าที่ |
|---|---|
| `CalendarContent()` | **หน้า Calendar ทั้งหมด** |
| Member Filter Row | LazyRow ของ chip ให้กรอง post ตาม member ("All" + member แต่ละคน) |
| Calendar Grid | ตาราง 7 คอลัมน์ (Sun-Sat) แสดงวันในเดือน |
| Month Navigation | ปุ่ม ← → เปลี่ยนเดือน |
| Day Cell | แต่ละวัน — มี highlight ถ้าเป็นวันนี้ (border), selected (fill), มี post (❤️ icon) |
| Day Posts | เมื่อเลือกวัน → แสดง LazyColumn ของ post cards สำหรับวันนั้น |

### Calendar Logic

```
first = currentMonth.atDay(1)          // วันแรกของเดือน
startOff = first.dayOfWeek.value % 7   // offset สำหรับ Sunday-start
rows = (startOff + days + 6) / 7       // จำนวนแถว
d = r * 7 + c - startOff + 1           // คำนวณวันที่จาก row/col
```

---

## 8. TodolistScreen.kt — Todo List

| Component | หน้าที่ |
|---|---|
| `TodoContent()` | **หน้า Todo ทั้งหมด** — แยก list เป็น Pending + Completed sections |
| `TodoRow(item)` | Card แต่ละ todo — checkbox, title, detail, status badge, date, delete |
| `AddTodoDialog(onDismiss)` | Dialog สร้าง todo ใหม่ — ใส่ title, detail, เลือก date |

### Status Badge Logic

```kotlin
val badge = when {
    item.isDone → "Completed"
    overdue     → "Overdue"        // date < today && !isDone
    today       → "Today"          // date == today
    else        → "Upcoming"       // date > today
}
```

---

## 9. NotificationScreen.kt — Alert Notifications

| Component | หน้าที่ |
|---|---|
| `NotificationContent()` | **หน้า Alerts** — แสดง todo ที่ overdue + due today |
| `AlertCard(task, isOverdue)` | Card แต่ละ alert — icon (⚠️/✅), title, detail, "Done" button |

### Alert Logic

```kotlin
val due = AppState.getDueNotifications()  // todos ที่ !isDone && date ≤ today
val overdue = due.filter { it.date.isBefore(today) }  // เลยกำหนด
val today = due.filter { it.date == today }            // ครบกำหนดวันนี้
```

> **Badge Counter**: จำนวน `due.size` แสดงเป็นตัวเลขบน icon 🔔 ใน bottom nav

---

## 🔄 Data Flow Summary

```
User Action (UI)
    ↓
AppState.addXxx() / deleteXxx() / toggleXxx()
    ↓
Modify mutableStateListOf  ──→  UI Recomposition (automatic)
    ↓
saveData() → Gson.toJson() → SharedPreferences.apply()
```

```
App Launch
    ↓
AppState.init(context)
    ↓
loadData() → SharedPreferences.getString() → Gson.fromJson()
    ↓
Populate mutableStateListOf ──→ UI renders with saved data
```
