package org.fossasia.susi.ai.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.fossasia.susi.ai.R;
import org.fossasia.susi.ai.adapters.recycleradapters.ChatFeedRecyclerAdapter;
import org.fossasia.susi.ai.helper.AndroidHelper;
import org.fossasia.susi.ai.helper.MapHelper;
import org.fossasia.susi.ai.data.model.ChatMessage;
import org.fossasia.susi.ai.data.model.MapData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <h1>Map view holder</h1>
 *
 * Created by saurabh on 7/10/16.
 */
public class MapViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.map_image)
    public ImageView mapImage;
    @BindView(R.id.location_pointer)
    public ImageView pointer;
    @BindView(R.id.background_layout)
    public LinearLayout backgroundLayout;

    private String TAG = ChatFeedRecyclerAdapter.class.getSimpleName();

    /**
     * Instantiates a new Map view holder.
     *
     * @param itemView the item view
     */
    public MapViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Inflate MapView
     *
     * @param model the ChatMessage object
     * @param currContext the Context
     */
    public void setView(final ChatMessage model, final Context currContext, final ChatFeedRecyclerAdapter recyclerAdapter, final int position) {
        if (model != null) {
            try {
                final MapHelper mapHelper = new MapHelper(new MapData(model.getLatitude(), model.getLongitude(), model.getZoom()));
                pointer.setVisibility(View.GONE);
                Log.v(TAG, mapHelper.getMapURL());

                Picasso.with(currContext).load(mapHelper.getMapURL())
                        .into(mapImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                pointer.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                Log.d("Error", "map image can't loaded");
                            }
                        });

                mapImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                          Open in Google Maps if installed, otherwise open browser.
                        */
                            Intent mapIntent;
                            if (AndroidHelper.INSTANCE.isGoogleMapsInstalled(currContext) && mapHelper.isParseSuccessful()) {
                                Uri gmmIntentUri = Uri.parse(String.format("geo:%s,%s?z=%s", model.getLatitude(), model.getLongitude(), model.getZoom()));
                                mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage(AndroidHelper.INSTANCE.getGOOGLE_MAPS_PKG());
                            } else {
                                mapIntent = new Intent(Intent.ACTION_VIEW);
                                mapIntent.setData(Uri.parse(mapHelper.getWebLink()));
                            }
                            currContext.startActivity(mapIntent);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
