package com.dealbreaker.cloud.backend;

import java.io.IOException;
import java.util.List;

import com.dealbreaker.cloud.backend.core.CloudBackendFragment;
import com.dealbreaker.cloud.backend.core.CloudCallbackHandler;
import com.dealbreaker.cloud.backend.core.CloudEntity;
import com.dealbreaker.cloud.backend.core.CloudQuery;
import com.dealbreaker.cloud.backend.core.CloudBackendFragment.OnListener;
import com.dealbreaker.cloud.backend.core.CloudQuery.Order;
import com.dealbreaker.cloud.backend.core.CloudQuery.Scope;
import com.dealbreaker.cloud.backend.dummy.AdContent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLngBounds;
import com.dealbreaker.cloud.backend.R;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details (if present) is a
 * {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required {@link ItemListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class ItemListActivity extends Activity implements
		ItemListFragment.Callbacks,OnMyLocationChangeListener,OnListener {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private static Location mCurrentLocation;
	private static final Geohasher gh = new Geohasher();
	private GoogleMap mMap;
	private static final String PROCESSING_FRAGMENT_TAG = "BACKEND_FRAGMENT";
	/*
	 * UI Components
	 */

	 private FragmentManager mFragmentManager;
	 private static CloudBackendFragment mProcessingFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		if (findViewById(R.id.item_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ItemListFragment) getFragmentManager().findFragmentById(
					R.id.item_list)).setActivateOnItemClick(true);
		}
		
            // Try to obtain the map from the SupportMapFragment.
           
		 //LatLngBounds visibleBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
         //String visibleRegionHash = gh.findHashForRegion(visibleBounds);
         initiateFragments();
 		
	}
	

	@Override
	protected void onStart() {
		super.onStart();
		AdContent.getAds(mProcessingFragment.getCloudBackend());
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.general_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
        if (id == R.id.post_ad) {
            Intent intent = new Intent(this,PostAdActivity.class);
            startActivity(intent);
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	private void initiateFragments() {
		 mFragmentManager = getFragmentManager();  
		 FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

	        // Check to see if we have retained the fragment which handles
	        // asynchronous backend calls
	        mProcessingFragment = (CloudBackendFragment) mFragmentManager.
	                findFragmentByTag(PROCESSING_FRAGMENT_TAG);
	        // If not retained (or first time running), create a new one
	        if (mProcessingFragment == null) {
	            mProcessingFragment = new CloudBackendFragment();
	            mProcessingFragment.setRetainInstance(true);
	            fragmentTransaction.add(mProcessingFragment, PROCESSING_FRAGMENT_TAG);
	        }
	        fragmentTransaction.commit();
	 }


	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
			ItemDetailFragment fragment = new ItemDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ItemDetailActivity.class);
			detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}


	@Override
	public void onMyLocationChange(Location location) {
		mCurrentLocation = location;
		
	}
	private void queryAds(String visibleRegionHash){
		CloudCallbackHandler<List<CloudEntity>> handler = new CloudCallbackHandler<List<CloudEntity>>() {
            @Override
            public void onComplete(List<CloudEntity> results) {
                    //Todo
            }

            @Override
            public void onError(IOException exception) {
                   //Todo
            }
        };
        mProcessingFragment.getCloudBackend().clearAllSubscription();
        CloudQuery cq = new CloudQuery("Geek");
	    cq.setSort(CloudEntity.PROP_UPDATED_AT, Order.DESC);
        cq.setLimit(100);
        cq.setScope(Scope.FUTURE_AND_PAST);
        mProcessingFragment.getCloudBackend().list(cq, handler);
	}


	@Override
	public void onCreateFinished() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onBroadcastMessageReceived(List<CloudEntity> message) {
		// TODO Auto-generated method stub
		
	}
}
