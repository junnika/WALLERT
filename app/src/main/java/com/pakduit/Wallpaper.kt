package com.pakduit
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import java.io.File

object Wallpaper {
    private val cols = listOf(
        intArrayOf(0xFFFFD1DC.toInt(), 0xFFD6F0FF.toInt()), // Permen
        intArrayOf(0xFFA1C4FD.toInt(), 0xFFC2E9FB.toInt()), // Langit
        intArrayOf(0xFFD4FC79.toInt(), 0xFF96E6A1.toInt()), // Matcha
        intArrayOf(0xFFFF9A9E.toInt(), 0xFFFAD0C4.toInt()), // Sunset
        intArrayOf(0xFF1A1040.toInt(), 0xFF3B2A7A.toInt())  // Galaksi
    )
    private fun p(c: Context) = c.getSharedPreferences("pd", 0)
    fun idx(c: Context) = p(c).getInt("bg", 0)
    fun next(c: Context) { p(c).edit().putInt("bg", (idx(c) + 1) % cols.size).apply(); DuitWidget.refresh(c) }
    fun drawable(c: Context): Drawable {
        val i = idx(c)
        if (i < 0) { val f = File(c.filesDir, "bg.jpg"); if (f.exists()) return BitmapDrawable(c.resources, BitmapFactory.decodeFile(f.path)) }
        return GradientDrawable(GradientDrawable.Orientation.TL_BR, cols[i.coerceAtLeast(0)])
    }
    /** warna latar widget: warna preset yang digelapkan supaya teks putih terbaca */
    fun widgetColor(c: Context): Int {
        val col = cols[idx(c).coerceAtLeast(0)][1]
        return Color.argb(0xE6, (Color.red(col) * 0.45).toInt(), (Color.green(col) * 0.45).toInt(), (Color.blue(col) * 0.45).toInt())
    }
    fun savePhoto(c: Context, uri: Uri) {
        val src = c.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = 2 }) } ?: return
        val dm = c.resources.displayMetrics; val tw = dm.widthPixels; val th = dm.heightPixels
        val s = maxOf(tw.toFloat() / src.width, th.toFloat() / src.height)
        val sc = Bitmap.createScaledBitmap(src, (src.width * s).toInt().coerceAtLeast(tw), (src.height * s).toInt().coerceAtLeast(th), true)
        val out = Bitmap.createBitmap(sc, (sc.width - tw) / 2, (sc.height - th) / 2, tw, th)
        File(c.filesDir, "bg.jpg").outputStream().use { out.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        p(c).edit().putInt("bg", -1).apply()
    }
}
