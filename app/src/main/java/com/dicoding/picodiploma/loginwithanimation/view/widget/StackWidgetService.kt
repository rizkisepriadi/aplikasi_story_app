package com.dicoding.picodiploma.loginwithanimation.view.widget

import StackRemoteViewsFactory
import android.content.Intent
import android.widget.RemoteViewsService
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.di.Injection

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val userRepository: UserRepository = Injection.provideUserRepository(applicationContext)

        return StackRemoteViewsFactory(this.applicationContext, userRepository)
    }
}
