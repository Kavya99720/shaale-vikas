/**
 * Shaale-Vikas — Firestore Seed Script
 *
 * Populates your Firestore database with sample data for testing.
 *
 * SETUP:
 *   1. Download your Firebase service account key:
 *      Firebase Console → Project Settings → Service Accounts → Generate new private key
 *      Save it as `serviceAccountKey.json` in this `scripts/` folder.
 *
 *   2. Install dependencies:
 *      cd scripts && npm install
 *
 *   3. Run the seeder:
 *      npm run seed
 *
 *   4. To wipe all seed data and re-seed:
 *      npm run seed:reset
 *
 * NOTE: Never commit serviceAccountKey.json to version control.
 */

const admin = require("firebase-admin");
const path = require("path");
const fs = require("fs");

const KEY_PATH = path.join(__dirname, "serviceAccountKey.json");

if (!fs.existsSync(KEY_PATH)) {
  console.error(
    "\n❌  serviceAccountKey.json not found in scripts/\n" +
      "    Download it from Firebase Console → Project Settings → Service Accounts\n"
  );
  process.exit(1);
}

const serviceAccount = require(KEY_PATH);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();
const RESET = process.argv.includes("--reset");
const FieldValue = admin.firestore.FieldValue;
const Timestamp = admin.firestore.Timestamp;

// ─── SEED DATA ──────────────────────────────────────────────────────────────

const schoolProfile = {
  name: "Government Higher Primary School, Kolar",
  established: "1962",
  location: "Kolar District, Karnataka",
  studentCount: 240,
  about:
    "A rural government school serving students from 5 surrounding villages in Kolar district. " +
    "This school has produced alumni who are engineers, doctors, and teachers across Karnataka. " +
    "The school currently needs support from its alumni community to maintain and upgrade its facilities.",
  photoUrl: "",
};

const needs = [
  {
    title: "Repair Leaking Roof in Classroom Block A",
    category: "INFRASTRUCTURE",
    description:
      "The roof of Classroom Block A has been leaking for two monsoon seasons, causing damage to walls and making classes impossible during heavy rain. " +
      "Students are forced to squeeze into other classrooms or suspend classes entirely. Urgent waterproofing and tile replacement is needed.",
    photoUrl: "",
    costEstimate: 45000,
    amountPledged: 18000,
    status: "ACTIVE",
    urgency: 3,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)),
  },
  {
    title: "New Wooden Desks and Benches for Grade 6 & 7",
    category: "FURNITURE",
    description:
      "The desks and benches in Grade 6 and 7 classrooms are broken and splinted, posing safety hazards to students. " +
      "30 new sets of desks and benches are needed to seat all 60 students comfortably and safely.",
    photoUrl: "",
    costEstimate: 30000,
    amountPledged: 12500,
    status: "ACTIVE",
    urgency: 2,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 14 * 24 * 60 * 60 * 1000)),
  },
  {
    title: "Library Books for Classes 1–8",
    category: "LIBRARY",
    description:
      "The school library has not received new books in over 5 years. " +
      "We need 200 Kannada and English books across fiction, science, and general knowledge for all grade levels. " +
      "Reading materials are essential for building literacy skills in rural students.",
    photoUrl: "",
    costEstimate: 15000,
    amountPledged: 4000,
    status: "ACTIVE",
    urgency: 1,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 3 * 24 * 60 * 60 * 1000)),
  },
  {
    title: "Solar-Powered Water Purifier",
    category: "INFRASTRUCTURE",
    description:
      "Students currently drink water from an open tank that is not purified. " +
      "A solar-powered RO water purifier will provide clean drinking water to 240 students and 12 staff members year-round without electricity costs.",
    photoUrl: "",
    costEstimate: 22000,
    amountPledged: 0,
    status: "ACTIVE",
    urgency: 3,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 1 * 24 * 60 * 60 * 1000)),
  },
  {
    title: "Renovation of Girls' Toilet Block",
    category: "TOILETS",
    description:
      "The girls' toilet block has 3 of 4 units non-functional due to broken plumbing. " +
      "This is one of the primary reasons for girl student dropouts. " +
      "Full plumbing repair, tiling, and door replacement is needed to restore dignity and safety.",
    photoUrl: "",
    costEstimate: 35000,
    amountPledged: 7500,
    status: "ACTIVE",
    urgency: 3,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 5 * 24 * 60 * 60 * 1000)),
  },
  {
    title: "Sports Equipment — Cricket & Kabaddi",
    category: "SPORTS",
    description:
      "The school has a large playground but no sports equipment. " +
      "We need 2 cricket kits, kabaddi court marking, and a volleyball net to encourage physical activity and sports participation among students.",
    photoUrl: "",
    costEstimate: 12000,
    amountPledged: 0,
    status: "ACTIVE",
    urgency: 1,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 10 * 24 * 60 * 60 * 1000)),
  },
  {
    title: "Annual Stationery Supply — 2025",
    category: "STATIONERY",
    description:
      "240 students need notebooks, pencils, pens, geometry boxes, and drawing materials for the academic year 2025–26. " +
      "Many students from BPL families cannot afford these basics. Your contribution ensures no student is left behind.",
    photoUrl: "",
    costEstimate: 18000,
    amountPledged: 9000,
    status: "ACTIVE",
    urgency: 2,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 2 * 24 * 60 * 60 * 1000)),
  },
  {
    title: "Painted Blackboards for All 8 Classrooms",
    category: "INFRASTRUCTURE",
    description:
      "All 8 classroom blackboards have faded and cracked, making it nearly impossible for students in the back rows to read. " +
      "Repainting with quality blackboard paint and adding wooden frames will significantly improve learning.",
    photoUrl: "",
    costEstimate: 8000,
    amountPledged: 8000,
    status: "FULFILLED",
    urgency: 2,
    completedAt: Timestamp.fromDate(new Date(Date.now() - 30 * 24 * 60 * 60 * 1000)),
    beforePhotoUrl: "",
    afterPhotoUrl: "",
    createdAt: Timestamp.fromDate(new Date(Date.now() - 60 * 24 * 60 * 60 * 1000)),
  },
];

