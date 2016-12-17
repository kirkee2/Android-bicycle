package com.example.lkj.bicycleproject.RecyclerView;

/**
 * Created by leegunjoon on 2016. 12. 17..
 */
public class RoadRow {

    private String id;
    private String title;
    private String image;
    private int watch;

    public int getWatch(){
        return watch;
    }

    public void setWatch(int watch){
        this.watch =watch;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id =id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}