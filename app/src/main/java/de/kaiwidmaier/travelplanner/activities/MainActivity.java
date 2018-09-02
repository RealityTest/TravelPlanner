package de.kaiwidmaier.travelplanner.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import de.kaiwidmaier.travelplanner.R;
import de.kaiwidmaier.travelplanner.adapters.RecyclerTravelAdapter;

public class MainActivity extends AppCompatActivity{

  private final static String TAG = MainActivity.class.getSimpleName();
  private ArrayList<Place> places;
  private RecyclerTravelAdapter adapter;

  public static final String ANONYMOUS = "Anonymous";
  private String photoUrl;

  private FirebaseAuth firebaseAuth;
  private FirebaseUser firebaseUser;
  private DatabaseReference firebaseDatabaseReference;
  private String username;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    username = ANONYMOUS;
    firebaseAuth = FirebaseAuth.getInstance();
    firebaseUser = firebaseAuth.getCurrentUser();
    if(firebaseUser == null){
      //Not signed in, launch Sign in Activity
      startActivity(new Intent(this, SignInActivity.class));
      finish();
      return;
    }
    else{
      username = firebaseUser.getDisplayName();
      if(firebaseUser.getPhotoUrl() != null){
        photoUrl = firebaseUser.getPhotoUrl().toString();
      }
    }

    places = new ArrayList<>();
    adapter = new RecyclerTravelAdapter(this, places);
    RecyclerView recycler = findViewById(R.id.recycler_trips);
    recycler.setLayoutManager(new LinearLayoutManager(this));
    recycler.setAdapter(adapter);

    PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
    autocompleteFragment.setHint("Where are you going?");
    autocompleteFragment.setFilter(new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS).build());
    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
      @Override
      public void onPlaceSelected(Place place) {
        // TODO: Get info about the selected place.
        Log.i(TAG, "Place: " + place.getName());
        Log.i(TAG, "Address: " + place.getAddress());
        places.add(place);
        adapter.notifyItemChanged(places.size() - 1);
      }

      @Override
      public void onError(Status status) {
        // TODO: Handle the error.
        Log.i(TAG, "An error occurred: " + status);
      }
    });
  }
}
