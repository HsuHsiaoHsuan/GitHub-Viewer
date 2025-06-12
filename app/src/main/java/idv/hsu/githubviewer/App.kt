package idv.hsu.githubviewer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import idv.hsu.githubviewer.data.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree());
        }
    }
}