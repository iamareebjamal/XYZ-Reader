package areeb.xyzreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import areeb.xyzreader.R;
import areeb.xyzreader.data.ArticleProvider;
import areeb.xyzreader.data.UpdaterService;
import areeb.xyzreader.data.model.Article;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class ArticleListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private RealmResults<Article> articles;
    private boolean mIsRefreshing = false;
    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }

        if (savedInstanceState == null) {
            refresh();
        }

        Realm.init(this);
        articles = ArticleProvider.getArticles();
        articles.addChangeListener(new RealmChangeListener<RealmResults<Article>>() {
            @Override
            public void onChange(RealmResults<Article> element) {
                onLoadFinished();
            }
        });
        onLoadFinished();

    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
        articles.removeChangeListeners();
    }

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }


    public void onLoadFinished() {
        if (articles == null)
            return;
        Adapter adapter = new Adapter(articles);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
        mIsRefreshing = false;
        updateRefreshingUI();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private RealmResults<Article> articles;

        public Adapter(RealmResults<Article> articles) {
            this.articles = articles;
        }

        @Override
        public long getItemId(int position) {
            return articles.get(position).id;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ArticleListActivity.this, ArticleDetailActivity.class);
                    intent.putExtra(ArticleDetailFragment.ARG_ITEM_ID, articles.get(vh.getAdapterPosition()).id);
                    startActivity(intent);
                }
            });
            return vh;
        }

        private String formatDate(String date) {
            Time time = new Time();
            time.parse3339(date);
            return DateUtils.getRelativeTimeSpanString(
                    time.toMillis(false),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Article article = articles.get(position);
            holder.titleView.setText(article.title);
            holder.subtitleView.setText(
                    formatDate(article.published_date)
                            + " by "
                            + article.author);
            Picasso.with(ArticleListActivity.this)
                    .load(article.thumb)
                    .placeholder(R.drawable.photo_background_protection)
                    .into(holder.thumbnailView);
        }

        @Override
        public int getItemCount() {
            return articles.size();
        }
    }
}
