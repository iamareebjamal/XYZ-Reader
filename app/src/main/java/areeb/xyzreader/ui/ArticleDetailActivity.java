package areeb.xyzreader.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ListIterator;

import areeb.xyzreader.R;
import areeb.xyzreader.data.ArticleProvider;
import areeb.xyzreader.data.model.Article;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    ViewPager mPager;
    private long mStartId;
    private MyPagerAdapter mPagerAdapter;

    private RealmResults<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ButterKnife.bind(this);

        articles = ArticleProvider.getArticles();
        articles.addChangeListener(new RealmChangeListener<RealmResults<Article>>() {
            @Override
            public void onChange(RealmResults<Article> element) {
                onLoadFinished();
            }
        });

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(mPager, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                insets = ViewCompat.onApplyWindowInsets(v, insets);
                if (insets.isConsumed())
                    return insets;

                boolean consumed = false;
                for (int i = 0, count = mPager.getChildCount(); i < count; i++) {
                    ViewCompat.dispatchApplyWindowInsets(mPager.getChildAt(i), insets);
                    if (insets.isConsumed())
                        consumed = true;
                }
                return consumed ? insets.consumeSystemWindowInsets() : insets;
            }
        });

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().hasExtra(ArticleDetailFragment.ARG_ITEM_ID)) {
                mStartId = getIntent().getLongExtra(ArticleDetailFragment.ARG_ITEM_ID, 0);
            }
        }

        onLoadFinished();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onLoadFinished() {
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            ListIterator<Article> articleListIterator = articles.listIterator();
            while (articleListIterator.hasNext()) {
                if (articleListIterator.next().id == mStartId) {
                    final int position = articleListIterator.previousIndex();
                    mPager.setCurrentItem(position, false);
                    break;
                }
            }
            mStartId = 0;
        }
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDefaultDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            return ArticleDetailFragment.newInstance(articles.get(position).id);
        }

        @Override
        public int getCount() {
            return (articles != null) ? articles.size() : 0;
        }
    }
}
