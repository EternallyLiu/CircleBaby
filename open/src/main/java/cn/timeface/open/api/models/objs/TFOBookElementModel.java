package cn.timeface.open.api.models.objs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.io.File;

import cn.timeface.open.R;
import cn.timeface.open.managers.interfaces.IMoveParams;
import cn.timeface.open.managers.interfaces.IPageScale;
import cn.timeface.open.utils.Utils;
import cn.timeface.open.utils.glide.TFOContentUrlLoader;

/**
 * @author liuxz:
 * @version OpenPlatPod 1.0 (区块信息)
 * @date 2016-4-18 下午1:48:01
 */
public class TFOBookElementModel implements Parcelable, IPageScale, IMoveParams {

    float my_view_scale = 1.f;
    boolean right = false;//是否为右页的元素
    //    1 图片 2 文字  3 音频 4 视频 5 挂件 6 webview
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_VIDEO = 4;
    public static final int TYPE_PENDANT = 5;
    public static final int TYPE_WEB = 6;

    int element_id;
    String element_index;
    float element_top;// 元素尺寸－距版心顶部边距
    float element_left;// 元素尺寸－距版心左边距
    float element_width;// 元素尺寸－宽度
    float element_height;// 元素尺寸－高度
    int element_depth;// 元素层级关系0最底层
    int element_rotation;// 元素以自身中心点旋转角度，顺时针正值，逆时针负值
    int element_type;// 元素类型 1 图片 2 文字
    String element_content;// 元素内容 文本或者图片URL

    String element_background;// 元素背景图片绝对路径或者十六进制色值
    float element_content_top;// 元素内容距元素顶部距离
    float element_content_left;// 元素内容距元素左边距离
    float element_content_right;// 元素内容距元素右边距离
    float element_content_bottom;// 元素内容距元素底部距离

    String element_mask_image;// 元素图片
    String element_front_mask_image;//最外层蒙板
    float element_exceed_alpha;// 元素超出部分透明度0透明1不透明
    TFOBookImageModel image_content_expand;// 元素图片扩展属性
    TFOBookTextContentExpandModel text_content_expand;// 元素文字扩展属性


    public TFOBookElementModel() {
    }

    public TFOBookElementModel(TFOBookImageModel imageModel) {
        this.element_type = TYPE_PENDANT;
        this.image_content_expand = imageModel;
        this.element_top = 10;
        this.element_left = 10;
        this.element_content = imageModel.getImageUrl();
        this.element_width = imageModel.getImageWidth() * imageModel.getImageScale();
        this.element_height = imageModel.getImageHeight() * imageModel.getImageScale();
    }

    public float getMyViewScale() {
        return my_view_scale;
    }

