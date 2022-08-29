package com.example.myweatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //Declare UI elements that will be updated in the app
    private TextInputEditText enterCity;
    private TextView dispCity, dispTemp, dispWeatherCond;
    private ImageView backGround, weatherIcon;

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
    }

    //onCLick method when search ImageView button is pressed (event listener)
    public void searchCityWeather(View view) {

        //Retrieve city name entered by user
        String cityName = enterCity.getText().toString().trim();

        //check to see if user has provided city name
        //TODO: add a way to make sure that the city name provided is valid -- maybe predefine all cities in world in string class??
        if(cityName.isEmpty()){
            //Display toast message telling user what to do
            Toast.makeText(this, "Enter City Name!", Toast.LENGTH_SHORT).show();
        }
        else{


            //Call on method to make API request for information from weatherapi.com server
            makeAPIRequest(cityName);
        }

    }

    public void makeAPIRequest(String cityName){

        //Instantiate Volley RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        //Initialize url containing api information (website + key + other parameters)
        String url = "http://api.weatherapi.com/v1/current.json?key=52c948f0a19443fc98540148222808&q=" + cityName + "&aqi=yes";

        //Request JSONObject response from the api (api provides JSON format for data)
        JsonObjectRequest  jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //display the result of the repsonse from app in the Logcat - just for debugging
//                Log.d("response", response.toString());

                //use try-catch block for JSON retrieval from api
                try{

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
                        if(current.getInt("is_day") == 1){
                            //use Picasso library to change background of app
//                            Picasso.get().load("https://media.istockphoto.com/photos/sun-on-blue-sky-picture-id1372419571?b=1&k=20&m=1372419571&s=170667a&w=0&h=4imKusTyijlQOKksfJsDPzAFHddtokz8u0axbbYQZkQ=").into(backGround);
                            Picasso.get().load("https://images.freeimages.com/images/large-previews/01a/on-a-cloudy-day-1554801.jpg").into(backGround);

                            //Change color of TextView UI elements
                            dispTemp.setTextColor(getResources().getColor(R.color.purple_700));
                            dispCity.setTextColor(getResources().getColor(R.color.purple_700));
                            dispWeatherCond.setTextColor(getResources().getColor(R.color.purple_700));
                        }
                        else{
                            //use Picasso library to change background of app
                            Picasso.get().load("https://wallpaperaccess.com/full/2113857.jpg").into(backGround);

                            //Change color of TextView UI elements
                            dispTemp.setTextColor(getResources().getColor(R.color.white));
                            dispCity.setTextColor(getResources().getColor(R.color.white));
                            dispWeatherCond.setTextColor(getResources().getColor(R.color.white));
                        }

                }
                catch (JSONException e){
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
}