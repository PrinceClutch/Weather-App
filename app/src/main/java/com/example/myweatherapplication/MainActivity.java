package com.example.myweatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Declare UI elements that will be updated in the app
    private TextInputEditText enterCity;
    private TextView dispCity, dispTemp, dispWeatherCond, dispLat, dispLong;
    private ImageView backGround, weatherIcon;

    //Declare FusedLocationProviderClient
    private FusedLocationProviderClient fusedLocationProviderClient;

    //Declare variables for storing latitude and longitude information
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign UI elements to the variables
        enterCity = findViewById(R.id.txtInputEdtEnterCityName);
        dispCity = findViewById(R.id.tvDispCityName);
        dispTemp = findViewById(R.id.txtViewTemp);
        dispWeatherCond = findViewById(R.id.txtViewCondition);
        weatherIcon = findViewById(R.id.imgViewConditionIcon);
        backGround = findViewById(R.id.imgViewBackGrnd);

        dispLat = findViewById(R.id.txtViewLattitude);
        dispLong = findViewById(R.id.txtViewLongitude);

        //Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

    }


    /*
        onCLick method when search ImageView button is pressed (event listener)
     */
    public void searchCityWeather(View view) {

        //Retrieve city name entered by user
        String cityName = enterCity.getText().toString().trim();

        //Check condition if permission given by user to use location
        //if permission is granted
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //When both permissions granted, call method
            getCurrentLocation();


        }
        //Otherwise if permission not granted then ask for permission
        else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
            100);

        }


        //check to see if user has provided city name
        //TODO: add a way to make sure that the city name provided is valid -- maybe predefine all cities in world in string class??
        if (cityName.isEmpty()) {
            //Display toast message telling user what to do
            Toast.makeText(this, "Enter City Name!", Toast.LENGTH_SHORT).show();
        } else {
            //Call on method to make API request for information from weatherapi.com server
            makeAPIRequest(cityName);
        }

    }

    /*
        Method makes request to API and parses JSON data retrieved
     */
    public void makeAPIRequest(String cityName) {

        //Instantiate Volley RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        //Initialize url containing api information (website + key + other parameters)
        String url = "http://api.weatherapi.com/v1/current.json?key=52c948f0a19443fc98540148222808&q=" + cityName + "&aqi=yes";

        //Request JSONObject response from the api (api provides JSON format for data)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //display the result of the repsonse from app in the Logcat - just for debugging
//                Log.d("response", response.toString());

                //use try-catch block for JSON retrieval from api
                try {

                    //Change the City Name TextView to display the city entered by user
                    dispCity.setText(cityName);

                    //Get weather condition string from api:
                    //Create JSON object to store "current" object from weatherapi
                    JSONObject current = response.getJSONObject("current");

                    //create JSON object that will store condition object in api
                    JSONObject condition = current.getJSONObject("condition");

                    //Display weather condition string to user
                    dispWeatherCond.setText(condition.getString("text"));

                    //Display the temperature of location
                    dispTemp.setText(current.getString("temp_c") + "°C");

                    //Display weather condition icon of location
                    String weathIcon = condition.getString("icon");
                    Picasso.get().load("http:".concat(weathIcon)).into(weatherIcon);

                    //Set the background of app to day or night based on the time of day
                    // from weatherapi.com if is_day = 0, then night else day
                    if (current.getInt("is_day") == 1) {
                        //use Picasso library to change background of app
//                            Picasso.get().load("https://media.istockphoto.com/photos/sun-on-blue-sky-picture-id1372419571?b=1&k=20&m=1372419571&s=170667a&w=0&h=4imKusTyijlQOKksfJsDPzAFHddtokz8u0axbbYQZkQ=").into(backGround);
                        Picasso.get().load("https://images.freeimages.com/images/large-previews/01a/on-a-cloudy-day-1554801.jpg").into(backGround);

                        //Change color of TextView UI elements
                        dispTemp.setTextColor(getResources().getColor(R.color.purple_700));
                        dispCity.setTextColor(getResources().getColor(R.color.purple_700));
                        dispWeatherCond.setTextColor(getResources().getColor(R.color.purple_700));
                    } else {
                        //use Picasso library to change background of app
                        Picasso.get().load("https://wallpaperaccess.com/full/2113857.jpg").into(backGround);

                        //Change color of TextView UI elements
                        dispTemp.setTextColor(getResources().getColor(R.color.white));
                        dispCity.setTextColor(getResources().getColor(R.color.white));
                        dispWeatherCond.setTextColor(getResources().getColor(R.color.white));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Show toast message to display api error report
                Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();

                //Change the display of the UI elements in the case of an error
                dispCity.setText("No Matching Location Found");
                dispCity.setTextColor(getResources().getColor(R.color.white));
                dispTemp.setText("");
                dispWeatherCond.setText("");

                //Make the weather icon transparent (not display anything)
                weatherIcon.setImageResource(android.R.color.transparent);

                //Make app background transparent
                backGround.setImageResource(android.R.color.transparent);

            }
        });

        //Add request to RequestQueue
        queue.add(jsonObjectRequest);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //When permission is granted call on getCurrentLocation
        if(requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            getCurrentLocation();
        }

        //When permission is not granted, display toast meassage
        else{
            Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {

        //Initialize location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check whether GPS or network provider are enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //When location service is enabled, get the last known location
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Initialize location
                    Location location = task.getResult();

                    //check if location is not null
                    if (location != null) {
                        //get latitude and longitude
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        //change textview displays
                        dispLat.setText(String.valueOf(location.getLatitude()));
                        dispLong.setText(String.valueOf(location.getLongitude()));


                    }

                    //If location is null, initialize location request
                    else {
                        //Initialize location request
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);

                        //Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
//                                super.onLocationResult(locationResult);
                                //Initialize location
                                Location location1 = locationResult.getLastLocation();

                                //Get latitude and longitude
                                latitude = location1.getLatitude();
                                longitude = location1.getLongitude();

                                //change textview displays
                                dispLat.setText(String.valueOf(location1.getLatitude()));
                                dispLong.setText(String.valueOf(location1.getLongitude()));

                            }
                        };

                        //Request location updates
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                    }
                }
            });
        }

        //When location service is disabled
        else {
            //Open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }


    }


}
