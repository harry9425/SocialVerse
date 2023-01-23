package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;

public class sharelocation extends FragmentActivity implements OnMapReadyCallback {

    Location currentlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    AlertDialog.Builder alertDialog;
    private static final int Request_code = 101;
    TextView name,indi,approx;
    private GoogleMap map;
    String  nametstring, uid, dp;
    DatabaseReference gettypo;
    CardView sendcurr;
    DatabaseReference mDatabase;
    SupportMapFragment supportMapFragment;
    ImageButton back,refresh,recenter,full;
    CircleImageView  circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharelocation);
        name=(TextView) findViewById(R.id.sendmapname);
        indi=(TextView) findViewById(R.id.onoffmap);
        circleImageView=(CircleImageView) findViewById(R.id.mapdp);
        name.setText(chatactivity.name);
        sendcurr=(CardView) findViewById(R.id.sendcurrentlocation);
        approx=(TextView) findViewById(R.id.approxmap);
        approx.setText("Accurate upto 20 meters");
        refresh=(ImageButton) findViewById(R.id.replaymap);
        recenter=(ImageButton) findViewById(R.id.recentermap);
        supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        recenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             onMapReady(map);
            }
        });
        full=(ImageButton) findViewById(R.id.fullscreenmap);
        refresh.setVisibility(View.VISIBLE);
        back=(ImageButton) findViewById(R.id.backbtnmap);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(statusOfGPS)
                {
                   fetchlastLocation();
                }
                else {
                    showSettingAlert();
                }
            }
        });
        gettypo= FirebaseDatabase.getInstance().getReference();
        gettypo = FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(chatactivity.uid);
        gettypo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = snapshot.child("presence").getValue().toString();
                if(val.equals("online"))
                    indi.setText("Active");
                else
                {
                    indi.setText("InActive");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        Picasso.get().load(chatactivity.dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                .into(circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(chatactivity.dp).placeholder(R.drawable.userdefaultdp).into(circleImageView);
                    }
                });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_code);
            return;
        }
        fetchlastLocation();
       manager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                2000,
                5, locationListenerGPS);

    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            approx.setText("Accurate upto "+location.getAccuracy()+" meters");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            fetchlastLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {
            showSettingAlert();
        }
    };

    private void fetchlastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_code);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
             if(location!=null)
             {
                 currentlocation=location;
                 Toast.makeText(sharelocation.this,currentlocation.getLatitude()+"",Toast.LENGTH_LONG).show();
                 supportMapFragment.getMapAsync(sharelocation.this);
             }
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map=googleMap;
        LatLng latLng=new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude());
        map.setBuildingsEnabled(true);
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("I am here");
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
        map.addMarker(markerOptions);
        sendcurr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage(String.valueOf(currentlocation.getLatitude()),String.valueOf(currentlocation.getLongitude()));

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case  Request_code:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    fetchlastLocation();
                } break;
        }
    }
    public void showSettingAlert()
    {
         alertDialog= new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS setting!");
        alertDialog.setMessage("GPS is not enabled, go to settings to enavle gps to acces this feature !! ");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent i=new Intent(sharelocation.this,chatactivity.class);
                startActivity(i);
                finish();
            }
        });
        alertDialog.show();
    }

    private void sendmessage(String lat, String lng)
    {
            mDatabase=FirebaseDatabase.getInstance().getReference().child("Chats");
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String randomkey = mDatabase.push().getKey();
            final messagemodel model = new messagemodel(mainpage.curuser);
            model.setTimestamp(System.currentTimeMillis());
            model.setFeelings(15);
            model.setMessage(lat+"&&"+lng);
            model.setImageurl("https://maps.googleapis.com/maps/api/staticmap?markers="+lat+","+lng+"&zoom=16&size=220x160&key=AIzaSyBfkN4KhPKZ19rYI9mOUsb-L9QNLPyWcB0");
            model.setChecker("location");
            mDatabase.child("Chats").child(mainpage.curuser).child(mainpage.curuser + chatactivity.uid).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + mainpage.curuser).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("lastmess", "You received location ping");
                            hashMap.put("lastmess time", model.getTimestamp());
                            mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + mainpage.curuser).child("AA details").setValue(hashMap);
                            hashMap.put("lastmess", "You sent location ping");
                            mDatabase.child("Chats").child(mainpage.curuser).child(mainpage.curuser+chatactivity.uid).child("AA details").setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    onBackPressed();
                                }
                            });
                        }
                    });
                }
            });
        }
    }
