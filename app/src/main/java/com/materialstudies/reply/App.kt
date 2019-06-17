package com.materialstudies.reply

import android.app.Application
import com.materialstudies.reply.data.EmailStore

class App : Application() {

    val emailStore = EmailStore()

}