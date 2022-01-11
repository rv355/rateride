package com.example.RateRide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements LocationListener{

    String myfolder = Environment.getExternalStorageDirectory() + "/";
    String myfolder1;
    File root = Environment.getExternalStorageDirectory();
    FileWriter fw = null;
    public static final String TAG = "Testing";
    private long lastupdate = 0;
    private float last_x, last_y, last_z;
    private static final int shake_threshhold = 600;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;
    private RadioGroup radioGroup3;
    private Button button;
    private Button exitbutton;
    private RatingBar ratingbar;
    String app_feedback;
    Calendar calendar;
    SimpleDateFormat sdf;


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.button);
        exitbutton = (Button)findViewById(R.id.button2);
        radioGroup1 = (RadioGroup)findViewById(R.id.radiogroup1);
        radioGroup2 = (RadioGroup)findViewById(R.id.radiogroup2);
        radioGroup3 = (RadioGroup)findViewById(R.id.radiogroup3);
        //Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        //String utctime =cal.getTime().toString();
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//Here you say to java the initial timezone. This is the secret
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//Will print in UTC
        String utctime = sdf.format(calendar.getTime());
        //Toast.makeText(this, "hello1", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, utctime, Toast.LENGTH_SHORT).show();

        String text = "Hello world";
        //Toast.makeText(this, myfolder, Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Log.v(TAG, "using location");
       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Log.v(TAG, "using location1");
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },1);
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION },1);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
           /* if (ContextCompat.checkSelfPermission(thisActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(thisActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        0x0)*/
          return;
       }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Log.v(TAG, "using location2");

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }
        //making a directoory
        File folder =new File(root,"/RateRide/");
        File folder1;
        if(!folder.exists())
        {
            folder.mkdirs();
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException e){
                e.printStackTrace();
        }
        }
        //else{

        //}
            //Toast.makeText(this, folder.toString()+" exists", Toast.LENGTH_SHORT).show();

        //}
        folder1 = new File(folder, "/"+utctime+"/");
        if(!folder1.exists())
        {
            folder1.mkdirs();
            try {
                Thread.sleep(500);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        myfolder1 = folder1.toString()+"/";

        //Toast.makeText(this, myfolder1+" exists", Toast.LENGTH_SHORT).show();

        /*************************************** UI data *************************************************************/
        radioGroup1.clearCheck();
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Get the selected Radio Button
                RadioButton
                        radioButton1
                        = (RadioButton)group
                        .findViewById(checkedId);
            }
        });
        radioGroup2.clearCheck();
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton
                        radioButton2
                        = (RadioButton)group
                        .findViewById(checkedId);
            }
        });
        radioGroup3.clearCheck();
        radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton
                        radioButton3
                        = (RadioButton)group
                        .findViewById(checkedId);
            }
        });



       ratingbar = findViewById(R.id.ratingBar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Your response has been recorded", Toast.LENGTH_SHORT).show();

                try {



                    float rating = ratingbar.getRating();
                    if (rating == 0.0f) {
                        fw = new FileWriter(myfolder1 + "\\myappfeedback.txt", true);
                        fw.write("\n"+ String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) +" selected rating -");
                        fw.close();
                        //app_feedback+= "selected rating - ";
                       // Toast.makeText(MainActivity.this, "No answer has been selected", Toast.LENGTH_SHORT).show();

                    } else {
                        fw = new FileWriter(myfolder1 + "\\myappfeedback.txt", true);
                        //Toast.makeText(MainActivity.this, "selected rating " + rating, Toast.LENGTH_SHORT).show();
                        //app_feedback+= "selected rating "+rating;
                        fw.write("\n"+ String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) +" selected rating " + rating);
                        ratingbar.setRating(0.0f);
                        fw.close();
                    }

                    //Get the selected Radio Button
                    int selectedId1 = radioGroup1.getCheckedRadioButtonId();
                    if (selectedId1 == -1) {
                        //Toast.makeText(MainActivity.this, "No answer has been selected", Toast.LENGTH_SHORT).show();
                        fw = new FileWriter(myfolder1 + "\\myappfeedback.txt", true);
                        fw.write(", Overspeeding -");
                        fw.close();
                        //app_feedback+= ", Overspeeding - ";
                    } else {
                        fw = new FileWriter(myfolder1 + "\\myappfeedback.txt", true);

                        RadioButton radioButton1
                                = (RadioButton) radioGroup1
                                .findViewById(selectedId1);

                        //app_feedback+= ", Overspeeding "+ radioButton1.getText();
                        // Now display the value of selected item
                        // by the Toast message
                       // Toast.makeText(MainActivity.this, radioButton1.getText(), Toast.LENGTH_SHORT).show();
                        fw.write(", Overspeeding " + radioButton1.getText());
                        radioGroup1.clearCheck();
                        fw.close();

                    }

                    int selectedId2 = radioGroup2.getCheckedRadioButtonId();
                    if (selectedId2 == -1) {
                        fw = new FileWriter(myfolder1 + "\\myappfeedback.txt", true);
                        fw.write(", Interaction with Pothole/Speed Breaker -");
                        fw.close();
                        //app_feedback+= ", Interaction with Pothole/Speed Breaker - ";
                     //   Toast.makeText(MainActivity.this, "No answer has been selected", Toast.LENGTH_SHORT).show();
                    } else {
                        fw = new FileWriter(myfolder1 + "\\myappfeedback.txt", true);

                        RadioButton radioButton2
                                = (RadioButton) radioGroup2
                                .findViewById(selectedId2);

                        // Now display the value of selected item
                        // by the Toast message
                       // Toast.makeText(MainActivity.this, radioButton2.getText(), Toast.LENGTH_SHORT).show();
                        //app_feedback+= ", Interaction with Pothole/Speed Breaker "+radioButton2.getText();
                        fw.write(", Interaction with Pothole/Speed Breaker " + radioButton2.getText());
                        radioGroup2.clearCheck();
                        fw.close();

                    }

                    int selectedId3 = radioGroup3.getCheckedRadioButtonId();
                    if (selectedId3 == -1) {
                        fw = new FileWriter(myfolder1 + "\\myappfeedback.txt", true);
                        fw.write(", Dangerous Maneuvers -");
                        fw.close();
                        //app_feedback+= ", Dangerous Maneuvers -";
                        //Toast.makeText(MainActivity.this, "No answer has been selected", Toast.LENGTH_SHORT).show();
                    } else {
                        fw = new FileWriter(myfolder1 + "\\myappfeedback.txt", true);

                        RadioButton radioButton3
                                = (RadioButton) radioGroup3
                                .findViewById(selectedId3);

                        // Now display the value of selected item
                        // by the Toast message
                        //Toast.makeText(MainActivity.this,radioButton3.getText(), Toast.LENGTH_SHORT).show();
                        //app_feedback+= ", Dangerous Maneuvers "+radioButton3.getText();
                        fw.write(", Dangerous Maneuvers " + radioButton3.getText());
                        radioGroup3.clearCheck();
                        fw.close();

                    }
                    //fw = new FileWriter(myfolder1 + "\\myappfeedback.txt", true);
                   // fw.write("\n"+String.valueOf(Calendar.getInstance().getTime())+ app_feedback);
                   // fw.close();

                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        exitbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "exit", Toast.LENGTH_SHORT).show();
                        finish();
                        System.exit(0);



                    }});
        /***************************************************Proximity data ***************************************************************************************************/

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //try {
          //  fw = new FileWriter(myfolder + "\\myapp.txt", true);
            //fw.write("\nButton Pressed");
            //Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();

            //fw.close();

        //} catch (Exception e) {
          //  e.printStackTrace();

        //}

        if (proximitySensor == null) {
            // int proximity_sensor_not_available =
            final int proximity_sensor_not_available = Log.d(TAG, "proximity sensor not available");
            finish();
        }

        SensorEventListener proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[0] < proximitySensor.getMaximumRange()) {
                    //getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                    try {
                        fw = new FileWriter(myfolder1 + "\\myapp.txt", true);

                        fw.write("P:" + String.valueOf(sensorEvent.values[0]) + "\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    //getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                    try {
                        fw = new FileWriter(myfolder1 + "\\myapp.txt", true);

                        fw.write("P:" + String.valueOf(sensorEvent.values[0]) + "\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(proximitySensorListener, proximitySensor, 2 * 1000 * 1000);


        //SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /***************************************************Gyroscope data ***************************************************************************************************/

        final Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if(gyroscopeSensor == null)
        {
            Toast.makeText(this,"gyroscope not found", Toast.LENGTH_SHORT).show();
        }
        SensorEventListener gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[0] > 0.5f) {
                    //Toast.makeText(this,"gyroscope not found", Toast.LENGTH_SHORT).show();
                    //getWindow().getDecorView().setBackgroundColor(Color.RED);

                    try {
                        fw = new FileWriter(myfolder1 + "\\Gyroscopedata.txt", true);

                        fw.write("\nG:" + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) + ": " + " X: " + String.valueOf(sensorEvent.values[0]) + "\n");
                        fw.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else if (sensorEvent.values[0] < -0.5f) {
                   // getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    try {
                        fw = new FileWriter(myfolder1 + "\\Gyroscopedata.txt", true);

                        fw.write("\nG:" + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) + ": " + " X: " + String.valueOf(sensorEvent.values[0]) + "\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (sensorEvent.values[1] < -0.5f) {
                   // getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    try {
                        fw = new FileWriter(myfolder1 + "\\Gyroscopedata.txt", true);

                        fw.write("\nG:" + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) + ": " + " Y: " + String.valueOf(sensorEvent.values[1]) + "\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (sensorEvent.values[1] < -0.5f) {
                    //getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    try {
                        fw = new FileWriter(myfolder1 + "\\Gyroscopedata.txt", true);

                        fw.write("\nG:" + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) + ": " + " Y: " + String.valueOf(sensorEvent.values[1]) + "\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (sensorEvent.values[2] < -0.5f) {
                    //getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    try {
                        fw = new FileWriter(myfolder1 + "\\Gyroscopedata.txt", true);

                        fw.write("\nG:" + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) + ": " + " Z: " + String.valueOf(sensorEvent.values[2]) + "\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (sensorEvent.values[2] < -0.5f) {
                   // getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                    try {
                        fw = new FileWriter(myfolder1 + "\\Gyroscopedata.txt", true);

                        fw.write("\nG:" + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) + ": " + "Z: " + String.valueOf(sensorEvent.values[2]) + "\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        //sensorManager.registerLi

        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, sensorManager.SENSOR_DELAY_NORMAL);

        /***************************************************Accelerometer data ***************************************************************************************************/
        final Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) {
            finish();
        }
        SensorEventListener accelerometersensor = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                Sensor mysensor = sensorEvent.sensor;
                if (mysensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = sensorEvent.values[0];
                    float y = sensorEvent.values[1];
                    float z = sensorEvent.values[2];
                   /* long cur_time = System.currentTimeMillis();
                    if ((cur_time - lastupdate) > 100) {
                        long diff_time = cur_time - lastupdate;
                        lastupdate = cur_time;
                        float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diff_time * 10000;
                        if (speed > shake_threshhold) {
                        }
                        last_x = x;
                        last_y = y;
                        last_z = z;*/
                    try {
                        fw = new FileWriter(myfolder1 + "\\Accel.txt", true);

                        fw.write("\nA:" + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) + ": " + "X: " + String.valueOf(x) + " Y: " + String.valueOf(y) + " Z: " + String.valueOf(z) + "\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                if (mysensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

                    float t1 = sensorEvent.values[0];
                    float t2 = sensorEvent.values[1];
                    float t3 = sensorEvent.values[2];
                    try {
                        fw = new FileWriter(myfolder1 + "\\Accel.txt", true);

                        fw.write("\nLA: " + String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) + ": " + " X: " + String.valueOf(t1) + " Y: " + String.valueOf(t2) + " Z: " + String.valueOf(t3) + "\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }



           @Override
           public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(accelerometersensor, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onLocationChanged(Location location) {
        String msg = ("Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude()+", Speed: "+ location.getSpeed());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        try {
            fw = new FileWriter(myfolder1 + "\\gpsdata.txt", true);
            fw.write("\nTimestamp: "+ String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()/1000) +" GPS: "+ msg);

            fw.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v(TAG, "Latitude");

    }

    @Override
    public void onProviderEnabled(String provider) {

        Log.v(TAG, "Latitude");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v(TAG,"Latitude");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults,
                this);
    }
   /*@Override protected void onStop(){
        super.onStop();
       finish();

    }*/
}



