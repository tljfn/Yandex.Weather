package sasd97.java_blog.xyz.yandexweather.data;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import org.threeten.bp.Instant;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.internal.operators.completable.CompletableFromAction;
import sasd97.java_blog.xyz.yandexweather.data.models.forecast.ResponseForecast16;
import sasd97.java_blog.xyz.yandexweather.data.models.forecast.ResponseForecast5;
import sasd97.java_blog.xyz.yandexweather.data.models.places.Place;
import sasd97.java_blog.xyz.yandexweather.data.models.places.PlaceDetailsResponse;
import sasd97.java_blog.xyz.yandexweather.data.models.places.PlacesResponse;
import sasd97.java_blog.xyz.yandexweather.data.net.PlacesApi;
import sasd97.java_blog.xyz.yandexweather.data.net.WeatherApi;
import sasd97.java_blog.xyz.yandexweather.data.storages.PlacesDao;
import sasd97.java_blog.xyz.yandexweather.data.storages.Storage;
import sasd97.java_blog.xyz.yandexweather.data.storages.WeatherDao;
import sasd97.java_blog.xyz.yandexweather.domain.models.WeatherModel;

import static sasd97.java_blog.xyz.yandexweather.WeatherApp.SPACE;
import static sasd97.java_blog.xyz.yandexweather.data.net.WeatherApi.days;

/**
 * Created by alexander on 12/07/2017.
 */

public final class AppRepositoryImpl implements AppRepository {

    private WeatherApi weatherApi;
    private PlacesApi placesApi;
    private Storage<String> cacheStorage;
    private Storage<String> prefsStorage;
    private Pair<String, String> apiKeys;
    private PlacesDao placesDao;
    private WeatherDao weatherDao;

    public AppRepositoryImpl(@NonNull PlacesDao placesDao,
                             @NonNull WeatherDao weatherDao,
                             @NonNull WeatherApi weatherApi,
                             @NonNull PlacesApi placesApi,
                             @NonNull Pair<String, String> apiKeys,
                             @NonNull Storage<String> cacheStorage,
                             @NonNull Storage<String> prefsStorage) {
        this.weatherDao = weatherDao;
        this.placesDao = placesDao;
        this.weatherApi = weatherApi;
        this.placesApi = placesApi;
        this.apiKeys = apiKeys;
        this.cacheStorage = cacheStorage;
        this.prefsStorage = prefsStorage;
    }

    @Override
    public Observable<WeatherModel> getWeather(@NonNull Place place) {
        return weatherApi
                .getWeather(place.getCoords().first, place.getCoords().second, apiKeys.first)
                .map(w -> new WeatherModel.Builder()
                        .city(w.getName())
                        .weatherId(w.getWeather().get(0).getId())
                        .humidity(w.getMain().getHumidity())
                        .pressure(w.getMain().getPressure())
                        .temperature(w.getMain().getTemp())
                        .minTemperature(w.getMain().getTempMin())
                        .maxTemperature(w.getMain().getTempMax())
                        .windDegree(w.getWind().getDegrees())
                        .windSpeed(w.getWind().getSpeed())
                        .clouds(w.getClouds().getPercentile())
                        .sunRiseTime(w.getSunsetAndSunrise().getSunriseTime() * 1000)
                        .sunSetTime(w.getSunsetAndSunrise().getSunsetTime() * 1000)
                        .updateTime(new Date().getTime())
                        .build());
    }

    @Override
    public Observable<ResponseForecast5> getForecast5(@NonNull Place place) {
        return weatherApi.getForecast5(
                place.getCoords().first, place.getCoords().second, apiKeys.first);
    }

    @Override
    public Observable<ResponseForecast16> getForecast16(@NonNull Place place) {
        return weatherApi.getForecast16(
                place.getCoords().first, place.getCoords().second, days, apiKeys.first);
    }

    @Override
    public Observable<PlacesResponse> getPlaces(@NonNull String s) {
        return placesApi.getPlaces(s, apiKeys.second);
    }

