package com.example.wellnesswrist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.Calendar;

public class MainViewModel extends ViewModel {
    public MutableLiveData<String> greeting = new MutableLiveData<>();

    public MainViewModel() {
        updateGreeting();
    }

    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            greeting.setValue("Good Morning!");
        } else if (hour >= 12 && hour < 17) {
            greeting.setValue("Good Afternoon!");
        } else {
            greeting.setValue("Good Evening!");
        }
    }
}