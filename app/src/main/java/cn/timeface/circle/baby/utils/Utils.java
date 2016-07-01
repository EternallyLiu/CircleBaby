package cn.timeface.circle.baby.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.github.promeg.pinyinhelper.Pinyin;
import com.ta.utdid2.android.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.TemplateAreaObj;
import cn.timeface.circle.baby.api.models.objs.TemplateObj;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.AbsoluteLayout.ImageLayout;
import cn.timeface.circle.baby.views.ScissorUtils;
import cn.timeface.common.utils.ChannelUtil;
import cn.timeface.common.utils.CheckedUtil;
import cn.timeface.common.utils.DeviceUtil;
import cn.timeface.common.utils.DeviceUuidFactory;
import cn.timeface.common.utils.MD5;
import cn.timeface.common.utils.StorageUtil;
import cn.timeface.common.utils.TimeFaceUtilInit;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author rayboot
 * @from 14/11/14 08:34
 * @TODO
 */
public class Utils {

    public static final String CPU_ARCHITECTURE_TYPE_32 = "32";
    public static final String CPU_ARCHITECTURE_TYPE_64 = "64";

    /**
     * ELF文件头 e_indent[]数组文件类标识索引
     */
    private static final int EI_CLASS = 4;
    /**
     * ELF文件头 e_indent[EI_CLASS]的取值：ELFCLASS32表示32位目标
     */
    private static final int ELFCLASS32 = 1;
    /**
     * ELF文件头 e_indent[EI_CLASS]的取值：ELFCLASS64表示64位目标
     */
    private static final int ELFCLASS64 = 2;

    /**
     * The system property key of CPU arch type
     */
    private static final String CPU_ARCHITECTURE_KEY_64 = "ro.product.cpu.abilist64";

    /**
     * The system libc.so file path
     */
    private static final String SYSTEM_LIB_C_PATH = "/system/lib/libc.so";
    private static final String SYSTEM_LIB_C_PATH_64 = "/system/lib64/libc.so";
    private static final String PROC_CPU_INFO_PATH = "/proc/cpuinfo";

    private static final char[] RMB_NUMS = "零一二三四五六七八九".toCharArray();
    private static final String[] U1 = {"", "十", "百", "千"};
    private static final String[] U2 = {"", "万", "亿"};

