package sasd97.java_blog.xyz.yandexweather.di.modules;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import sasd97.java_blog.xyz.yandexweather.data.AppRepository;
import sasd97.java_blog.xyz.yandexweather.data.AppRepositoryImpl;
import sasd97.java_blog.xyz.yandexweather.data.net.WeatherApi;
import sasd97.java_blog.xyz.yandexweather.data.storages.CacheStorage;
import sasd97.java_blog.xyz.yandexweather.data.storages.PrefsStorage;
import sasd97.java_blog.xyz.yandexweather.utils.RxSchedulersImpl;
import sasd97.java_blog.xyz.yandexweather.utils.RxSchedulers;

/**
 * Created by alexander on 09/07/2017.
 */

@Module
public class AppModule {

    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    @NonNull
    public Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public RxSchedulers provideSchedulers() {
        return new RxSchedulersImpl();
    }

    @Provides
    @Singleton
    public AppRepository provideRepository(WeatherApi api,
                                           PrefsStorage prefsStorage,
                                           CacheStorage cacheStorage) {
        return new AppRepositoryImpl(api, cacheStorage, prefsStorage);
    }
}