package areeb.xyzreader.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Article extends RealmObject {
    @PrimaryKey
    public long id;
    public String title;
    public String author;
    public String body;
    public String thumb;
    public String photo;
    public float aspect_ratio;
    public String published_date;
}
