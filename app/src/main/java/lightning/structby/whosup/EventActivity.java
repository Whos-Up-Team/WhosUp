package lightning.structby.whosup;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends Activity {

    private static final String TAG = "AddEventActivity";

    String placeName;
    String placeAddress;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    String uid;

    EditText eventNameEditText;
    EditText descriptionEditText;
    EditText dateEditText;
    EditText timeEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();



        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                placeName = place.getName().toString();
                placeAddress = place.getAddress().toString();
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Address: " + place.getAddress());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        Button button = (Button)findViewById(R.id.addbutton);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                eventNameEditText = (EditText)findViewById(R.id.eventNameeditText);
                descriptionEditText = (EditText)findViewById(R.id.descriptioneditText);
                dateEditText = (EditText)findViewById(R.id.dateeditText);
                timeEditText = (EditText)findViewById(R.id.timeeditText);

                String eventNameString = eventNameEditText.getText().toString();
                String descriptionString = descriptionEditText.getText().toString();
                String dateString = dateEditText.getText().toString();
                String timeString = timeEditText.getText().toString();

                List<String> peopleAttending = new ArrayList<>();
                peopleAttending.add(uid);
                peopleAttending.add("1234");

                Event newEvent = new Event(eventNameString, descriptionString, dateString, timeString, placeName, placeAddress, peopleAttending);

                String pushKey = myRef.child("Events").push().getKey();

                for(int i = 0; i < newEvent.peopleAttending.size(); i++){
                    myRef.child("Events").child(pushKey).child("peopleAttending").push().setValue(newEvent.peopleAttending.get(i));
                }
                myRef.child("Events").child(pushKey).setValue(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }


}
