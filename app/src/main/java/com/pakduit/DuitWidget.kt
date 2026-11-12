package com.pakduit
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class DuitWidget : AppWidgetProvider() {
    override fun onUpdate(c: Context, m: AppWidgetManager, ids: IntArray) { ids.forEach { m.updateAppWidget(it, views(c)) } }
    companion object {
        fun views(c: Context): RemoteViews {
            val v = RemoteViews(c.packageName, R.layout.widget); val m = Store.mood(c); val l = Store.limit(c)
            val pic = Mascot.pick(c, m)
            v.setImageViewBitmap(R.id.img, pic.bmp)
            val say = pic.text
            val info = if (l > 0) "${Store.rp(Store.spent(c))} / ${Store.rp(l)} (${Store.spent(c) * 100 / l}%)" else "Belum ada batas. Buka aplikasi."
            v.setTextViewText(R.id.txt, "$info\n$say")
            v.setInt(R.id.root, "setBackgroundColor", Wallpaper.widgetColor(c))
            v.setOnClickPendingIntent(R.id.root, PendingIntent.getActivity(c, 0, Intent(c, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
            return v
        }
        fun refresh(c: Context) {
            val mg = AppWidgetManager.getInstance(c)
            mg.getAppWidgetIds(ComponentName(c, DuitWidget::class.java)).forEach { mg.updateAppWidget(it, views(c)) }
        }
    }
}
