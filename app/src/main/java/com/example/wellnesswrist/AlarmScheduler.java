package com.example.wellnesswrist;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlarmScheduler {
    private final Context context;
    private String sunriseTimeToday = null;

    public AlarmScheduler(Context context) {
        this.context = context;
        fetchSunriseTimeFromAPI(); // fetch once at service start
    }

    private void fetchSunriseTimeFromAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.sunrise-sunset.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SunriseApi api = retrofit.create(SunriseApi.class);
        Call<SunriseResponse> call = api.getSunrise();

        call.enqueue(new Callback<SunriseResponse>() {
            @Override
            public void onResponse(Call<SunriseResponse> call, Response<SunriseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String sunriseUTC = response.body().results.sunrise;
                    sunriseTimeToday = convertUTCToLocalTime(sunriseUTC);
                    Log.d("SunriseTime", "Converted Sunrise: " + sunriseTimeToday);
                }
            }

            @Override
            public void onFailure(Call<SunriseResponse> call, Throwable t) {
                Log.e("SunriseAPI", "Failed to fetch sunrise time", t);
            }
        });
    }

    private String convertUTCToLocalTime(String utcTime) {
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("hh:mm:ss a", Locale.US);
            utcFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcTime);

            SimpleDateFormat localFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            localFormat.setTimeZone(java.util.TimeZone.getDefault());
            return localFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void checkAndVibrateSunriseAlarm() {
        if (sunriseTimeToday == null) return;

        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = format.format(Calendar.getInstance().getTime());

        if (currentTime.equals(sunriseTimeToday)) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(2000); // fallback for older devices
                }
            }

            Toast.makeText(context, "Sunrise! Time to wake up!", Toast.LENGTH_SHORT).show();

            // prevent repeat vibration in same minute
            sunriseTimeToday = null;
        }
    }

    public void checkAndNotifyWaterReminder() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        if ((hour == 10 || hour == 14 || hour == 18) && minute < 2) {
            Toast.makeText(context, "Time to drink water!", Toast.LENGTH_SHORT).show();
        }
    }
}
