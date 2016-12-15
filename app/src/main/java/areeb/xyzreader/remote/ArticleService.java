package areeb.xyzreader.remote;

import java.util.List;

import areeb.xyzreader.data.model.Article;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticleService {
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://dl.dropboxusercontent.com/u/231329/xyzreader_data/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static ArticleAPI articleAPI = retrofit.create(ArticleAPI.class);

    public static Call<List<Article>> getArticlesCall(){
        return articleAPI.getArticles();
    }
}
