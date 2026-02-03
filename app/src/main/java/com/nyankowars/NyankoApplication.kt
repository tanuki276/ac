package com.nyankowars

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NyankoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Timberの初期化（開発時のみ）
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // 他の初期化処理
        initializeApp()
    }
    
    private fun initializeApp() {
        // Firebase、Crashlyticsなどの初期化
        // 必要に応じて実装
    }
}