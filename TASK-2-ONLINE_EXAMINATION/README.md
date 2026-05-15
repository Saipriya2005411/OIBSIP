# ExamPortal — Online Examination System

## Folder Structure
```
ExamPortal/
├── ExamApp.java          ← Main UI + entry point
├── ExamDatabase.java     ← File-based database (creates examportal_db.json)
├── User.java             ← User model with full attempt history
├── Question.java         ← Question model
├── QuestionBank.java     ← All exam questions
└── README.md
```

## What's New (vs original)

### 🗄 Database
- All user data persisted to `examportal_db.json` (auto-created on first run)
- Every exam attempt saved immediately with timestamp
- Profile changes (name, email, password) saved to DB

### 👤 Create Account / Register
- "New user? Create an account →" link on login screen
- Full registration form with validation:
  - Username (3–20 chars, unique)
  - Full name, email
  - Password (≥ 6 chars) with confirmation

### 📊 Performance Analysis
- **Summary tiles** — Total exams, Average score, Best, Worst
- **Per-subject breakdown** — Average, attempts, grade per subject
- **Score trend bar chart** — Visual bar chart of last 15 attempts
- **Grade distribution** — Count of A+/B/C/D grades
- **Full attempt history table** — Date, subject, score, grade for every attempt
- Accessible from top bar (📊 Analytics) and result screen

## Demo Accounts
| Username  | Password  | Name            |
|-----------|-----------|-----------------|
| student1  | pass123   | Saipriya Sharma |
| student2  | java2024  | Rahul Verma     |
| admin     | admin123  | Admin User      |

## How to Run in VSCode

### Prerequisites
1. Install **JDK 11+** from https://adoptium.net
2. Install **Extension Pack for Java** in VSCode (`Ctrl+Shift+X`)

### Steps
1. `File` → `Open Folder` → select `ExamPortal`
2. Open `ExamApp.java`
3. Click **▶ Run** above `main()` or press `F5`

Data is saved to `examportal_db.json` in the same folder.
