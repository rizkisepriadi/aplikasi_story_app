package com.dicoding.picodiploma.loginwithanimation.view.widget

import StackRemoteViewsFactory
import android.content.Intent
import android.widget.RemoteViewsService
import com.dicoding.picodiploma.loginwithanimation.viewModel.ViewModelFactory

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        // Anda bisa mendapatkan UserRepository dari ViewModelFactory atau mekanisme dependency injection lainnya
        val userRepository = ViewModelFactory.getInstance(applicationContext).repository

        return StackRemoteViewsFactory(this.applicationContext, userRepository)
    }
}
