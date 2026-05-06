const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * Triggered when a new need is created in Firestore.
 * Sends a push notification to all alumni subscribed to the topic.
 */
exports.notifyNewNeed = functions.firestore
  .document("needs/{needId}")
  .onCreate(async (snap, context) => {
    const need = snap.data();
    if (!need) return null;

    const categoryEmoji = {
      FURNITURE: "🪑",
      INFRASTRUCTURE: "🏗️",
      STATIONERY: "📚",
      TOILETS: "🚽",
      LIBRARY: "📖",
      SPORTS: "⚽",
      TECHNOLOGY: "💻",
      OTHER: "🏫",
    };

    const emoji = categoryEmoji[need.category] || "🏫";
    const urgencyTag = need.urgency >= 3 ? " [URGENT]" : "";

    const message = {
      notification: {
        title: `${emoji} New School Need${urgencyTag}`,
        body: `${need.title} — ₹${Math.round(need.costEstimate).toLocaleString("en-IN")} needed`,
      },
      data: {
        needId: context.params.needId,
        screen: "need_detail",
      },
      topic: "shaale_vikas_needs",
    };

    try {
      await admin.messaging().send(message);
      console.log("Notification sent for need:", need.title);
    } catch (err) {
      console.error("Failed to send notification:", err);
    }

    return null;
  });

/**
 * Triggered when a need is marked as FULFILLED.
 * Notifies alumni that a need has been completed.
 */
exports.notifyNeedFulfilled = functions.firestore
  .document("needs/{needId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();

    if (!before || !after) return null;
    if (before.status === after.status) return null;
    if (after.status !== "FULFILLED") return null;

    const message = {
      notification: {
        title: "✅ Need Fulfilled!",
        body: `"${after.title}" has been successfully funded. Thank you alumni!`,
      },
      data: {
        needId: context.params.needId,
        screen: "gallery",
      },
      topic: "shaale_vikas_needs",
    };

    try {
      await admin.messaging().send(message);
      console.log("Fulfilled notification sent for need:", after.title);
    } catch (err) {
      console.error("Failed to send fulfilled notification:", err);
    }

    return null;
  });
