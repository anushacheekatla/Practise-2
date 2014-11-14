package com.dealbreaker.cloud.backend;

import java.io.IOException;
import java.util.List;

import com.dealbreaker.cloud.backend.core.CloudBackendFragment;
import com.dealbreaker.cloud.backend.core.CloudCallbackHandler;
import com.dealbreaker.cloud.backend.core.CloudEntity;
import com.dealbreaker.cloud.backend.core.CloudQuery;
import com.dealbreaker.cloud.backend.core.Filter;
import com.dealbreaker.cloud.backend.core.CloudBackendFragment.OnListener;
import com.dealbreaker.cloud.backend.core.CloudQuery.Scope;
import com.dealbreaker.cloud.backend.core.Filter.Op;
import com.dealbreaker.cloud.backend.R;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.os.Build;

public class PostAdActivity extends Activity implements OnListener {
private static final String PROCESSING_FRAGMENT_TAG = "BACKEND_FRAGMENT";
	
	
    /*
	 * UI Components
	 */

	 private FragmentManager mFragmentManager;
	 private static CloudBackendFragment mProcessingFragment;
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_ad);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		initiateFragments();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post_ad, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
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
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}
		
		

		@Override
		public void onStart() {
			
			super.onStart();
			final EditText deal = (EditText) getActivity().findViewById(R.id.add_content);
			Button button = (Button) getActivity().findViewById(R.id.post_ad);
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mProcessingFragment.getCloudBackend()!=null)
			        {
			    		final String userName = mProcessingFragment.getCloudBackend().getCredential().getSelectedAccount().name.toString();
			    		mProcessingFragment.getCloudBackend().listByProperty("PostAd", "userName", Op.EQ, userName, null, 1, Scope.PAST, new CloudCallbackHandler<List<CloudEntity>>() {
							
							@Override
							public void onComplete(List<CloudEntity> results) {
								
								if(results.size() > 0)
								{

									CloudEntity editAd = results.get(0);
									editAd.put("AdContent", deal.getText());
									//editAd.put("location",gh.encode(mCurrentLocation));
									 CloudCallbackHandler<CloudEntity> handler = new CloudCallbackHandler<CloudEntity>() {
							                @Override
							                public void onComplete(final CloudEntity result) {
							                    
							                }

							                @Override
							                public void onError(final IOException exception) {
							                    
							                }
							            };
							            mProcessingFragment.getCloudBackend().update(editAd, handler);
								}
								else
								{
									CloudEntity insertAd = new CloudEntity("PostAd");
						    		insertAd.put("userName",userName);
						    		insertAd.put("AdContent", deal.getText());
						        	//insertAd.put("location", gh.encode(mCurrentLocation));
						        	// create a response handler that will receive the result or an error
						            CloudCallbackHandler<CloudEntity> handler = new CloudCallbackHandler<CloudEntity>() {
						                @Override
						                public void onComplete(final CloudEntity result) {
						                    //Toast.makeText(getApplicationContext(), "Name Changed to " + result.get("restaurantName").toString(), Toast.LENGTH_SHORT).show();
						                    
						                }

						                @Override
						                public void onError(final IOException exception) {
						                    
						                }
						            };
						        	// execute the insertion with the handler
						            
						            	mProcessingFragment.getCloudBackend().update(insertAd, handler);
								}
								
							}
						});
			    		
			        }
					
				}
			});
		}



		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_post_ad,
					container, false);
			return rootView;
		}
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
