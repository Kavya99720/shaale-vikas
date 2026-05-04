# Shaale-Vikas — Complete Setup Guide 🏫

Follow these steps in order. Takes about 20–30 minutes from scratch.

---

## ✅ CHECKLIST OVERVIEW

- [ ] Step 1 — Clone the repo
- [ ] Step 2 — Create Firebase project
- [ ] Step 3 — Enable Firebase Authentication
- [ ] Step 4 — Create Firestore database
- [ ] Step 5 — Enable Firebase Storage
- [ ] Step 6 — Download google-services.json
- [ ] Step 7 — Deploy Firestore rules & indexes
- [ ] Step 8 — Create the Admin user
- [ ] Step 9 — Get your Gemini API key
- [ ] Step 10 — Run the data seeder
- [ ] Step 11 — Build & run in Android Studio

---

## STEP 1 — Clone the Repository

```bash
git clone https://github.com/Kavya99720/shaale-vikas.git
cd shaale-vikas
```

Open the project in **Android Studio** (File → Open → select the `shaale-vikas` folder).
Let Gradle sync finish before proceeding.

---

## STEP 2 — Create a Firebase Project

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Click **"Add project"**
3. Name it: `Shaale-Vikas` (or anything you prefer)
4. Disable Google Analytics (optional, not needed)
5. Click **Create project**

---

## STEP 3 — Enable Firebase Authentication

Inside your Firebase project:

1. Left sidebar → **Build → Authentication**
2. Click **"Get started"**
3. Click on **"Email/Password"** under "Native providers"
4. Toggle **Enable** → Save

---

## STEP 4 — Create Firestore Database

