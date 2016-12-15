package areeb.xyzreader.remote;

import java.util.List;

import areeb.xyzreader.data.model.Article;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ArticleAPI {
    @GET("data.json")
    Call<List<Article>> getArticles();
}