    @Override
    public Observable<PlaceDetailsResponse> getPlaceDetails(@NonNull String placeId) {
        return placesApi.getPlaceDetails(placeId, apiKeys.second);
    }

    @Override
    public Single<List<Place>> getFavoritePlaces() {
        return placesDao.getPlaces();
    }

    @Override
    public Completable insertPlace(Place place) {
        return Completable.fromAction(() -> placesDao.insertPlace(place));
    }

    @Override
    public Completable removePlaces(List<Place> places) {
        return new CompletableFromAction(() -> placesDao.removePlaces(places));
    }

    @Override
    public Single<List<WeatherModel>> getForecast(String placeId) {
        return weatherDao.getForecast(placeId);
    }

    @Override
    public Completable insertForecast(List<WeatherModel> forecast) {
        return Completable.fromAction(() -> weatherDao.insertForecast(forecast));
    }

    @Override
    public Completable removeForecast(String placeId) {
        return new CompletableFromAction(() -> weatherDao.removeForecast(placeId));
    }

    @Override
    public String getCachedWeather(@NonNull Place place) {
        return this.cacheStorage.getString(toFileName(place), null);
    }

    @Override
    public void saveWeatherToCache(@NonNull Place place, @NonNull String json) {
        cacheStorage.put(toFileName(place), json);
    }

    @Override
    public boolean isBackgroundServiceEnabled() {
        return prefsStorage.getBoolean(BACKGROUND_SERVICE_PREFS_KEY, true);
    }

    @Override
    public boolean switchBackgroundServiceState() {
        boolean mode = isBackgroundServiceEnabled();
        prefsStorage.put(BACKGROUND_SERVICE_PREFS_KEY, !mode);
        return !mode;
    }

    @Override
    public Completable savePlace(@NonNull Place place) {
        return new CompletableFromAction(() -> prefsStorage.put(PLACE_PREFS_KEY, place));
    }

    @Override
    public Place getPlace() {
        String s = prefsStorage.getString(PLACE_PREFS_KEY, "");
        String placeName = s.split("\\*\\*\\*")[0];
        String[] objects = s.split(SPACE);
        /*"some_city_coord1_coord2".split("_").length >= 3*/
        if (objects.length < 3){
            return new Place("ChIJybDUc_xKtUYRTM9XV8zWRD0", "Москва", new Pair<>(0.0, 0.0), (int) Instant.now().getEpochSecond());
        }
        String c1 = objects[objects.length - 3];
        String c2 = objects[objects.length - 2];
        String placeId = objects[objects.length - 1];
        return new Place(placeId , placeName,
                new Pair<>(Double.valueOf(c1), Double.valueOf(c2)), (int) Instant.now().getEpochSecond());
    }

    @Override
    public void saveWeatherUpdateInterval(int minutes) {
        prefsStorage.put(WEATHER_UPDATE_INTERVAL_PREFS_KEY, minutes);
    }

    @Override
    public int getWeatherUpdateInterval() {
        return prefsStorage.getInteger(WEATHER_UPDATE_INTERVAL_PREFS_KEY, 15);
    }

    @Override
    public void saveTemperatureUnits(int units) {
        prefsStorage.put(UNITS_TEMPERATURE_PREFS_KEY, units);
    }

    @Override
    public int getTemperatureUnits() {
        return prefsStorage.getInteger(UNITS_TEMPERATURE_PREFS_KEY, 0);
    }

    @Override
    public void savePressureUnits(int units) {
        prefsStorage.put(UNITS_PRESSURE_PREFS_KEY, units);
    }

    @Override
    public int getPressureUnits() {
        return prefsStorage.getInteger(UNITS_PRESSURE_PREFS_KEY, 0);
    }

    @Override
    public void saveSpeedUnits(int units) {
        prefsStorage.put(UNITS_SPEED_PREFS_KEY, units);
    }

    @Override
    public int getSpeedUnits() {
        return prefsStorage.getInteger(UNITS_SPEED_PREFS_KEY, 0);
    }

    public static String toFileName(@NonNull Place place) {
        return place.getName().replaceAll(SPACE, "_").replaceAll(",", "").toLowerCase();
    }
}