    public void setMyViewScale(float my_view_scale) {
        this.my_view_scale = my_view_scale;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public int getElementId() {
        return element_id;
    }

    public void setElementId(int element_id) {
        this.element_id = element_id;
    }

    public String getElementIndex() {
        return element_index;
    }

    public void setElementIndex(String element_index) {
        this.element_index = element_index;
    }

    public float getElementTop() {
        return element_top;
    }

    public void setElementTop(int element_top) {
        this.element_top = element_top;
    }

    public float getElementLeft() {
        return element_left;
    }

    public void setElementLeft(float element_left) {
        this.element_left = element_left;
    }

    public float getElementWidth() {
        return element_width;
    }

    public void setElementWidth(float element_width) {
        this.element_width = element_width;
    }

    public float getElementHeight() {
        return element_height;
    }

    public void setElementHeight(float element_height) {
        this.element_height = element_height;
    }

    public int getElementDepth() {
        return element_depth;
    }

    public void setElementDepth(int element_depth) {
        this.element_depth = element_depth;
    }

    public int getElementRotation() {
        return element_rotation;
    }

    public void setElementRotation(int element_rotation) {
        this.element_rotation = element_rotation;
    }

    public int getElementType() {
        return element_type;
    }

    public void setElementType(int element_type) {
        this.element_type = element_type;
    }

    public String getElementContent() {
        return element_content;
    }

    public void setElementContent(String element_content) {
        this.element_content = element_content;
    }

    public String getElementBackground() {
        return element_background;
    }

    public void setElementBackground(String element_background) {
        this.element_background = element_background;
    }

    public float getElementContentTop() {
        return element_content_top;
    }

    public void setElementContentTop(float element_content_top) {
        this.element_content_top = element_content_top;
    }

    public float getElementContentLeft() {
        return element_content_left;
    }

    public void setElementContentLeft(float element_content_left) {
        this.element_content_left = element_content_left;
    }

    public float getElementContentRight() {
        return element_content_right;
    }

    public void setElementContentRight(float element_content_right) {
        this.element_content_right = element_content_right;
    }

    public float getElementContentBottom() {
        return element_content_bottom;
    }

    public void setElementContentBottom(float element_content_bottom) {
        this.element_content_bottom = element_content_bottom;
    }

    public String getElementMaskImage() {
        return element_mask_image;
    }

    public void setElementMaskImage(String element_mask_image) {
        this.element_mask_image = element_mask_image;
    }

    public float getElementExceedAlpha() {
        return element_exceed_alpha;
    }

    public void setElementExceedAlpha(float element_exceed_alpha) {
        this.element_exceed_alpha = element_exceed_alpha;
    }

    public TFOBookImageModel getImageContentExpand() {
        return image_content_expand;
    }

    public void setImageContentExpand(TFOBookImageModel image_content_expand) {
        this.image_content_expand = image_content_expand;
    }

    public TFOBookTextContentExpandModel getTextContentExpand() {
        return text_content_expand;
    }

    public void setTextContentExpand(TFOBookTextContentExpandModel text_content_expand) {
        this.text_content_expand = text_content_expand;
    }

    public FrameLayout getView(Context context) {
        FrameLayout elementFrameLayout = new FrameLayout(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) this.element_width, (int) this.element_height);
        lp.topMargin = (int) element_top;
        lp.leftMargin = (int) element_left;
        elementFrameLayout.setLayoutParams(lp);

        if (!TextUtils.isEmpty(this.element_background)) {
            if (element_background.contains("http")) {
                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new FrameLayout.LayoutParams((int) this.element_width, (int) this.element_height));
                Glide.with(context).load(element_background).fitCenter().into(imageView);
                elementFrameLayout.addView(imageView);
            } else if (element_background.charAt(0) == '#' && (element_background.length() == 7 || element_background.length() == 9)) {
                elementFrameLayout.setBackgroundColor(Color.parseColor(this.element_background));
            }
        }

        //增加主元素view
        if (element_type == TYPE_IMAGE) {
            elementFrameLayout.addView(getImageView(context));
        } else if (element_type == TYPE_TEXT) {
            elementFrameLayout.addView(getTextView(context));
        } else if (element_type == TYPE_WEB) {
            elementFrameLayout.addView(getWebView(context));
        } else if (element_type == TYPE_PENDANT) {
            elementFrameLayout.addView(getImageView(context));
        }

//        if (!TextUtils.isEmpty(this.element_mask_image)) {
//            //最后增加蒙版
//            ImageView mask = new ImageView(context);
//            mask.setLayoutParams(new FrameLayout.LayoutParams((int) this.element_width, (int) this.element_height));
//            Glide.with(context)
//                    .load(this.element_mask_image)
//                    .centerCrop()
//                    .into(mask);
//            elementFrameLayout.addView(mask);
//        }

        if (!TextUtils.isEmpty(this.element_front_mask_image)) {
            //增加 最外层蒙版
            ImageView frontMask = new ImageView(context);
            frontMask.setLayoutParams(new FrameLayout.LayoutParams((int) this.element_width, (int) this.element_height));
            Glide.with(context)
                    .load(this.element_front_mask_image)
                    .centerCrop()
                    .into(frontMask);
            elementFrameLayout.addView(frontMask);
        }

        if (element_rotation != 0) {
            //旋转
            elementFrameLayout.setRotation(element_rotation);
        }

