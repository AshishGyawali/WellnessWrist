package com.example.wellnesswrist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    public MutableLiveData<Boolean> monitorPosture = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> drinkWater = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> sunriseAlarm = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> ambientNoise = new MutableLiveData<>(false);
}