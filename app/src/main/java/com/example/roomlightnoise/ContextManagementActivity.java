package com.example.roomlightnoise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ContextManagementActivity extends AppCompatActivity {

    private TextView lightValue;
    private TextView noiseValue;

    private RequestQueue queue;

    private String room;
    private RoomContextState context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightValue = findViewById(R.id.textViewLightValue);
        noiseValue = findViewById(R.id.textViewNoiseValue);

        queue = Volley.newRequestQueue(this);

        ((Button) findViewById(R.id.buttonCheck)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                room = ((EditText) findViewById(R.id.editText1))
                        .getText().toString();
                retrieveRoomContextState(room);
            }
        });

        ((Button) findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnLightOnOff(room);
                retrieveRoomContextState(room);
            }
        });
    }

    private void turnLightOnOff(String room) {
        String url = "https://thawing-journey-78988.herokuapp.com/api/rooms/" + room + "/switch-light-and-list/";
        StringRequest postRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        error.printStackTrace();
                    }
                }
        );
        queue.add(postRequest);
    }

    private void retrieveRoomContextState(String room) {

        String url = "https://thawing-journey-78988.herokuapp.com/api/rooms/" + room + "/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String id = response.getString("id");
                            int lightLevel = Integer.parseInt(response.getJSONObject("light").get("level").toString());
                            String lightStatus = response.getJSONObject("light").get("status").toString();
                            int noiseLevel = Integer.parseInt(response.getJSONObject("noise").get("level").toString());
                            context = new RoomContextState(id,lightStatus,lightLevel,noiseLevel);
                            updateLight(context);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request);
    }

    public void updateLight(RoomContextState state) {
        lightValue.setText(": " + state.getLight());
        noiseValue.setText(": " + state.getNoise());
        if ((state.getStatus()).equals("ON")) {
            ((ImageView) findViewById(R.id.imageView1)).setImageResource(R.drawable.ic_bulb_on);
        } else if ((state.getStatus()).equals("OFF")){
            ((ImageView) findViewById(R.id.imageView1)).setImageResource(R.drawable.ic_bulb_off);
        }
    }


}