        return elementFrameLayout;
    }

    private ImageView getImageView(final Context context) {
        ImageView imageView = new ImageView(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(lp);
        imageView.setPadding((int) this.element_content_left, (int) this.element_content_top, (int) this.element_content_right, (int) this.element_content_bottom);

        if (!TextUtils.isEmpty(this.element_mask_image)) {
            Glide.with(context)
                    .using(new TFOContentUrlLoader(context))
                    .load(this)
                    .centerCrop()
                    .bitmapTransform(new Transformation<Bitmap>() {
                        @Override
                        public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
                            return BitmapResource.obtain(Utils.fixBitmap(resource.get(), getMaskFile(context)), Glide.get(context).getBitmapPool());
                        }

                        @Override
                        public String getId() {
                            return "mask image";
                        }
                    })
                    .into(imageView);


        } else {
            Glide.with(context)
                    .using(new TFOContentUrlLoader(context))
                    .load(this)
                    .error(R.drawable.tfo_empty_img)
                    .centerCrop()
                    .into(imageView);
        }

        return imageView;
    }

    private View getTextView(Context context) {
        //如果文字有图片,则加载图片
        if (this.image_content_expand != null && !TextUtils.isEmpty(this.image_content_expand.getImageUrl())) {
            ImageView imageView = new ImageView(context);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);
//            imageView.setPadding((int) (this.element_content_left + this.image_content_expand.getImagePaddingLeft()), (int) (this.element_content_top + this.image_content_expand.getImagePaddingTop()), (int) this.element_content_right, (int) this.element_content_bottom);
            imageView.setPadding((int) (this.element_content_left), (int) (this.element_content_top), (int) this.element_content_right, (int) this.element_content_bottom);
            Glide.with(context)
                    .using(new TFOContentUrlLoader(context))
                    .load(this)
                    .fitCenter()
                    .into(imageView);
            return imageView;
        }

        TextView textView = new TextView(context);
        textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        textView.setPadding((int) this.element_content_left, (int) this.element_content_top, (int) this.element_content_right, (int) this.element_content_bottom);
        textView.setText(this.element_content.replaceAll("<br/>", "\n"));

        if (text_content_expand != null) {
            text_content_expand.setTextExpandInfo(textView);
        }

        return textView;
    }

    private WebView getWebView(Context context) {
        WebView webView = new WebView(context);
        webView.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
        webView.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
        webView.setPadding((int) this.element_content_left, (int) this.element_content_top, (int) this.element_content_right, (int) this.element_content_bottom);
        if (element_content.startsWith("http")) {
            webView.loadUrl(element_content);
        } else {
            webView.loadData(element_content, "text/html; charset=UTF-8", null);
        }
        return webView;
    }

    @Override
    public void setPageScale(float scale) {
        if (scale != my_view_scale) {
            this.my_view_scale = scale;

            this.element_top *= scale;
            this.element_left *= scale;
            this.element_width *= scale;
            this.element_height *= scale;
            this.element_content_top *= scale;
            this.element_content_left *= scale;
            this.element_content_right *= scale;
            this.element_content_bottom *= scale;
        }

        if (this.image_content_expand != null) {
            this.image_content_expand.setPageScale(scale);
        }
        if (this.text_content_expand != null) {
            this.text_content_expand.setPageScale(scale);
        }
    }

    @Override
    public void moveParams(float movedX, float movedY, int eleW, int eleH, float scale, float rotation) {
        float contentW = element_width - element_content_left - element_content_right;
        float contentH = element_height - element_content_top - element_content_bottom;

        setElementLeft((int) (element_left + movedX + (element_width - eleW) / 2));
        setElementTop((int) (element_top + movedY + (element_height - eleH) / 2));

        if (element_content_left == element_content_right) {
            float value = (eleW - (contentW * scale)) / 2;
            setElementContentLeft(value);
            setElementContentRight(value);
        } else {
            setElementContentLeft(element_content_left * scale);
            setElementContentRight(eleW - (contentW * scale) - element_content_left);
        }

        if (element_content_top == element_content_bottom) {
            float value = (eleH - (contentH * scale)) / 2;
            setElementContentTop(value);
            setElementContentBottom(value);
        } else {
            setElementContentTop(element_content_top * scale);
            setElementContentBottom(eleH - (contentH * scale) - element_content_top);
        }

        if (this.image_content_expand != null) {
            this.image_content_expand.moveParams(movedX, movedY, eleW, eleH, scale, rotation);
        }
        if (this.text_content_expand != null) {
            this.text_content_expand.moveParams(movedX, movedY, eleW, eleH, scale, rotation);
        }

        setElementWidth(eleW);
        setElementHeight(eleH);

        setElementRotation((int) rotation);
    }

    @Override
    public void resetPageScale() {
        this.element_top /= my_view_scale;
        this.element_left /= my_view_scale;
        this.element_width /= my_view_scale;
        this.element_height /= my_view_scale;
        this.element_content_top /= my_view_scale;
        this.element_content_left /= my_view_scale;
        this.element_content_right /= my_view_scale;
        this.element_content_bottom /= my_view_scale;

        if (this.image_content_expand != null) {
            this.image_content_expand.resetPageScale();
        }
        if (this.text_content_expand != null) {
            this.text_content_expand.resetPageScale();
        }

        my_view_scale = 1.f;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.my_view_scale);
        dest.writeByte(this.right ? (byte) 1 : (byte) 0);
        dest.writeInt(this.element_id);
        dest.writeString(this.element_index);
        dest.writeFloat(this.element_top);
        dest.writeFloat(this.element_left);
        dest.writeFloat(this.element_width);
        dest.writeFloat(this.element_height);
        dest.writeInt(this.element_depth);
        dest.writeInt(this.element_rotation);
        dest.writeInt(this.element_type);
        dest.writeString(this.element_content);
        dest.writeString(this.element_background);
        dest.writeFloat(this.element_content_top);
        dest.writeFloat(this.element_content_left);
        dest.writeFloat(this.element_content_right);
        dest.writeFloat(this.element_content_bottom);
        dest.writeString(this.element_mask_image);
        dest.writeString(this.element_front_mask_image);
        dest.writeFloat(this.element_exceed_alpha);
        dest.writeParcelable(this.image_content_expand, flags);
        dest.writeParcelable(this.text_content_expand, flags);
    }

    protected TFOBookElementModel(Parcel in) {
        this.my_view_scale = in.readFloat();
        this.right = in.readByte() != 0;
        this.element_id = in.readInt();
        this.element_index = in.readString();
        this.element_top = in.readFloat();
        this.element_left = in.readFloat();
        this.element_width = in.readFloat();
        this.element_height = in.readFloat();
        this.element_depth = in.readInt();
        this.element_rotation = in.readInt();
        this.element_type = in.readInt();
        this.element_content = in.readString();
        this.element_background = in.readString();
        this.element_content_top = in.readFloat();
        this.element_content_left = in.readFloat();
        this.element_content_right = in.readFloat();
        this.element_content_bottom = in.readFloat();
        this.element_mask_image = in.readString();
        this.element_front_mask_image = in.readString();
        this.element_exceed_alpha = in.readFloat();
        this.image_content_expand = in.readParcelable(TFOBookImageModel.class.getClassLoader());
        this.text_content_expand = in.readParcelable(TFOBookTextContentExpandModel.class.getClassLoader());
    }

    public static final Creator<TFOBookElementModel> CREATOR = new Creator<TFOBookElementModel>() {
        @Override
        public TFOBookElementModel createFromParcel(Parcel source) {
            return new TFOBookElementModel(source);
        }

        @Override
        public TFOBookElementModel[] newArray(int size) {
            return new TFOBookElementModel[size];
        }
    };

    public float getContentWidth() {
        return element_width - element_content_left - element_content_right;
    }

    public float getContentHeight() {
        return element_height - element_content_top - element_content_bottom;
    }

    public String getCropImageUrl(int width) {
        String imgUrl = this.image_content_expand.getImageUrl();
        Rect rect = this.image_content_expand.getOrgCropRect(getContentWidth(), getContentHeight());

        int rotation = this.image_content_expand.getImageRotation();
        rotation = (rotation + 360) % 360;
        int w = rect.width();
        int h = rect.height();
        if (rotation == 90 || rotation == 270) {
            int temp = w;
            w = h;
            h = temp;
        }
        imgUrl += "@" + rect.left + "-" + rect.top + "-" + w + "-" + h + "a" + "_" + rotation + "r" + "_" + width + "w_1l_1o" + ".webp";
        Log.i("open glide image url", "getCropImageUrl: " + imgUrl);
        return imgUrl;
    }

    public String getCropImageUrl() {
        return getCropImageUrl((int) getContentWidth());
    }

    public File getMaskFile(Context context) {
        if (TextUtils.isEmpty(element_mask_image)) {
            throw new NullPointerException("不存在遮罩图片");
        }
        File path = Glide.getPhotoCacheDir(context);
        return new File(path, element_mask_image.hashCode() + ".png");
    }
}
