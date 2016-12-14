package areeb.xyzreader.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Article extends RealmObject {
    @PrimaryKey
    public String id;
    public String title;
    public String author;
    public String body;
    public String thumb;
    public String photo;
    public String aspect_ratio;
    public String published_date;
}
