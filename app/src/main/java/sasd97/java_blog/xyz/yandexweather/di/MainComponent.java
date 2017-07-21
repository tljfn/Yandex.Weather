package sasd97.java_blog.xyz.yandexweather.di;

import dagger.Subcomponent;
import sasd97.java_blog.xyz.yandexweather.di.modules.ConvertersModule;
import sasd97.java_blog.xyz.yandexweather.di.modules.MainModule;
import sasd97.java_blog.xyz.yandexweather.di.modules.WeatherTypesModule;
import sasd97.java_blog.xyz.yandexweather.di.scopes.MainScope;
import sasd97.java_blog.xyz.yandexweather.presentation.main.MainActivity;
import sasd97.java_blog.xyz.yandexweather.presentation.main.MainPresenter;
import sasd97.java_blog.xyz.yandexweather.presentation.settings.SelectWeatherUpdateIntervalPresenter;
import sasd97.java_blog.xyz.yandexweather.presentation.settings.SettingsPresenter;
import sasd97.java_blog.xyz.yandexweather.presentation.weather.WeatherPresenter;

/**
 * Created by alexander on 09/07/2017.
 */

@Subcomponent(modules = {MainModule.class, ConvertersModule.class, WeatherTypesModule.class})
@MainScope
public interface MainComponent {
    void inject(MainActivity activity);

    MainPresenter getMainPresenter();

    WeatherPresenter getWeatherPresenter();

    SettingsPresenter getSettingsPresenter();

    SelectWeatherUpdateIntervalPresenter getSelectIntervalPresenter();
}