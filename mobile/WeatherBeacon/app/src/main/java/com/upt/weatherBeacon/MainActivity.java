package com.upt.weatherBeacon;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.upt.weatherBeacon.AppState.GlobalState;
import com.upt.weatherBeacon.data.remote.WeatherRepository.Dto.WeatherForecasts;
import com.upt.weatherBeacon.data.remote.WeatherRepository.OpenMeteoApi;
import com.upt.weatherBeacon.databinding.ActivityMainBinding;
import com.upt.weatherBeacon.di.Config;
import com.upt.weatherBeacon.ui.base.BaseActivity;
import com.upt.weatherBeacon.ui.base.BaseViewModel;
import com.upt.weatherBeacon.ui.base.Navigation;
import com.upt.weatherBeacon.ui.base.navigation.NavAttribs;
import com.upt.weatherBeacon.ui.base.navigation.Screen;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@AndroidEntryPoint
public class MainActivity extends BaseActivity<BaseViewModel> {

    private ActivityMainBinding binding;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private GlobalState appState = GlobalState.getState();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        // Initialize LocationManager and LocationListener
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is disabled, show alert dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Enable GPS");
            alertDialogBuilder.setMessage("This app requires GPS to function properly. Please enable GPS in your device settings.");
            alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


//        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener,null);
        locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 50, 5000, locationListener, null);



    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void setupUi() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        viewModel.uiEventStream.setValue(new Navigation(new NavAttribs(Screen.LoginScreen, null, false)));

        viewModel.uiEventStream.setValue(new Navigation(new NavAttribs(Screen.SplashScreen, null, false)));
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            // Get latitude and longitude
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            System.out.println("COORDINATES: "+latitude+ " "+ longitude);
            appState.setLatitude(latitude);
            appState.setLongitude(longitude);


            // Initialize Geocoder
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {
                // Get city name from latitude and longitude
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    String cityName = addresses.get(0).getLocality();
                    // Print city name
                    Log.d("CityName", "Current City: " + cityName);
                    appState.setCity(cityName);
//                    Toast.makeText(MainActivity.this, "Current City: " + cityName, Toast.LENGTH_LONG).show();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted

            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // Permission denied
            Toast.makeText(this, "Location permission denied. Cannot retrieve current city.", Toast.LENGTH_SHORT).show();
        }
    }
}