    public static Map<String, String> getHttpHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("LOC", DeviceUtil.getLanguageInfo());
        headers.put("USERID", FastData.getUserId());
        headers.put("DEVICEID", new DeviceUuidFactory(TimeFaceUtilInit.getContext()).getDeviceId());
        headers.put("TOKEN", TextUtils.isEmpty(FastData.getUserToken()) ? "timeface_android" : FastData.getUserToken());
        headers.put("VERSION", "10");
        headers.put("CHANNEL", ChannelUtil.getChannel(TimeFaceUtilInit.getContext()));
        return headers;
    }

    public static int getActionBarSize(Context context) {
        return (int) context.getResources()
                .getDimension(R.dimen.abc_action_bar_default_height_material);
    }

    /**
     * 转换订单状态
     */
    public static String processStatus(int status) {
        String statusStr = null;
        switch (status) {
            case TypeConstant.STATUS_CHECKING:
                statusStr = "审核中";
                break;

            case TypeConstant.STATUS_CHECK_FAILED:
                statusStr = "审核未通过";
                break;

            case TypeConstant.STATUS_PRINTING:
                statusStr = "印刷中";
                break;

            case TypeConstant.STATUS_DELIVERING:
                statusStr = "配送中";
                break;

            case 4:
                statusStr = "退款中";
                break;

            case TypeConstant.STATUS_DELIVERY_SUCCESS:
                statusStr = "已送达";
                break;

            case 6:
                statusStr = "已关闭";
                break;

            case TypeConstant.STATUS_TRANSACATION_CLOSED:
                statusStr = "交易关闭";
                break;
//            // 未确认
//            case Constant.STATUS_NOT_CONFIRM:
//                statusStr = "未确认";
//                break;
            // 未支付
            case TypeConstant.STATUS_NOT_PAY:
                statusStr = "未支付";
                break;
        }
        return statusStr;
    }

    // 隐藏软键盘
    public static void hideSoftInput(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showSoftInput(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static String getPinYin(String string) {
        if (string == null)
            return null;
        if (string.length() < 1) {
            //            return "*";
            return "#";
        }

        if (CheckedUtil.isAlphabet(string.charAt(0))) {
            return string.substring(0, 1).toUpperCase();
        } else if (CheckedUtil.isNumeric(string.substring(0, 1))) {
//            return string.substring(0, 1);
            return "#";
        } else if (CheckedUtil.isChineseLetter(string.charAt(0))) {
            String[] pinyinArray = new String[]{Pinyin.toPinyin(string.charAt(0))};
//                    PinyinHelper.toHanyuPinyinStringArray(string.charAt(0));
            if (pinyinArray != null && pinyinArray.length > 0
                    && !TextUtils.isEmpty(pinyinArray[0].trim())
                    && pinyinArray[0].trim().length() > 0) {
                return pinyinArray[0].trim().substring(0, 1).toUpperCase();
            } else {
                return "#";
            }
        }
        return "#";
    }

    //汉字，全角字符算1个字，其他算半个字
    public static float getTFTextLength(String text) {
        float result = 0;
        if (TextUtils.isEmpty(text)) {
            return result;
        }
        for (int i = 0; i < text.length(); i++) {
            char s = text.charAt(i);
            if (CheckedUtil.isChineseLetter(s)) {
                result += 1;
            } else {
                result += 0.5;
            }
        }
        return result;
    }

    public static boolean openDownloadManagerState(Context context) {
        String packageName = "com.android.providers.downloads";
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
        }
        return false;
    }

    public static File getNullFile(Context context) {
        File nullFile = StorageUtil.getTFPhotoPath("null_file.jpg");
        if (nullFile.exists()) {
            return nullFile;
        }
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.null_file);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(nullFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            out = null;
        }
        return nullFile;
    }

    /**
     * show RevealBackground anim
     *
     * @param clickView
     * @param root
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void showRevealAnim(View clickView, View root, int sdkInt) {
        if (sdkInt < 20) {
            return;
        }
        int[] location = new int[]{0, 0};
        if (clickView != null) {
            location = new int[2];
            clickView.getLocationOnScreen(location);
        }
        int finalRadius = Math.max(root.getWidth(), root.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(root, location[0], location[1], 0, finalRadius);
        root.setVisibility(View.VISIBLE);
        anim.start();
    }

    /**
     * dismiss RevealBackground anim
     *
     * @param clickView
     * @param root
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void dismissRevealAnim(View clickView, final View root, int sdkInt) {
        if (sdkInt < 20) {
            return;
        }

        int finalRadius = root.getWidth();
        int[] location = new int[]{0, 0};
        if (clickView != null) {
            location = new int[2];
            clickView.getLocationOnScreen(location);
        }
        Animator anim = ViewAnimationUtils.createCircularReveal(root, location[0], location[1], finalRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                root.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    /**
     * 根据podtype转换成请求参数booktype
     *
     * @param podType
     * @return
     */
    public static int changePodType2BookType(int podType) {
        int bookType = 0;
        switch (podType) {
            case TypeConstant.POD_TYPE_TIME_BOOK:
                bookType = TypeConstant.BOOK_TYPE_TIME;
                break;

            case TypeConstant.POD_TYPE_CIRCLE_CONTACT:
                bookType = TypeConstant.BOOK_TYPE_CIRCLE_CONTACT;
                break;

            case TypeConstant.POD_TYPE_TIME_BOOK_CIRCLE:
                bookType = TypeConstant.BOOK_TYPE_CIRCLE;
                break;

            case TypeConstant.POD_TYPE_TIME_BOOK_WECHAT:
                bookType = TypeConstant.BOOK_TYPE_WECHAT;
                break;

            case TypeConstant.POD_TYPE_TIME_BOOK_QQ:
                bookType = TypeConstant.BOOK_TYPE_QQ;
                break;

            case TypeConstant.POD_TYPE_TIME_BOOK_BLOG:
                bookType = TypeConstant.BOOK_TYPE_BLOG;
                break;
        }
        return bookType;
    }

    /**
     * 根据booktype转换成请求参数podtype
     *
     * @param bookType
     * @return
     */
    public static int changeBookType2PodType(int bookType) {
        int podType = 0;
        switch (bookType) {
            case TypeConstant.BOOK_TYPE_TIME:
                podType = TypeConstant.POD_TYPE_TIME_BOOK;
                break;
            case TypeConstant.BOOK_TYPE_CIRCLE_CONTACT:
                podType = TypeConstant.POD_TYPE_CIRCLE_CONTACT;
                break;
            case TypeConstant.BOOK_TYPE_CIRCLE:
                podType = TypeConstant.POD_TYPE_TIME_BOOK_CIRCLE;
                break;
            case TypeConstant.BOOK_TYPE_WECHAT:
                podType = TypeConstant.POD_TYPE_TIME_BOOK_WECHAT;
                break;
            case TypeConstant.BOOK_TYPE_QQ:
                podType = TypeConstant.POD_TYPE_TIME_BOOK_QQ;
                break;

            case TypeConstant.BOOK_TYPE_BLOG:
                podType = TypeConstant.POD_TYPE_TIME_BOOK_BLOG;
                break;
        }
        return podType;
    }

    /**
     * 布局到指定位置
     *
     * @param view
     * @param x
     * @param y
     */
    public static void setLayout(View view, int x, int y) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(x, y, 0, 0);
        view.setLayoutParams(layoutParams);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator createAnimation(View v, Boolean isFirst, int x, int y, float sr, float er) {
        Animator animator;
        if (isFirst) {
            animator = ViewAnimationUtils.createCircularReveal(
                    v,// 操作的视图
                    x,// 动画开始的中心点X
                    y,// 动画开始的中心点Y
                    sr,// 动画开始半径
                    er);// 动画结束半径
        } else {
            animator = ViewAnimationUtils.createCircularReveal(
                    v,// 操作的视图
                    x,// 动画开始的中心点X
                    y,// 动画开始的中心点Y
                    sr,// 动画开始半径
                    er);// 动画结束半径
        }

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(800);
        return animator;
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    public static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static Bitmap getScreenViewBitmap(View v) {
        v.setDrawingCacheEnabled(true);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return b;
    }

    public static Bitmap canvasBitmap(String str, int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(30f);
        textPaint.setColor(Color.GREEN);
        c.translate(0, 0);
        StaticLayout staticLayout = new StaticLayout(str, textPaint, 256, Layout.Alignment.ALIGN_CENTER, 1.3f, 0, false);
        staticLayout.draw(c);
        return bitmap;
    }

    /**
     * @param integer
     * @return
     */
    public static String getChineseNo(String integer) {
        StringBuilder buffer = new StringBuilder();
        // 从个位数开始转换
        int i, j;
        for (i = integer.length() - 1, j = 0; i >= 0; i--, j++) {
            char n = integer.charAt(i);
            if (n == '0') {
                // 当n是0且n的右边一位不是0时，插入[零]
                if (i < integer.length() - 1 && integer.charAt(i + 1) != '0') {
                    buffer.append(RMB_NUMS[0]);
                }
                // 插入[万]或者[亿]
                if (j % 4 == 0) {
                    if (i > 0 && integer.charAt(i - 1) != '0'
                            || i > 1 && integer.charAt(i - 2) != '0'
                            || i > 2 && integer.charAt(i - 3) != '0') {
                        buffer.append(U2[j / 4]);
                    }
                }
            } else {
                if (j % 4 == 0) {
                    buffer.append(U2[j / 4]);     // 插入[万]或者[亿]
                }
                buffer.append(U1[j % 4]);         // 插入[拾]、[佰]或[仟]
                buffer.append(RMB_NUMS[n - '0']); // 插入数字
            }
        }
        return buffer.reverse().toString();
    }

    public static ArrayList<TemplateAreaObj> getTemplateArea(TemplateObj item, int areaType) {
        ArrayList<TemplateAreaObj> areaItems = new ArrayList<>();
        List<TemplateAreaObj> templateList = item.getTemplateList();
        for (TemplateAreaObj areaItem : templateList) {
            if (areaItem.getType() == areaType) {
                areaItems.add(areaItem);
            }
        }
        return areaItems;
    }

    public static ImageLayout.LayoutParams getLayoutParams(TemplateAreaObj areaItem) {
        ImageLayout.LayoutParams layoutParams = new ImageLayout.LayoutParams();
        layoutParams.left = (int) areaItem.getLeft() - 2;
        layoutParams.top = (int) areaItem.getTop();
        layoutParams.width = (int) areaItem.getWidth() + 2;
        layoutParams.height = (int) areaItem.getHeight() + 2;
        return layoutParams;
    }

    public static int getTextAlign(int textAlign) {
        int gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        switch (textAlign) {
            case 0://居左
                gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case 1://居右
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
            case 2://居中
                gravity = Gravity.CENTER;
                break;
        }
        return gravity;
    }

    public static void setupInputText(TextView textView, String content, String defContent) {
        boolean empty = TextUtils.isEmpty(content);
        String text = empty ? defContent : content;
        if (empty) {
            textView.setText("");
            textView.setHintTextColor(Color.parseColor("#039ae3"));
            textView.setHint(text);
        } else {
            textView.setText(text);
        }
    }

    /**
     * 四舍五入取小数点后面两位
     */
    public static float floatRound(float money) {
        BigDecimal b = new BigDecimal(money);
        money = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return money;
    }

    public static int getWidth(String filePath) {
        Point size = ImageUtil.getDefault().getImgSize(filePath);
        int orientation = ImageUtil.getDefault().getImageOrientation(filePath);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270 || orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return size.y;
        } else {
            return size.x;
        }
    }

    public static int getHeight(String filePath) {
        Point size = ImageUtil.getDefault().getImgSize(filePath);
        int orientation = ImageUtil.getDefault().getImageOrientation(filePath);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270 || orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return size.x;
        } else {
            return size.y;
        }
    }

    /**
     * Get the CPU arch type: x32 or x64
     */
    public static String getArchType(Context context) {
        if (getSystemProperty(CPU_ARCHITECTURE_KEY_64, "").length() > 0) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else if (isCPUInfo64()) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else if (isLibc64()) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else {
            return CPU_ARCHITECTURE_TYPE_32;
        }
    }

    private static String getSystemProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(clazz, key, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * Read the first line of "/proc/cpuinfo" file, and check if it is 64 bit.
     */
    private static boolean isCPUInfo64() {
        File cpuInfo = new File(PROC_CPU_INFO_PATH);
        if (cpuInfo != null && cpuInfo.exists()) {
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                inputStream = new FileInputStream(cpuInfo);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 512);
                String line = bufferedReader.readLine();
                if (line != null && line.length() > 0 && line.toLowerCase(Locale.US).contains("arch64")) {
                    return true;
                } else {
                }
            } catch (Throwable t) {
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Check if system libc.so is 32 bit or 64 bit
     */
    private static boolean isLibc64() {
        File libcFile = new File(SYSTEM_LIB_C_PATH);
        if (libcFile != null && libcFile.exists()) {
            byte[] header = readELFHeadrIndentArray(libcFile);
            if (header != null && header[EI_CLASS] == ELFCLASS64) {
                return true;
            }
        }

        File libcFile64 = new File(SYSTEM_LIB_C_PATH_64);
        if (libcFile64 != null && libcFile64.exists()) {
            byte[] header = readELFHeadrIndentArray(libcFile64);
            if (header != null && header[EI_CLASS] == ELFCLASS64) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检测是否为ARM架构
     */
    public static boolean isARMFramework() {
        // CPU指令集 CPU_ABI:armeabi-v7a CPU_ABI2:armeabi
        if (Build.CPU_ABI.contains("arm")
                || Build.CPU_ABI2.contains("arm")) {
            return true;
        }
        return false;
    }

    /**
     * ELF文件头格式是固定的:文件开始是一个16字节的byte数组e_indent[16]
     * e_indent[4]的值可以判断ELF是32位还是64位
     */
    private static byte[] readELFHeadrIndentArray(File libFile) {
        if (libFile != null && libFile.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(libFile);
                if (inputStream != null) {
                    byte[] tempBuffer = new byte[16];
                    int count = inputStream.read(tempBuffer, 0, 16);
                    if (count == 16) {
                        return tempBuffer;
                    } else {
                    }
                }
            } catch (Throwable t) {
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static Observable<Object> getUserAvatar(String avatar) {
        if (avatar.contains("timeface")) {
            return Observable.just(avatar);
        } else {
            return Utils.loadPic(avatar);
        }
    }

    public static Observable<Object> loadPic(String url) {

        return Observable.just(url)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        File photoPath = StorageUtil.getTFPhotoPath(MD5.encode(url) + ".png");
                        Bitmap bitmap = null;
                        if (!photoPath.exists()) {
                            try {
                                URL newurl = new URL(url);
                                bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                            } catch (IOException e) {
                                e.printStackTrace();
                                if (photoPath.exists()) photoPath.delete();
                            }
                            return Observable.from(ScissorUtils.flushToFile(bitmap, Bitmap.CompressFormat.PNG, 100, photoPath));
                        } else {
                            return Observable.just(photoPath.getAbsolutePath());
                        }
                    }
                })
                .map(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        return new File(s);
                    }
                })
                .compose(SchedulersCompat.applyIoSchedulers());
    }

    public static Observable<String> savePicToCammeraDir(String url) {
        return Observable.just(url)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        String subUrl = url.substring(url.lastIndexOf("/"));
                        File photoPath = StorageUtil.genSystemPhotoFile(subUrl);
                        Bitmap bitmap = null;
                        if (!photoPath.exists()) {
                            try {
                                URL newurl = new URL(url);
                                bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return Observable.from(ScissorUtils.flushToFile(bitmap, Bitmap.CompressFormat.JPEG, 100, photoPath));
                        } else {
                            return Observable.just(photoPath.getAbsolutePath());
                        }
                    }
                })
                .compose(SchedulersCompat.applyIoSchedulers())
                ;
    }


    /**
     * 检测网络是否可用
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;
    public static int getNetworkType(Context context) {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if(!StringUtils.isEmpty(extraInfo)){
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }


}
