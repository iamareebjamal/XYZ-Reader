package areeb.xyzreader.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    @BindView(R.id.backdrop)
    ImageView mPhotoView;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.article_title)
    TextView titleView;
    @BindView(R.id.date)
    TextView dateView;
    @BindView(R.id.author)
    TextView authorView;
    @BindView(R.id.article_body)
    TextView bodyView;
    @BindView(R.id.author_photo)
    ImageView authorImage;
    @BindView(R.id.date_photo)
    ImageView dateImage;
    @BindView(R.id.title_photo)
    ImageView titleImage;
    private long mItemId;
    private int mVibrantColor = 0xFF333333;
    private View mRootView;
    private Article article;

    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    private static void setTint(ImageView imageView, int tintColor) {
        Drawable wrapped = DrawableCompat.wrap(imageView.getDrawable());
        DrawableCompat.setTint(wrapped, tintColor);
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

        ButterKnife.bind(this, mRootView);

        article = ArticleProvider.getArticle(mItemId);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(article.title + "\n\n" + article.body)
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        getActivityCast().setToolbar(toolbar);

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

    private void setColors() {
        Bitmap bitmap = ((BitmapDrawable) mPhotoView.getDrawable()).getBitmap();
        if (bitmap == null)
            return;
        Palette.from(bitmap)
                .maximumColorCount(24)
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                        if (swatch != null) {
                            mVibrantColor = swatch.getRgb();
                            titleView.setTextColor(swatch.getTitleTextColor());
                            authorView.setTextColor(swatch.getBodyTextColor());
                            dateView.setTextColor(swatch.getBodyTextColor());

                            setTint(titleImage, swatch.getBodyTextColor());
                            setTint(authorImage, swatch.getBodyTextColor());
                            setTint(dateImage, swatch.getBodyTextColor());
                        }

                        mRootView.findViewById(R.id.meta_bar)
                                .setBackgroundColor(mVibrantColor);
                        collapsingToolbar.setStatusBarScrimColor(mVibrantColor);
                    }
                });
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        if (article != null) {
            bodyView.setMovementMethod(LinkMovementMethod.getInstance());

            titleView.setText(article.title);
            authorView.setText(article.author);
            dateView.setText(formatDate(article.published_date));
            bodyView.setText(Html.fromHtml(article.body));
            Picasso.with(getContext())
                    .load(article.photo)
                    .into(mPhotoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            setColors();

                        }

                        @Override
                        public void onError() {

                        }
                    });

        } else {
            mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            authorView.setText("N/A");
            bodyView.setText("N/A");
        }
    }
}
