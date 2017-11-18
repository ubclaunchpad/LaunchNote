package com.example.ubclaunchpad.launchnote.recyclerView;


public class DisplayImage {

    private String image_text;
    private String image_url;

    public DisplayImage(String text, String url) {
        image_text = text;
        image_url = url;
    }

    public String getImageText() {
        return image_text;
    }

    public void setImageText(String image_text) {
        this.image_text = image_text;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }
}
