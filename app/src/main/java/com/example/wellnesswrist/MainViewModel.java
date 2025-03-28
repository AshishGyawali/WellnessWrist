package com.example.wellnesswrist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public MutableLiveData<String> status = new MutableLiveData<>("Welcome to Wellness Wrist");
}