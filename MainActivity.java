package com.pp2.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.annotation.NonNull;
import android.widget.ImageView;


import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

//AIzaSyAj_HoTdTitEHrnvpAbuUy2Gl08ULmbN8M

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);

        final GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();

        client.connect();

        Thread thread = new Thread() {
            boolean flag = false;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            @Override
            public void run() {
                try {
                    while (true) {
                        ativarDND(client);
                        verificarAtividade(client);

                        if (notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_NONE && !flag) {
                            flag = true;
                            detectTime();
                            detectLocation(client);
                            defClasseLigado();
                        }
                        else if(notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_ALL && flag) {
                            flag = false;
                            detectTime();
                            detectLocation(client);
                            defClasseDesligado();
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        };
        thread.start();
    }

    public static void verifyStoragePermissions(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private boolean checkLocationPermission() {
        if( !hasLocationPermission() ) {
            Log.e("info", "Does not have location permission granted");
            requestLocationPermission();
            return false;
        }

        return true;
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED;
    }

    private final static int REQUEST_PERMISSION_RESULT_CODE = 42;

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                REQUEST_PERMISSION_RESULT_CODE );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_RESULT_CODE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Log.e("info", "Location permission denied.");
                }
            }
        }
    }

    private void detectLocation(GoogleApiClient client) {
        if( !checkLocationPermission() ) {
            return;
        }

        Awareness.SnapshotApi.getLocation(client)
                .setResultCallback(new ResultCallback<LocationResult>() {

                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        String local = "";

                        Location location = locationResult.getLocation();

                        File file = new File (Environment.getExternalStoragePublicDirectory("/") + "/Documents/data.csv");
                        FileOutputStream outputStream;

                        local = ","+location.getLatitude()+","+location.getLongitude()+"\n";

                        try {
                            verifyStoragePermissions(MainActivity.this);
                            outputStream = new FileOutputStream(file, true);
                            outputStream.write(local.getBytes());
                            outputStream.close();
                            Log.e("info", "local gravado!");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    public void detectTime() {
        String horario = "";
        Calendar rightNow = Calendar.getInstance();
        int hora = rightNow.get(Calendar.HOUR_OF_DAY);
        int min = rightNow.get(Calendar.MINUTE);
        int seg = rightNow.get(Calendar.SECOND);

        horario = ""+hora+":"+min+":"+seg+",";

        File file = new File (Environment.getExternalStoragePublicDirectory("/") + "/Documents/data.csv");
        FileOutputStream outputStream;

        try {
            verifyStoragePermissions(MainActivity.this);
            outputStream = new FileOutputStream(file, true);
            outputStream.write(horario.getBytes());
            outputStream.close();
            Log.e("info", "horario gravado!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void defClasseLigado() {
        String classe = "ligado";
        File file = new File (Environment.getExternalStoragePublicDirectory("/") + "/Documents/data.csv");
        FileOutputStream outputStream;

        try {
            verifyStoragePermissions(MainActivity.this);
            outputStream = new FileOutputStream(file, true);
            outputStream.write(classe.getBytes());
            outputStream.close();
            Log.e("info", "atributo classe ligado adicionada!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void defClasseDesligado() {
        String classe = "desligado";
        File file = new File (Environment.getExternalStoragePublicDirectory("/") + "/Documents/data.csv");
        FileOutputStream outputStream;

        try {
            verifyStoragePermissions(MainActivity.this);
            outputStream = new FileOutputStream(file, true);
            outputStream.write(classe.getBytes());
            outputStream.close();
            Log.e("info", "atributo classe desligado adicionada!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void changeInterruptionFilter(int interruptionFilter){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if(mNotificationManager.isNotificationPolicyAccessGranted()){

                mNotificationManager.setInterruptionFilter(interruptionFilter);
            }else {

                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    private void ativarDND(GoogleApiClient client) {
        if( !checkLocationPermission() ) {
            return;
        }

        Awareness.SnapshotApi.getLocation(client)
                .setResultCallback(new ResultCallback<LocationResult>() {

                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        Bayes bayes = new Bayes();
                        bayes.loadDataset();
                        String horario;
                        String resultado;

                        Calendar rightNow = Calendar.getInstance();
                        int hora = rightNow.get(Calendar.HOUR_OF_DAY);
                        int min = rightNow.get(Calendar.MINUTE);
                        int seg = rightNow.get(Calendar.SECOND);

                        Location location = locationResult.getLocation();

                        horario= ""+hora+":"+min+":"+seg;
                        resultado = bayes.inferir(horario, ""+location.getLatitude(), ""+location.getLongitude());
                        if(resultado.equals("ligado"))
                            changeInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                        else if(resultado.equals("desligado"))
                            changeInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                        Log.e("info", "Classe inferida: "+resultado);

                    }

                });
    }

    private void verificarAtividade(GoogleApiClient client) {
        Awareness.SnapshotApi.getDetectedActivity(client)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (detectedActivityResult.getStatus().isSuccess()) {
                            ActivityRecognitionResult activityRecognitionResult =
                                    detectedActivityResult.getActivityRecognitionResult();

                            int atividade = activityRecognitionResult.getMostProbableActivity().getType();
                            Log.e("info+", "Atividade: "+atividade);

                            if(atividade == 0) changeInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                        }
                    }
                });
    }
}


