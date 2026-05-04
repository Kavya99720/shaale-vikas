# Shaale-Vikas 🏫

**"Connecting Alumni to Rural Schools — One Need at a Time"**

A Kotlin + Jetpack Compose Android app that bridges rural school headmasters with their alumni for transparent micro-crowdfunding of school infrastructure needs.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Backend | Firebase Firestore + Firebase Storage |
| Auth | Firebase Authentication |
| GenAI | Google Gemini API (gemini-1.5-flash) |
| Architecture | MVVM + Repository Pattern |
| Navigation | Jetpack Navigation Compose |
| Images | Coil |

---

## Setup Instructions

### 1. Firebase Setup
1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with package name `com.shaalevikas.app`
3. Download `google-services.json` and place it in `app/`
4. Enable **Email/Password** authentication in Firebase Auth
5. Create Firestore database in **production mode**
6. Enable **Firebase Storage**

### 2. Admin Account Setup
In Firebase Console → Authentication, create an admin user. Then in Firestore, add a document to the `users` collection with:
```json
{
  "id": "<firebase-uid>",
  "name": "Headmaster Name",
  "email": "admin@school.com",
  "role": "ADMIN",
  "totalPledged": 0
}
```

### 3. School Profile Setup
In Firestore, create a `school` collection with one document:
```json
{
  "name": "Government Higher Primary School",
  "established": "1962",
  "location": "Kolar District, Karnataka",
  "studentCount": 240,
  "about": "A rural government school serving students from 5 surrounding villages..."
}
```

### 4. Gemini API Key
1. Get an API key from [Google AI Studio](https://aistudio.google.com)
2. Add to your `local.properties` file (never commit this):
```
GEMINI_API_KEY=your_api_key_here
```

### 5. Firestore Security Rules
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
    }
    match /needs/{needId} {
      allow read: if true;
      allow write: if request.auth != null &&
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }
    match /pledges/{pledgeId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
    }
    match /school/{docId} {
      allow read: if true;
      allow write: if request.auth != null &&
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'ADMIN';
    }
  }
}
```

---

## Project Structure

```
app/src/main/java/com/shaalevikas/app/
├── MainActivity.kt
├── data/
│   ├── model/Models.kt          # Data classes: Need, Pledge, User, etc.
│   └── repository/              # Firebase data access layer
├── viewmodel/                   # MVVM ViewModels
├── ui/
│   ├── auth/                    # Splash, Login, Register, ForgotPassword
│   ├── home/                    # Needs Dashboard
│   ├── needs/                   # Need Detail
│   ├── pledge/                  # Pledge Screen
│   ├── halloffame/              # Leaderboard
│   ├── gallery/                 # Impact Gallery (Before/After)
│   ├── profile/                 # User Profile & Pledge History
│   ├── school/                  # School Profile
│   ├── admin/                   # Admin Dashboard + Add/Edit Need
│   └── theme/                   # Material3 Theme & Colors
├── navigation/NavGraph.kt       # Jetpack Navigation
└── utils/
    ├── GeminiHelper.kt          # Gemini API integration
    └── ShaaleMessagingService.kt # FCM push notifications
```

---

## Features

- **Multi-role auth** — Admin (Headmaster) & Alumni via Firebase Auth
- **Needs Dashboard** — Real-time scrollable list with progress bars
- **Pledge System** — Alumni commit support; Firestore transaction updates progress in real-time
- **Duplicate pledge prevention** — One pledge per user per need
- **Hall of Fame** — Live leaderboard with Bronze/Silver/Gold/Platinum badges
- **Impact Gallery** — Before & After photo pairs for completed projects
- **GenAI Assistant** — Gemini-powered description generator & cost estimator
- **Admin Panel** — Post/Edit/Delete needs, mark fulfilled, upload completion photos
- **Push Notifications** — FCM for new needs and fulfilled milestones
- **School Profile** — Info page about the school from Firestore

---

## Build & Run

```bash
# Clone and open in Android Studio
git clone https://github.com/Kavya99720/shaale-vikas.git

# Add google-services.json to app/
# Add GEMINI_API_KEY to local.properties
# Sync Gradle and Run
```

Minimum SDK: Android 7.0 (API 24) | Target SDK: API 35
