package com.pakduit
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

data class Pic(val bmp: Bitmap, val text: String)

object Mascot {
    /** index 0 aman, 1 hampir batas, 2 lewat batas */
    private val data: List<List<Pair<Int, List<String>>>> = listOf(
        listOf(Pair(R.drawable.pic_ok1, listOf("KIW, MASIH BANYAK NIEH 😏", "Dompetmu gemuk banget, jadi pengen dideketin~", "Hemat banget sih kamu, makin sayang deh", "Sisa banyak nih, traktir aku dong kiw"))),
        listOf(Pair(R.drawable.pic_near1, listOf("Udah hampir abis, masih aja jajan?", "Hmm... yakin mau beli itu?", "Sabar dong, tahan dulu jajannya")),
        Pair(R.drawable.pic_near2, listOf("Dikit lagi jebol loh, masih aja checkout", "Hadeh, dompet udah ngos-ngosan kamu masih aja jajan", "Udah hampir abis tuh, ampun deh"))),
        listOf(Pair(R.drawable.pic_over1, listOf("UDAH JEBOL WOY! STOP JAJAN!", "KAMU TUH YA! BATAS UDAH LEWAT MASIH AJA BELI!")),
        Pair(R.drawable.pic_over2, listOf("Dompetnya nangis nih... tega banget kamu", "Udah bangkrut bulan ini, puasa jajan dulu ya sayang")))
    )
    fun pick(c: Context, m: Int): Pic {
        val e = data[m].random()
        return Pic(BitmapFactory.decodeResource(c.resources, e.first), e.second.random())
    }
}
