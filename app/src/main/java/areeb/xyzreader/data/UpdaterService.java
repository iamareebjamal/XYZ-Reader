package areeb.xyzreader.data;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import areeb.xyzreader.data.model.Article;
import areeb.xyzreader.remote.ArticleService;
import io.realm.Realm;

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.example.xyzreader.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.example.xyzreader.intent.extra.REFRESHING";

    public UpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }

        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        // Don't even inspect the intent, we only do one thing, and that's fetch content.

        try {
            Realm.init(this);
            Realm realm = Realm.getDefaultInstance();
            List<Article> articles = ArticleService.getArticlesCall().execute().body();

            for(final Article article : articles) {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(article);
                realm.commitTransaction();
                Log.d("Realm", "Saved " + article.title);
            }
        } catch (IOException ioe) {
            Log.e(TAG, "Error updating content.", ioe);
        }

        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }
}
