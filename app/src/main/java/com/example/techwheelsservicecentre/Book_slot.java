package com.example.techwheelsservicecentre;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Book_slot extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener, View.OnClickListener {
    TextView tv,selecttv;
    private Button  bookslot;
    private String recive_email;
    private TextView date;
    private String setslot;
    //private TextView settime;
    private TextView vehiclemodel;
    private TextView regno,AvailSlots ;
    private  Spinner spinner;
    private String item;
    private  static FirebaseFirestore db= FirebaseFirestore.getInstance();
    private static String TAG="Firebasse Retrive";
    private static List<String> slottime;
    private static String date1;
    public static int s1,s2,s3,s4;
    private static AutoCompleteTextView autoCompleteTextView;
    private static ArrayAdapter<String> arrayAdapter;
    private static String [] cars;
    private static String vechModel;
    private String regnopattern;
    private String availcount;
    private NotificationManagerCompat notificationCompat;
    private Uri alarmsound;
    private long[] vibrate={0,100,200,300};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_slot);
        spinner=(Spinner)findViewById(R.id.spinner);
        alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notificationCompat= NotificationManagerCompat.from(this);

        availcount="0";
        ArrayAdapter<String> mya=new ArrayAdapter<String>(Book_slot.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinner));
        mya.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mya);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();
                AvailSlots = (TextView) findViewById(R.id.availslot);
                if (item.equals("9AM-11AM")) {
                    AvailSlots.setText("Available Slots:" + s1);
                    availcount=Integer.toString(s1);

                } else if (item.equals("11AM-1PM")) {
                    AvailSlots.setText("Available Slots:" + s2);
                    availcount=Integer.toString(s2);
                } else if (item.equals("2PM-4PM")) {
                    AvailSlots.setText("Available Slots:" + s3);
                    availcount=Integer.toString(s3);
                } else if (item.equals("4PM-6PM")) {
                    AvailSlots.setText("Available Slots:" + s4);
                    availcount=Integer.toString(s4);
                }
                else if (item.equals("Select time-slot")) {
                    AvailSlots.setText("Available slots:");
                }

                if (availcount.equals("0")&& position!=0) {
                    bookslot.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"Sorry! No Available Slot",Toast.LENGTH_LONG).show();

                }

                else {
                    bookslot.setEnabled(true);
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setslot=spinner.getSelectedItem().toString();
        Toast.makeText(getApplicationContext(),setslot,Toast.LENGTH_SHORT).show();
        bookslot= (Button)findViewById(R.id.confirmSlot);
        date = (TextView) findViewById(R.id.date);
        //settime = (TextView) findViewById(R.id.settime);

        regno = (TextView) findViewById(R.id.regno);

        selecttv=(TextView)findViewById(R.id.dateselect);
        selecttv.setMovementMethod(LinkMovementMethod.getInstance());
        selecttv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment=new com.example.techwheelsservicecentre.DatePicker();
                dialogFragment.show(getSupportFragmentManager(), "date picker");
            }
        });


        Intent intent=getIntent();
        recive_email=intent.getStringExtra("key");
        bookslot.setOnClickListener(this);
        autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.vehiclemodel);
        cars=getResources().getStringArray(R.array.cars);
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cars);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vechModel = parent.getItemAtPosition(position).toString();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        tv=(TextView)findViewById(R.id.date);
        date1= DateFormat.getDateInstance().format(c.getTime());
        tv.setText(date1);
        myslots();

    }



    @Override
    public void onClick(View v) {

        //trying notifications
        setslot=spinner.getSelectedItem().toString();

        if(setslot.equals("Select time-slot"))
        {
            Toast.makeText(getApplicationContext(),"Select Time Slot",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(vechModel)){
            Toast.makeText(getApplicationContext(),"Select Vehicle Model",Toast.LENGTH_LONG).show();
            return;
        }

        final String date1=date.getText().toString();
        final String regno1=regno.getText().toString().trim();
        regnopattern="^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$";
        if(TextUtils.isEmpty(regno1)|| !regno1.matches(regnopattern)){
            Toast.makeText(getApplicationContext(),"Enter Valid Registration no.",Toast.LENGTH_LONG).show();
            return;




        }
        // Log.d(TAG,date1);

        //Toast.makeText(getApplicationContext(),date1,Toast.LENGTH_SHORT).show();


        // FirebaseFirestore db= FirebaseFirestore.getInstance();
        Map<String, String> User = new HashMap<>();
/*

        User.put("9AM-11AM-1", "");
        User.put("9AM-11AM-2", "");
        User.put("9AM-11AM-3", "");
        User.put("9AM-11AM-4", "");
        User.put("11AM-1PM-1", "");
        User.put("11AM-1PM-2", "");
        User.put("11AM-1PM-3", "");
        User.put("11AM-1PM-4", "");
        User.put("2PM-4PM-1", "");
        User.put("2PM-4PM-2", "");
        User.put("2PM-4PM-3", "");
        User.put("2PM-4PM-4", "");
        User.put("4PM-6PM-1", "");
        User.put("4PM-6PM-2", "");
        User.put("4PM-6PM-3", "");
        User.put("4PM-6PM-4", "");




        db.collection("Slots").document(date1)
                .set(User)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error writing document", e);
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();


                    }
                });
*/
        String setslot="";
        if(item.equals("9AM-11AM"))
        {
            for(int j=1,k=4;j<=4;j++,k--){
                if(s1==j){
                    setslot=item+"-"+k;
                    break;
                }
            }
        }
        if(item.equals("11AM-1PM"))
        {
            for(int j=1,k=4;j<=4;j++,k--){
                if(s2==j){
                    setslot=item+"-"+k;
                    break;
                }

            }
        }
        if(item.equals("2PM-4PM"))
        {
            for(int j=1,k=4;j<=4;j++,k--){
                if(s3==j){
                    setslot=item+"-"+k;
                    break;
                }

            }
        }
        if(item.equals("4PM-6PM"))
        {
            for(int j=1,k=4;j<=4;j++,k--){
                if(s4==j){
                    setslot=item+"-"+k;
                    break;
                }

            }
        }


        //setslot=item+"";
        //User.put(setslot,recive_email);

        final String finalSetslot = setslot;
        db.collection("Slots").document(date1)
                .update(setslot,recive_email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");


                        EditText regNo=(EditText) findViewById(R.id.regno);
                        String regVAr=regNo.getText().toString();
                        Map<String, Object> His = new HashMap<>();
                        His.put("timestamp", FieldValue.serverTimestamp());
                        His.put("email",recive_email);
                        His.put("date",date1);
                        His.put("timeslot",item);
                        His.put("vehicleModel",vechModel);
                        His.put("regno",regVAr);
                        His.put("status","Applied for Servicing");

                        db.collection("History")
                                .add(His)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        Toast.makeText(getApplicationContext(),"Slot Booked",Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(Book_slot.this,Dashboard_TW.class);
                                        PendingIntent pendingIntent = (PendingIntent) PendingIntent.getActivity(Book_slot.this,0,intent,0);
                                        startActivity(intent);


                                        Notification notification=new NotificationCompat.Builder(Book_slot.this,App_notify.chanelid)
                                                .setSmallIcon(R.drawable.iconic)
                                                .setContentTitle(" Booking Confirmed with Tech Wheels ")
                                                .setContentText(" ! Tech Wheels is Happy to Serve ! ")
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                .setContentIntent(pendingIntent)
                                                .setAutoCancel(true)
                                                .setColor(Color.BLACK)
                                                .setStyle(new NotificationCompat.InboxStyle()
                                                        .addLine("CAR :"+vechModel)
                                                        .addLine("Reg no: "+regno1)
                                                        .addLine("Time: "+ finalSetslot)
                                                        .addLine("Date :"+date1)
                                                        .setBigContentTitle("Details:"))
                                                .setSound(alarmsound)
                                                .setVibrate(vibrate)
                                                .build();

                                        notificationCompat.notify(1,notification);



                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Log.w(TAG, "Error writing document", e);
                                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();


                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error writing document", e);
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();


                    }
                });

        /*DocumentReference docRef = db.collection("Slots").document(date1);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData())
                        // ;
                        String check;
                        for(int i=1;i<=4;i++){
                            check=setslot+"-"+i;
                            if(document.getString(check)==null){
}
                        }
                        document.getString("");
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        */
    }


    private static void myslots(){
        s1=s2=s3=s4=0;
        slottime=new LinkedList<String>();
        slottime.add("9AM-11AM-1");
        slottime.add("9AM-11AM-2");
        slottime.add("9AM-11AM-3");
        slottime.add("9AM-11AM-4");

        slottime.add("11AM-1PM-1");
        slottime.add("11AM-1PM-2");;
        slottime.add("11AM-1PM-3");
        slottime.add("11AM-1PM-4");

        slottime.add("2PM-4PM-1");
        slottime.add("2PM-4PM-2");
        slottime.add("2PM-4PM-3");
        slottime.add("2PM-4PM-4");

        slottime.add("4PM-6PM-1");
        slottime.add("4PM-6PM-2");
        slottime.add("4PM-6PM-3");
        slottime.add("4PM-6PM-4");

        DocumentReference docRef = db.collection("Slots").document(date1);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        //int cnt=0;
                        Iterator itr=slottime.iterator();

                        while (itr.hasNext()){
                            String x=itr.next().toString(),y;
                            if((y=document.getString(x))==""){
                                if(x.startsWith("9AM")){
                                    s1++;
                                }
                                if (x.startsWith("11AM")){
                                    s2++;
                                }

                                if (x.startsWith("2PM")){
                                    s3++;
                                }

                                if (x.startsWith("4PM")){
                                    s4++;
                                }
                                Log.d("one",Integer.toString(s1));
                                Log.d("two",Integer.toString(s2));
                                Log.d("three",Integer.toString(s3));
                                Log.d("four",Integer.toString(s4));
                            }
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

//        Log.d("Date",date)   ;



    }
}