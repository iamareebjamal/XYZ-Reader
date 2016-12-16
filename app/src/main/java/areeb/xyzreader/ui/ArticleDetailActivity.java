package areeb.xyzreader.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import java.util.ListIterator;

import areeb.xyzreader.R;
import areeb.xyzreader.data.ArticleProvider;
import areeb.xyzreader.data.model.Article;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private long mStartId;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private RealmResults<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);

        articles = ArticleProvider.getArticles();
        articles.addChangeListener(new RealmChangeListener<RealmResults<Article>>() {
            @Override
            public void onChange(RealmResults<Article> element) {
                onLoadFinished();
            }
        });

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().hasExtra(ArticleDetailFragment.ARG_ITEM_ID)) {
                mStartId = getIntent().getLongExtra(ArticleDetailFragment.ARG_ITEM_ID, 0);
            }
        }

        onLoadFinished();

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
        if(ab != null){
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
