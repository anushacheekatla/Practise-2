package com.dealbreaker.cloud.backend.dummy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dealbreaker.cloud.backend.core.CloudBackendMessaging;
import com.dealbreaker.cloud.backend.core.CloudCallbackHandler;
import com.dealbreaker.cloud.backend.core.CloudEntity;
import com.dealbreaker.cloud.backend.core.CloudQuery;
import com.dealbreaker.cloud.backend.core.CloudQuery.Order;
import com.dealbreaker.cloud.backend.core.CloudQuery.Scope;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class AdContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<AdItem> ITEMS = new ArrayList<AdItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, AdItem> ITEM_MAP = new HashMap<String, AdItem>();

	/*static {
		// Add 3 sample items.
		addItem(new AdItem("1", "Item 1"));
		addItem(new AdItem("2", "Item 2"));
		addItem(new AdItem("3", "Item 3"));
	}*/

	private static void addItem(AdItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}
	public static void getAds(CloudBackendMessaging cloudBackend){
		CloudCallbackHandler<List<CloudEntity>> handler = new CloudCallbackHandler<List<CloudEntity>>() {
            @Override
            public void onComplete(List<CloudEntity> results) {
                   for(int i = 0;i<results.size();i++)
                   {
                	   addItem(new AdItem(String.valueOf(i),results.get(i).get("AdContent").toString()));
                   }
            }

            @Override
            public void onError(IOException exception) {
                   
            }
        };
        CloudQuery cq = new CloudQuery("PostAd");
	    cq.setSort(CloudEntity.PROP_UPDATED_AT, Order.DESC);
        cq.setLimit(100);
        cq.setScope(Scope.FUTURE_AND_PAST);
        cloudBackend.list(cq,handler);
    
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class AdItem {
		public String id;
		public String content;

		public AdItem(String id, String content) {
			this.id = id;
			this.content = content;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}
