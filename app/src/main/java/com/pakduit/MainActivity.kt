package com.pakduit
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 1)
    }
    override fun onActivityResult(rc: Int, res: Int, d: Intent?) {
        super.onActivityResult(rc, res, d)
        if (rc == 7 && res == RESULT_OK) d?.data?.let { Wallpaper.savePhoto(this, it); DuitWidget.refresh(this); build() }
    }
    override fun onResume() { super.onResume(); build() }
    private fun btn(t: String, f: () -> Unit) = Button(this).apply { text = t; setOnClickListener { f() } }
    private fun edit(h: String, num: Boolean) = EditText(this).apply { hint = h; if (num) inputType = InputType.TYPE_CLASS_NUMBER }
    private fun build() {
        val l = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 80, 40, 60) }
        window.setBackgroundDrawable(Wallpaper.drawable(this)); l.setBackgroundColor(0xB8FFFFFF.toInt())
        val m = Store.mood(this); val pic = Mascot.pick(this, m)
        l.addView(ImageView(this).apply { setImageBitmap(pic.bmp); layoutParams = LinearLayout.LayoutParams(360, 360).apply { gravity = android.view.Gravity.CENTER } })
        val lim = Store.limit(this)
        l.addView(TextView(this).apply {
            textSize = 16f; gravity = android.view.Gravity.CENTER
            text = (if (lim > 0) "Terpakai ${Store.rp(Store.spent(this@MainActivity))} dari ${Store.rp(lim)}\n" else "Atur batas dulu di bawah\n") +
                "Masuk bulan ini: ${Store.rp(Store.income(this@MainActivity))}\n" +
                pic.text
        })
        val amt = edit("Nominal (Rp)", true); val note = edit("Catatan (mis. makan siang)", false)
        l.addView(amt); l.addView(note)
        val add = { inc: Boolean ->
            val a = amt.text.toString().toLongOrNull()
            if (a != null && a > 0) { Store.add(this, Tx(Store.today(), a, inc, Store.guess(note.text.toString(), inc), note.text.toString())); build() }
        }
        l.addView(btn("Catat KELUAR") { add(false) }); l.addView(btn("Catat MASUK") { add(true) })
        val lm = edit("Batas pengeluaran per bulan (Rp)", true).apply { if (lim > 0) setText(lim.toString()) }
        val wn = edit("Peringatan di (%)", true).apply { setText(Store.warn(this@MainActivity).toString()) }
        l.addView(TextView(this).apply { text = "\nBATAS"; textSize = 14f }); l.addView(lm); l.addView(wn)
        l.addView(btn("Simpan batas") { Store.setLimit(this, lm.text.toString().toLongOrNull() ?: 0, (wn.text.toString().toIntOrNull() ?: 80).coerceIn(1, 99)); build() })
        l.addView(btn("🎨 Ganti wallpaper") { Wallpaper.next(this); build() })
        l.addView(btn("📷 Pakai foto sendiri") { startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), 7) })
        l.addView(btn("Izinkan baca notifikasi M-Banking") { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) })
        l.addView(TextView(this).apply { text = "\nTRANSAKSI TERAKHIR"; textSize = 14f })
        Store.all(this).takeLast(20).reversed().forEach { t ->
            l.addView(TextView(this).apply { text = "${t.d}  ${if (t.inc) "+" else "-"}${Store.rp(t.a)}  [${t.k}] ${t.n}"; textSize = 13f; setPadding(0, 8, 0, 8) })
        }
        setContentView(ScrollView(this).apply { addView(l) })
    }
}
