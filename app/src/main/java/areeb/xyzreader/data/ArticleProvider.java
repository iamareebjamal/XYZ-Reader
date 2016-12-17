package areeb.xyzreader.data;

import areeb.xyzreader.data.model.Article;
import io.realm.Realm;
import io.realm.RealmResults;

public class ArticleProvider {
    private static RealmResults<Article> articles;

    private static void loadResults() {
        articles = Realm.getDefaultInstance()
                .where(Article.class)
                .findAll();
    }

    public static RealmResults<Article> getArticles() {
        if (articles == null) {
            loadResults();
        }

        return articles;
    }

    public static Article getArticle(long id) {
        if (articles == null)
            loadResults();

        for (Article article : articles) {
            if (article.id == id)
                return article;
        }

        return null;
    }
}
