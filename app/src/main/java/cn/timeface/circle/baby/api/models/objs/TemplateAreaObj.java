package cn.timeface.circle.baby.api.models.objs;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/27.
 */
public class TemplateAreaObj extends BaseObj{
    String bgImage;
    String family;
    String text;
    String textColor;

    int height;
    int imgMargin;
    int left;
    int limitNum;
    int lineNum;
    int textAlign;
    int textFont;
    int textType;
    int top;
    int type;
    int width;

    TemplateImage templateImage;

    public TemplateAreaObj(String bgImage, String family, String text, String textColor, int height, int imgMargin, int left, int limitNum, int lineNum, int textAlign, int textFont, int textType, int top, int type, int width, TemplateImage templateImage) {
        this.bgImage = bgImage;
        this.family = family;
        this.text = text;
        this.textColor = textColor;
        this.height = height;
        this.imgMargin = imgMargin;
        this.left = left;
        this.limitNum = limitNum;
        this.lineNum = lineNum;
        this.textAlign = textAlign;
        this.textFont = textFont;
        this.textType = textType;
        this.top = top;
        this.type = type;
        this.width = width;
        this.templateImage = templateImage;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getImgMargin() {
        return imgMargin;
    }

    public void setImgMargin(int imgMargin) {
        this.imgMargin = imgMargin;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(int textAlign) {
        this.textAlign = textAlign;
    }

    public int getTextFont() {
        return textFont;
    }

    public void setTextFont(int textFont) {
        this.textFont = textFont;
    }

    public int getTextType() {
        return textType;
    }

    public void setTextType(int textType) {
        this.textType = textType;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public TemplateImage getTemplateImage() {
        return templateImage;
    }

    public void setTemplateImage(TemplateImage templateImage) {
        this.templateImage = templateImage;
    }
}
