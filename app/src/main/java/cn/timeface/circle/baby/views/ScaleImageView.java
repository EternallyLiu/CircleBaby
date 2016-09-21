package cn.timeface.circle.baby.views;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.utils.ImageFactory;

/**
 * Created by lidonglin on 2016/5/23.
 */
public class ScaleImageView extends ImageView {

    float x_down = 0;
    float y_down = 0;
    PointF start = new PointF();
    PointF mid = new PointF();
    PointF center = new PointF();
    float oldDist = 1f;
    float oldRotation = 0;
    Matrix matrix = new Matrix();
    Matrix matrix1 = new Matrix();
    Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    int mode = NONE;

    boolean matrixCheck = false;

    int widthScreen;
    int heightScreen;

    Bitmap gintama;
    private float degree;
    private float bitmapWidth;
    private float bitmapHeight;
    private float picWidth;
    private float picHeight;
    private ImgObj imgObj;
    private boolean first;

    public ScaleImageView(Activity activity, ImgObj imgObj) {
        super(activity);
        this.imgObj = imgObj;
        gintama = BitmapFactory.decodeFile(imgObj.getLocalPath());
//        gintama = comp(imgObj.getLocalPath());


        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthScreen = dm.widthPixels;
        heightScreen = dm.heightPixels;

        matrix = new Matrix();

        setScaleType(ImageView.ScaleType.CENTER_CROP);
        setImageBitmap(gintama);
//        centerPoint(center);
        first = true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        reset();
        if(first){
            centerPic();
        }

    }

