package com.pakduit
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotifListener : NotificationListenerService() {
    private val apps = listOf("bca", "mandiri", "bri", "bni", "jago", "seabank", "gopay", "gojek", "ovo", "dana", "shopee", "linkaja", "blu", "cimb", "permata", "jenius", "octo", "btn", "danamon", "maybank", "livin", "brimo", "wondr")
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName.lowercase(); if (apps.none { pkg.contains(it) }) return
        val e = sbn.notification.extras
        val txt = ((e.getCharSequence("android.title") ?: "").toString() + " " + (e.getCharSequence("android.bigText") ?: e.getCharSequence("android.text") ?: "")).trim()
        val m = Regex("(?:rp|idr)\\.?\\s*([\\d.,]+)", RegexOption.IGNORE_CASE).find(txt) ?: return
        val a = m.groupValues[1].replace(Regex("[.,]\\d{1,2}$"), "").replace(Regex("[.,]"), "").toLongOrNull() ?: return
        if (a <= 0) return
        val o = RegexOption.IGNORE_CASE
        val inc = Regex("menerima|diterima|dana masuk|kredit|top ?up|setoran|gaji", o).containsMatchIn(txt) && !Regex("pembayaran|dikirim|transfer ke|debit", o).containsMatchIn(txt)
        val key = pkg + a + txt.hashCode(); val sp = getSharedPreferences("pd", 0)
        if (sp.getString("last", "") == key) return
        sp.edit().putString("last", key).apply()
        Store.add(this, Tx(Store.today(), a, inc, Store.guess(txt, inc), txt.take(60)))
    }
}