1. Left sidebar → **Build → Firestore Database**
2. Click **"Create database"**
3. Choose **"Start in production mode"** (we'll deploy proper rules in Step 7)
4. Select a region close to you (e.g., `asia-south1` for India)
5. Click **Enable**

---

## STEP 5 — Enable Firebase Storage

1. Left sidebar → **Build → Storage**
2. Click **"Get started"**
3. Choose **"Start in production mode"**
4. Select the same region as Firestore
5. Click **Done**

---

## STEP 6 — Download google-services.json

1. In Firebase Console → Click the **gear icon ⚙️** → **Project settings**
2. Scroll down to **"Your apps"** → Click **"Add app"** → Choose **Android**
3. Fill in:
   - **Android package name:** `com.shaalevikas.app`
   - **App nickname:** Shaale-Vikas
   - **Debug signing certificate SHA-1:** *(optional for now, add later for production)*
4. Click **Register app**
5. Click **Download google-services.json**
6. Place this file inside the **`app/`** folder:
   ```
   shaale-vikas/
   └── app/
       └── google-services.json   ← place it here
   ```
7. Click **Next → Next → Continue to console** (skip the SDK steps, it's already in the code)

---

## STEP 7 — Deploy Firestore Rules & Indexes

### Option A — Firebase CLI (Recommended)

Install the Firebase CLI if you don't have it:
```bash
npm install -g firebase-tools
firebase login
```

From the `shaale-vikas/` root folder:
```bash
firebase use --add
# Select your Firebase project when prompted

firebase deploy --only firestore
firebase deploy --only storage
```

### Option B — Manually via Firebase Console

**Firestore Rules:**
1. Firestore Database → **Rules** tab
2. Replace all content with the contents of `firestore.rules`
3. Click **Publish**

**Firestore Indexes:**
1. Firestore Database → **Indexes** tab
2. Click **Add index** and create these composite indexes:

| Collection | Fields | Order |
|------------|--------|-------|
| needs | status (ASC), urgency (DESC) | Collection |
| needs | status (ASC), completedAt (DESC) | Collection |
| pledges | needId (ASC), userId (ASC) | Collection |
| pledges | userId (ASC), createdAt (DESC) | Collection |

**Storage Rules:**
1. Storage → **Rules** tab
2. Replace content with the contents of `storage.rules`
3. Click **Publish**

---

## STEP 8 — Create the Admin (Headmaster) Account

### 8a — Create the Auth account
1. Firebase Console → **Authentication → Users** tab
2. Click **"Add user"**
3. Enter:
   - **Email:** `admin@shaalevikas.edu` *(or any email you prefer)*
   - **Password:** *(strong password)*
4. Click **Add user**
5. Copy the **User UID** shown in the users list

### 8b — Create the Admin Firestore document
1. Firestore Database → **Data** tab
2. Click **"Start collection"** → Collection ID: `users` → **Next**
3. Set Document ID to the **UID you copied above**
4. Add these fields:

| Field | Type | Value |
|-------|------|-------|
| id | string | *(paste the UID)* |
| name | string | `School Headmaster` |
| email | string | `admin@shaalevikas.edu` |
| role | string | `ADMIN` |
| totalPledged | number | `0` |

5. Click **Save**

> **Important:** The `role` field value must be exactly `ADMIN` in uppercase. This is what gives the account admin access in the app.

---

## STEP 9 — Get Your Gemini API Key

1. Go to [aistudio.google.com](https://aistudio.google.com)
2. Sign in with your Google account
3. Click **"Get API key"** → **"Create API key"**
4. Copy the key

Now add it to your project's `local.properties` file (in the root of your project):

```properties
# local.properties  ← this file already exists, just add this line
sdk.dir=/path/to/your/android/sdk
GEMINI_API_KEY=AIzaSy...your_key_here
```

> `local.properties` is listed in `.gitignore` — your key will never be uploaded to GitHub.

---

## STEP 10 — Run the Data Seeder

This populates your Firestore with sample needs, alumni, and school profile.

### 10a — Download the service account key
1. Firebase Console → **⚙️ Project Settings → Service accounts** tab
2. Click **"Generate new private key"** → **Generate key**
3. Save the downloaded JSON file as:
   ```
   shaale-vikas/scripts/serviceAccountKey.json
   ```
   > This file is in `.gitignore` — it will NOT be uploaded to GitHub.

### 10b — Run the seeder
```bash
cd shaale-vikas/scripts
npm install
npm run seed
```

**Expected output:**
```
🌱  Shaale-Vikas Firestore Seeder
══════════════════════════════════

📍  Seeding school profile...
  ✅  School profile created

📋  Seeding needs...
  ✅  Repair Leaking Roof in Classroom Block A
  ✅  New Wooden Desks and Benches for Grade 6 & 7
  ✅  Library Books for Classes 1–8
  ✅  Solar-Powered Water Purifier
  ✅  Renovation of Girls' Toilet Block
  ✅  Sports Equipment — Cricket & Kabaddi
  ✅  Annual Stationery Supply — 2025
  ✅  Painted Blackboards for All 8 Classrooms

👥  Seeding alumni user profiles...
  ✅  Ravi Kumar Naidu — ₹18000 pledged
  ...

✅  Seeding complete!
```

To wipe and re-seed from scratch:
```bash
npm run seed:reset
```

---

## STEP 11 — Build & Run in Android Studio

1. In Android Studio, click **"Sync Project with Gradle Files"** (elephant icon 🐘)
2. Wait for the sync to complete — it will download all dependencies automatically
3. Select your device or emulator from the dropdown
4. Click **▶ Run** (or press `Shift + F10`)

### What you should see on first launch:
- **Splash screen** with the Shaale-Vikas logo
- **Login screen** — sign in with your admin credentials
- **Admin Dashboard** — 7 active needs with funding progress bars
- **+ button** — add a new need with GenAI description generation
- **Gallery** — 1 completed project (blackboards)

For alumni testing, register a new account — it will automatically get the `ALUMNI` role.

---

## COMMON ISSUES

### ❌ "google-services.json not found"
Make sure the file is in `app/google-services.json`, not the root folder.

### ❌ "PERMISSION_DENIED" in Firestore
Your Firestore security rules haven't been deployed. Complete Step 7.

### ❌ GenAI button does nothing / shows error
Your `GEMINI_API_KEY` in `local.properties` is missing or incorrect. Check Step 9.

### ❌ Admin sees Alumni dashboard
The `role` field in Firestore for your admin user must be exactly `ADMIN` (all caps, no spaces).

### ❌ Gradle sync fails
Make sure you're using Android Studio **Hedgehog (2023.1.1)** or newer.
Go to **File → Project Structure → SDK Location** and verify your Android SDK path.

### ❌ Seeder fails with "serviceAccountKey.json not found"
The key file must be placed at `scripts/serviceAccountKey.json` exactly (Step 10a).

---

## PROJECT STRUCTURE REFERENCE

```
shaale-vikas/
├── app/
│   ├── google-services.json          ← Add this (Step 6)
│   ├── build.gradle.kts
│   └── src/main/java/com/shaalevikas/app/
│       ├── MainActivity.kt
│       ├── data/model/Models.kt      ← Need, Pledge, User, etc.
│       ├── data/repository/          ← Firebase data access
│       ├── viewmodel/                ← MVVM ViewModels
│       ├── ui/                       ← All Jetpack Compose screens
│       ├── navigation/NavGraph.kt    ← App navigation
│       └── utils/GeminiHelper.kt    ← Gemini AI integration
├── firestore.rules                   ← Security rules (Step 7)
├── firestore.indexes.json            ← Composite indexes (Step 7)
├── storage.rules                     ← Storage rules (Step 7)
├── firebase.json                     ← Firebase CLI config
├── scripts/
│   ├── seed_firestore.js             ← Data seeder (Step 10)
│   ├── serviceAccountKey.json        ← Add this (Step 10, gitignored)
│   └── package.json
├── local.properties                  ← Add GEMINI_API_KEY here (Step 9)
└── README.md
```

---

## BUILDING A RELEASE APK

Once the app is working:

1. **Android Studio → Build → Generate Signed Bundle / APK**
2. Choose **APK**
3. Create a new keystore (save it safely — you'll need it for all future updates)
4. Select **release** build variant
5. Click **Finish**

The APK will be in `app/release/app-release.apk`.

---

*Shaale-Vikas — "ಶಾಲೆ ವಿಕಾಸ" — School Development*
*Built with ❤️ for rural education in Karnataka*