const sampleAlumni = [
  {
    name: "Ravi Kumar Naidu",
    email: "ravi.naidu@example.com",
    role: "ALUMNI",
    totalPledged: 18000,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 90 * 24 * 60 * 60 * 1000)),
  },
  {
    name: "Deepa Shastri",
    email: "deepa.shastri@example.com",
    role: "ALUMNI",
    totalPledged: 12500,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 80 * 24 * 60 * 60 * 1000)),
  },
  {
    name: "Suresh Gowda",
    email: "suresh.gowda@example.com",
    role: "ALUMNI",
    totalPledged: 9000,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 70 * 24 * 60 * 60 * 1000)),
  },
  {
    name: "Anitha Reddy",
    email: "anitha.reddy@example.com",
    role: "ALUMNI",
    totalPledged: 7500,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 60 * 24 * 60 * 60 * 1000)),
  },
  {
    name: "Manjunath Rao",
    email: "manju.rao@example.com",
    role: "ALUMNI",
    totalPledged: 4000,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 50 * 24 * 60 * 60 * 1000)),
  },
  {
    name: "Priya Venkatesh",
    email: "priya.v@example.com",
    role: "ALUMNI",
    totalPledged: 0,
    createdAt: Timestamp.fromDate(new Date(Date.now() - 20 * 24 * 60 * 60 * 1000)),
  },
];

// ─── HELPERS ────────────────────────────────────────────────────────────────

async function deleteCollection(collectionPath) {
  const snap = await db.collection(collectionPath).get();
  if (snap.empty) return;
  const batch = db.batch();
  snap.docs.forEach((doc) => batch.delete(doc.ref));
  await batch.commit();
  console.log(`  🗑  Cleared '${collectionPath}' (${snap.size} docs)`);
}

// ─── SEEDER ─────────────────────────────────────────────────────────────────

async function seed() {
  console.log("\n🌱  Shaale-Vikas Firestore Seeder");
  console.log("══════════════════════════════════\n");

  if (RESET) {
    console.log("⚠️   --reset flag detected. Wiping existing seed data...");
    await deleteCollection("needs");
    await deleteCollection("pledges");
    await deleteCollection("school");
    // Note: We do NOT delete 'users' to avoid removing real auth users
    console.log("  ⚠️   'users' collection skipped (preserves auth accounts)\n");
  }

  // 1. School profile
  console.log("📍  Seeding school profile...");
  const schoolRef = db.collection("school").doc("main");
  await schoolRef.set(schoolProfile);
  console.log("  ✅  School profile created\n");

  // 2. Needs
  console.log("📋  Seeding needs...");
  const needIds = [];
  for (const need of needs) {
    const ref = await db.collection("needs").add(need);
    needIds.push(ref.id);
    console.log(`  ✅  "${need.title.substring(0, 50)}..."`);
  }
  console.log();

  // 3. Alumni users (leaderboard only — no Firebase Auth accounts created)
  console.log("👥  Seeding alumni user profiles (leaderboard)...");
  for (const alumni of sampleAlumni) {
    const ref = db.collection("users").doc();
    await ref.set({ ...alumni, id: ref.id });
    console.log(`  ✅  ${alumni.name} — ₹${alumni.totalPledged} pledged`);
  }
  console.log();

  // 4. Sample pledges linked to needs and alumni
  console.log("🤝  Seeding sample pledges...");
  const pledgeData = [
    { needIndex: 0, alumniIndex: 0, amount: 18000, name: "Ravi Kumar Naidu" },
    { needIndex: 1, alumniIndex: 1, amount: 12500, name: "Deepa Shastri" },
    { needIndex: 6, alumniIndex: 2, amount: 9000, name: "Suresh Gowda" },
    { needIndex: 4, alumniIndex: 3, amount: 7500, name: "Anitha Reddy" },
    { needIndex: 2, alumniIndex: 4, amount: 4000, name: "Manjunath Rao" },
  ];
  for (const p of pledgeData) {
    if (needIds[p.needIndex]) {
      await db.collection("pledges").add({
        needId: needIds[p.needIndex],
        userId: `sample_${p.alumniIndex}`,
        name: p.name,
        phone: "",
        amount: p.amount,
        createdAt: Timestamp.fromDate(new Date(Date.now() - 5 * 24 * 60 * 60 * 1000)),
      });
      console.log(`  ✅  ${p.name} → ₹${p.amount}`);
    }
  }
  console.log();

  console.log("══════════════════════════════════");
  console.log("✅  Seeding complete!\n");
  console.log("📱  Launch the app and you should see:");
  console.log("    • 7 active needs on the dashboard");
  console.log("    • 1 fulfilled need in the Impact Gallery");
  console.log("    • 5 donors in the Hall of Fame leaderboard");
  console.log("    • School profile populated\n");
  console.log("⚠️   IMPORTANT: Create your Admin user manually in Firebase Console:");
  console.log("    Authentication → Add user (e.g. admin@shaalevikas.edu)");
  console.log("    Then add a Firestore doc in 'users' with role: 'ADMIN'\n");
}

seed().catch((err) => {
  console.error("❌  Seeding failed:", err.message);
  process.exit(1);
});