    protected void onDraw(Canvas canvas) {
        canvas.save();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        canvas.drawBitmap(gintama, matrix, paint);
        canvas.restore();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                oldRotation = getRotate(event);

                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
//                oldRotation = rotation(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);
                centerPoint(center);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    matrix1.set(savedMatrix);
//                    float rotation = rotation(event) - oldRotation;
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;
                    matrix1.postScale(scale, scale, center.x, center.y);// 縮放
//                    matrix1.postRotate(rotation, center.x, center.y);// 旋轉
//                    matrixCheck = matrixCheck();
//                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
//                    }
                } else if (mode == DRAG) {
                    matrix1.set(savedMatrix);
                    float rotation = getRotate(event) - oldRotation;
//                    matrix1.postRotate(rotation, center.x, center.y);// 旋轉
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
//                    matrixCheck = matrixCheck();
//                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
//                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                reset();
                break;
        }
        return true;
    }

    private boolean matrixCheck() {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * gintama.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * gintama.getHeight() + f[5];
        float x4 = f[0] * gintama.getWidth() + f[1] * gintama.getHeight() + f[2];
        float y4 = f[3] * gintama.getWidth() + f[4] * gintama.getHeight() + f[5];

        // 图片现宽度
//        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
//        // 缩放比率判断
//        if (width < widthScreen / 3 || width > widthScreen * 3) {
//            return true;
//        }
        // 出界判断
//        if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
//                && x3 < widthScreen / 3 && x4 < widthScreen / 3)
//                || (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
//                && x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
//                || (y1 < heightScreen / 3 && y2 < heightScreen / 3
//                && y3 < heightScreen / 3 && y4 < heightScreen / 3)
//                || (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
//                && y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
//            return true;
//        }
        int width = getWidth();
        int height = getHeight();
//        if (x1 > 0 || y1 > 0 || x2 < width || y2 > 0 || x3 > 0 || y3 < height || x4 < width || y4 < height) {
//            return true;
//        }


        return false;
    }

    //保持图片填充
    public void reset() {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * gintama.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * gintama.getHeight() + f[5];
        float x4 = f[0] * gintama.getWidth() + f[1] * gintama.getHeight() + f[2];
        float y4 = f[3] * gintama.getWidth() + f[4] * gintama.getHeight() + f[5];

        //边框宽高
        int width = getWidth();
        int height = getHeight();
        //图片宽高
        picWidth = x2 - x1;
        picHeight = y3 - y1;
        float w = width / picWidth;
        float h = height / picHeight;
        if (!(w < 1 && h < 1)) {
            float scale = w > h ? w : h;
            matrix1.postScale(scale, scale, center.x, center.y);// 縮放
        }
        if (x1 > 0) {
            matrix1.postTranslate(-x1, 0);
        }
        if (y1 > 0) {
            matrix1.postTranslate(0, -y1);
        }
        if (x2 < width) {
            matrix1.postTranslate(width - x2, 0);
        }
        if (y3 < height) {
            matrix1.postTranslate(0, height - y3);
        }
        matrix.set(matrix1);
        invalidate();
    }

    //刚进入时图片不要放大太多
    public void centerPic() {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * gintama.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * gintama.getHeight() + f[5];
        float x4 = f[0] * gintama.getWidth() + f[1] * gintama.getHeight() + f[2];
        float y4 = f[3] * gintama.getWidth() + f[4] * gintama.getHeight() + f[5];

        //边框宽高
        int width = getWidth();
        int height = getHeight();
        //图片宽高
        picWidth = x2 - x1;
        picHeight = y3 - y1;
        float w = width / picWidth;
        float h = height / picHeight;
        if (w < 0.8 && h < 0.8) {
            float scale = w > h ? w : h;
            matrix1.postScale(scale, scale, center.x, center.y);// 縮放
        }
        if (!(w < 1 && h < 1)) {
            float scale = w > h ? w : h;
            matrix1.postScale(scale, scale, center.x, center.y);// 縮放
        }
        if (x1 > 0) {
            matrix1.postTranslate(-x1, 0);
        }
        if (y1 > 0) {
            matrix1.postTranslate(0, -y1);
        }
        if (x2 < width) {
            matrix1.postTranslate(width - x2, 0);
        }
        if (y3 < height) {
            matrix1.postTranslate(0, height - y3);
        }
        matrix.set(matrix1);
        invalidate();
        first = false;
    }


    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        degree = (float) Math.toDegrees(radians);
        return degree;
    }

    // 取旋转角度
    private float getRotate(MotionEvent event) {
        double delta_x = (event.getX() - center.x);
        double delta_y = (event.getY() - center.y);
        double radians = Math.atan2(delta_y, delta_x);
        degree = (float) Math.toDegrees(radians);
        return degree;
    }

    // 将移动，缩放以及旋转后的图层保存为新图片
    // 本例中沒有用到該方法，需要保存圖片的可以參考
    public Bitmap CreatNewPhoto() {
        Bitmap bitmap = Bitmap.createBitmap(widthScreen, heightScreen,
                Config.ARGB_8888); // 背景图片
        Canvas canvas = new Canvas(bitmap); // 新建画布
        canvas.drawBitmap(gintama, matrix, null); // 画图片
        canvas.save(Canvas.ALL_SAVE_FLAG); // 保存画布
        canvas.restore();
        return bitmap;
    }

    public void centerPoint(PointF point) {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * gintama.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * gintama.getHeight() + f[5];
        float x4 = f[0] * gintama.getWidth() + f[1] * gintama.getHeight() + f[2];
        float y4 = f[3] * gintama.getWidth() + f[4] * gintama.getHeight() + f[5];


        float x = x1 + x2 + x3 + x4;
        float y = y1 + y2 + y3 + y4;
        point.set(x / 4, y / 4);
    }

    public float getDegree() {
        return degree;
    }


    private Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 10, baos);//这里压缩10%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 400f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;//压缩好比例大小后再进行质量压缩
    }

    private Bitmap comp(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int w = options.outWidth;
        int h = options.outHeight;
        float hh = 1080f;
        float ww = 720f;
        int scale = 1;
        int scaleX = (int) (w / ww);
        int scaleY = (int) (h / hh);
        scale = scaleX > scaleY ? scaleX : scaleY;
        if(scale<1){
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        if (scale!=1){
            String url = ImageFactory.saveImage(bitmap);
            imgObj.setLocalPath(url);
            System.out.println("ScaleImageView.url ===== " + url);
        }
        return bitmap;
    }

    //图片左上角在原图中的位置
    public PointF getLeftTop() {
//        getXY();
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * gintama.getHeight() + f[2];
        float y3 = f[3] * 0 + f[4] * gintama.getHeight() + f[5];
        float width = x2 - x1;
        float height = y3 - y1;
        float w = Math.abs(x1);
        float h = Math.abs(y1);
        int bitmapWidth = getBitmapWidth();
        int bitmapHeight = getBitmapHeight();

        PointF pointF = new PointF();
        pointF.x = w * bitmapWidth / width;
        pointF.y = h * bitmapHeight / height;
        return pointF;
    }

    public int getBitmapWidth() {
//        return picWidth;
        return gintama.getWidth();
    }

    public int getBitmapHeight() {
//        return picHeight;
        return gintama.getHeight();
    }

    //裁剪区域
    public float getCropWidth() {
        float scale = getScale();
        int width = getWidth();
        return scale * width;
    }

    public float getCropHeight() {
        float scale = getScale();
        int height = getHeight();
        return scale * height;
    }

    public float getScale() {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];
        float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];

        float v = x2 - x1;
        int width = gintama.getWidth();
        float scale = width / v;
        return scale;
    }

}

