# NeoBank ATM — VSCode Setup Guide

## Folder Structure
```
NeoBank/
├── ATMApp.java       ← Main UI + entry point
├── Account.java      ← Account model
├── Bank.java         ← Business logic
├── Database.java     ← File-based database (creates neobank_db.json)
└── README.md
```

## Prerequisites
Install the **Java Development Kit (JDK) 11 or higher**:
- Download from: https://adoptium.net  (free, recommended)
- During install, check "Set JAVA_HOME" if prompted

## Step-by-step in VSCode

### 1. Install the Extension
Open VSCode → press `Ctrl+Shift+X` → search **"Extension Pack for Java"** by Microsoft → Install

### 2. Open the Folder
`File` → `Open Folder` → select the `NeoBank` folder

### 3. Run the App
Open `ATMApp.java` → click the **▶ Run** button that appears above the `main` method
— OR press `F5`

That's it! The app window will open.

## Demo accounts (auto-created on first run)
| User ID | PIN  | Name     | Balance   |
|---------|------|----------|-----------|
| user01  | 1234 | Saipriya | ₹50,000   |
| user02  | 5678 | Vishal   | ₹25,000   |
| user03  | 9999 | Shaaya   | ₹75,000   |

New accounts can be registered from the login screen.
All data is saved automatically to `neobank_db.json`.
