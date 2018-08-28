package de.kaiwidmaier.travelplanner.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import de.kaiwidmaier.travelplanner.R;

public class RecyclerTravelAdapter extends RecyclerView.Adapter<RecyclerTravelAdapter.ViewHolder>{

  private static final String TAG = RecyclerTravelAdapter.class.getSimpleName();
  private ArrayList<Place> places;
  private LayoutInflater inflater;
  private Context context;

  public RecyclerTravelAdapter(Context context, ArrayList<Place> places) {
    this.inflater = LayoutInflater.from(context);
    this.places = places;
    this.context = context;
  }


  @NonNull
  @Override
  public RecyclerTravelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = inflater.inflate(R.layout.item_travel_card, parent, false);
    return new RecyclerTravelAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final RecyclerTravelAdapter.ViewHolder holder, int position) {
    getPhotos(places.get(position).getId(), holder);
    holder.textName.setText(places.get(position).getName());
  }

  private void getPhotos(String placeId, @NonNull final RecyclerTravelAdapter.ViewHolder holder) {
    final GeoDataClient geoDataClient;
    geoDataClient = Places.getGeoDataClient(context);
    final Task<PlacePhotoMetadataResponse> photoMetadataResponse = geoDataClient.getPlacePhotos(placeId);
    photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
      @Override
      public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
        // Get the list of photos.
        PlacePhotoMetadataResponse photos = task.getResult();
        // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
        // Get the first photo in the list.
        final PlacePhotoMetadata photoMetadata;
        try {
          photoMetadata = photoMetadataBuffer.get(0);
        }
        catch(IllegalStateException e){
          //No photo available
          //TODO: Placeholder image
          Log.e(TAG, "No photo found for place");
          return;
        }
        // Get the attribution text.
        CharSequence attribution = photoMetadata.getAttributions();
        // Get a full-size bitmap for the photo.
        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
          @Override
          public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
            PlacePhotoResponse photo = task.getResult();
            Bitmap bitmap = photo.getBitmap();
            holder.imgPlacePhoto.setImageBitmap(bitmap);
            if(photoMetadata.getAttributions() != null){
              holder.textAttribution.setText(String.format(context.getString(R.string.photo), Html.fromHtml(photoMetadata.getAttributions().toString())));
            }
            else{
              holder.textAttribution.setText("");
            }
          }
        });
      }
    });
  }

  @Override
  public int getItemCount() {
    return places.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imgPlacePhoto;
    TextView textName;
    TextView textAttribution;

    private ViewHolder(View itemView) {
      super(itemView);
      imgPlacePhoto = itemView.findViewById(R.id.imageView_place_photo);
      imgPlacePhoto.setColorFilter(context.getResources().getColor(R.color.black_transparent_filter));
      textName = itemView.findViewById(R.id.text_place_name);
      textAttribution = itemView.findViewById(R.id.text_attribution);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
  }

  private Place getItem(int id) {
    return places.get(id);
  }



}

