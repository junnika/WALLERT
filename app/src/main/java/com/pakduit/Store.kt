package com.pakduit
import android.app.*
import android.content.Context
import android.content.Intent
import org.json.*
import java.text.SimpleDateFormat
import java.util.*

data class Tx(val d: String, val a: Long, val inc: Boolean, val k: String, val n: String)

object Store {
    private fun p(c: Context) = c.getSharedPreferences("pd", 0)
    fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    fun month() = today().substring(0, 7)
    fun rp(n: Long) = "Rp" + String.format(Locale("id", "ID"), "%,d", n)
    fun limit(c: Context) = p(c).getLong("limit", 0)
    fun warn(c: Context) = p(c).getInt("warn", 80)
    fun setLimit(c: Context, l: Long, w: Int) { p(c).edit().putLong("limit", l).putInt("warn", w).putString("seen", "").apply(); DuitWidget.refresh(c) }
    fun all(c: Context): List<Tx> {
        val j = JSONArray(p(c).getString("tx", "[]"))
        return List(j.length()) { val o = j.getJSONObject(it); Tx(o.getString("d"), o.getLong("a"), o.getBoolean("i"), o.getString("k"), o.getString("n")) }
    }
    fun add(c: Context, t: Tx) {
        val j = JSONArray()
        (all(c) + t).forEach { j.put(JSONObject().put("d", it.d).put("a", it.a).put("i", it.inc).put("k", it.k).put("n", it.n)) }
        p(c).edit().putString("tx", j.toString()).apply()
        check(c); DuitWidget.refresh(c)
    }
    fun spent(c: Context) = all(c).filter { !it.inc && it.d.startsWith(month()) }.sumOf { it.a }
    fun income(c: Context) = all(c).filter { it.inc && it.d.startsWith(month()) }.sumOf { it.a }
    fun mood(c: Context): Int {
        val l = limit(c); if (l <= 0) return 0
        val pct = spent(c) * 100 / l
        return if (pct >= 100) 2 else if (pct >= warn(c)) 1 else 0
    }
    fun guess(t: String, inc: Boolean): String {
        if (inc) return "Masuk"
        val s = t.lowercase()
        return when {
            Regex("makan|kopi|resto|food|warung|cafe").containsMatchIn(s) -> "Makan"
            Regex("gojek|grab|bensin|parkir|tol|krl|mrt|ojek").containsMatchIn(s) -> "Transport"
            Regex("shopee|tokopedia|lazada|mart|belanja").containsMatchIn(s) -> "Belanja"
            Regex("listrik|pln|pdam|internet|pulsa|bpjs|cicilan").containsMatchIn(s) -> "Tagihan"
            else -> "Lainnya"
        }
    }
    fun check(c: Context) {
        val m = mood(c); if (m == 0) return
        val key = month() + m; if (p(c).getString("seen", "") == key) return
        p(c).edit().putString("seen", key).apply()
        val nm = c.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel("lim", "Batas pengeluaran", NotificationManager.IMPORTANCE_HIGH))
        val pi = PendingIntent.getActivity(c, 0, Intent(c, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        val pic = Mascot.pick(c, m)
        val n = Notification.Builder(c, "lim").setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(pic.text)
            .setContentText("Terpakai ${rp(spent(c))} dari ${rp(limit(c))}")
            .setLargeIcon(pic.bmp).setContentIntent(pi).setAutoCancel(true).build()
        nm.notify(1, n)
    }
}
