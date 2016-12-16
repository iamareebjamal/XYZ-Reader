package areeb.xyzreader.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import areeb.xyzreader.R;
import areeb.xyzreader.data.ArticleProvider;
import areeb.xyzreader.data.model.Article;

public class ArticleDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private long mItemId;
    private int mVibrantColor;

    private View mRootView;
    private ImageView mPhotoView;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;

    private Article article;

    public ArticleDetailFragment() { }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
    }

    public ArticleDetailActivity getActivityCast() {
        return (ArticleDetailActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

        mPhotoView = (ImageView) mRootView.findViewById(R.id.backdrop);

        mRootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        article = ArticleProvider.getArticle(mItemId);
        toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        getActivityCast().setToolbar(toolbar);

        collapsingToolbar = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_toolbar);

        bindViews();
        return mRootView;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    private String formatDate(String date) {
        Time time = new Time();
        time.parse3339(date);
        return DateUtils.getRelativeTimeSpanString(
                time.toMillis(false),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString();
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        TextView titleView = (TextView) mRootView.findViewById(R.id.article_title);
        TextView bylineView = (TextView) mRootView.findViewById(R.id.article_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);

        if (article != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            titleView.setText(article.title);
            bylineView.setText(Html.fromHtml(
                    formatDate(article.published_date)
                            + " by <font color='#ffffff'>"
                            + article.author
                            + "</font>"));
            bodyView.setText(Html.fromHtml(article.body));
            Picasso.with(getContext())
                    .load(article.photo)
                    .into(mPhotoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) mPhotoView.getDrawable()).getBitmap();
                            if (bitmap == null)
                                return;
                            Palette.from(bitmap)
                                    .maximumColorCount(24)
                                    .generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            mVibrantColor = palette.getDarkVibrantColor(0xFF555555);
                                            mRootView.findViewById(R.id.meta_bar)
                                                    .setBackgroundColor(mVibrantColor);
                                            collapsingToolbar.setStatusBarScrimColor(mVibrantColor);
                                        }
                            });

                        }


                        @Override
                        public void onError() {

                        }
                    });

        } else {
            mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            bylineView.setText("N/A" );
            bodyView.setText("N/A");
        }
    }
}
