package com.savinoordine.accessibilitytest.injection

import com.savinoordine.accessibilitytest.service.MyAccessibilityService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideService(): MyAccessibilityService = MyAccessibilityService()
}