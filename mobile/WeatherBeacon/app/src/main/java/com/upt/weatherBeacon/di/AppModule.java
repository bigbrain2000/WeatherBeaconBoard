package com.upt.weatherBeacon.di;

import static com.upt.weatherBeacon.di.NetworkModule.provideOpenMeteoAPI;

import com.upt.weatherBeacon.data.remote.WeatherRepository.OpenMeteoApi;
import com.upt.weatherBeacon.data.remote.WeatherRepository.WeatherRepository;
import com.upt.weatherBeacon.data.remote.userRepository.UserRepository;

import javax.annotation.Signed;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public static UserRepository provideUserRepository(){
        return new UserRepository();
    }



}
