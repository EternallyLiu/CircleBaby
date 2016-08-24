package cn.timeface.open.api.models.response;

/**
 * author: shiyan  Created on 8/24/16.
 * email : sy0725work@gmail.com
 */
public class CoverColor {
    String cover_text_color;
    String cover_background_color;

    public String getCoverTextColor() {
        return cover_text_color;
    }

    public void setCoverTextColor(String cover_text_color) {
        this.cover_text_color = cover_text_color;
    }

    public String getCoverBackgroundColor() {
        return cover_background_color;
    }

    public void setCoverBackgroundColor(String cover_background_color) {
        this.cover_background_color = cover_background_color;
    }
}
