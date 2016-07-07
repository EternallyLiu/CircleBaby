package cn.timeface.open.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import cn.timeface.open.R;
import cn.timeface.open.activities.EditActivity;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.events.ChangeStickerStatusEvent;
import cn.timeface.open.utils.DeviceUtil;

public class StickerView extends FrameLayout {

    public static final String TAG = "StickerView";
    private BorderView iv_border;
    private ImageView iv_scale;
    private ImageView iv_delete;
    private ImageView iv_beauty;
    String content_id;

    // For moving
    private float move_orgX = -1, move_orgY = -1;

    public final static int BUTTON_SIZE_DP = 10;
    int half_btn_size;
    boolean canMove = true;

    Rect orgRect = new Rect();
    Rect orgMainViewRect = new Rect();
    float diagonalOrg;
    int[] location = new int[2];
    int marginLeft, marginTop;
    long clickTime;
    float clickRawX, clickRawY;

    TFOBookElementModel elementModel;

    public StickerView(Context context, String contentId, TFOBookElementModel elementModel) {
        super(context);
        this.elementModel = elementModel;
        this.content_id = contentId;
        init(context);
    }

    public StickerView(Context context) {
        super(context);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.iv_border = new BorderView(context);

        this.iv_scale = new ImageView(context);
        this.iv_delete = new ImageView(context);
        this.iv_beauty = new ImageView(context);

        this.iv_scale.setImageResource(R.drawable.icon_scale);
        this.iv_delete.setImageResource(R.drawable.icon_delete);
        this.iv_beauty.setImageResource(R.drawable.icon_flip);

        this.setTag("DraggableViewGroup");
        this.iv_border.setTag("iv_border");
        this.iv_scale.setTag("iv_scale");
        this.iv_delete.setTag("iv_delete");
        this.iv_beauty.setTag("iv_beauty");

        int btn_size = DeviceUtil.dpToPx(getResources(), BUTTON_SIZE_DP);
        half_btn_size = btn_size / 2;

        LayoutParams lp_main = new LayoutParams((int) elementModel.getElementWidth() + btn_size, (int) elementModel.getElementHeight() + btn_size);
        lp_main.gravity = Gravity.CENTER;

        LayoutParams lp_element = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp_element.setMargins(half_btn_size, half_btn_size, half_btn_size, half_btn_size);

        LayoutParams lp_border = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp_border.setMargins(half_btn_size, half_btn_size, half_btn_size, half_btn_size);

        LayoutParams lp_scale = new LayoutParams(btn_size, btn_size);
        lp_scale.gravity = Gravity.BOTTOM | Gravity.END;

        LayoutParams lp_delete = new LayoutParams(btn_size, btn_size);
        lp_delete.gravity = Gravity.TOP | Gravity.END;

        LayoutParams lp_flip = new LayoutParams(btn_size, btn_size);
        lp_flip.gravity = Gravity.TOP | Gravity.START;

        this.setLayoutParams(lp_main);
        this.addView(getMainView(), lp_element);
        this.addView(iv_border, lp_border);
        this.addView(iv_scale, lp_scale);
        this.addView(iv_delete, lp_delete);
        this.addView(iv_beauty, lp_flip);
        this.setOnTouchListener(mTouchListener);
        this.iv_scale.setOnTouchListener(mTouchListener);

        this.setTag(R.string.tag_ex, content_id);
        this.setTag(R.string.tag_obj, elementModel);

        this.iv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StickerView.this.getParent() != null) {
                    ViewGroup rootView = ((ViewGroup) StickerView.this.getParent());
                    rootView.removeView(StickerView.this);

                }
            }
        });
        this.iv_beauty.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                View mainView = getMainView();
                mainView.setRotationY(mainView.getRotationY() == -180f ? 0f : -180f);
                mainView.invalidate();
                requestLayout();
            }
        });
    }

    private View getMainView() {
        if (elementModel != null) {
            return elementModel.getView(getContext());
        }
        return null;
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);
            {
                //首先做通用事件处理,判断是否为点击事件,以及触发选中效果
                if (action == MotionEvent.ACTION_DOWN) {
                    EventBus.getDefault().post(new ChangeStickerStatusEvent(action, StickerView.this));
                    clickTime = System.currentTimeMillis();
                    clickRawX = event.getRawX();
                    clickRawY = event.getRawY();
                } else if (action == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - clickTime < 10) {
                        RectF rect = new RectF(clickRawX - 5, clickRawY - 5, clickRawX + 5, clickRawY + 5);
                        if (rect.contains(event.getRawX(), event.getRawY())) {
                            EventBus.getDefault().post(new ChangeStickerStatusEvent(action, StickerView.this));
                        }
                    }
                }
            }

            if (!canMove) {
                return true;
            }

            //做拖动和放大处理
            if (view.getTag().equals("DraggableViewGroup")) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Log.v(TAG, "sticker view action down");
                        bringToFront();
                        move_orgX = event.getRawX();
                        move_orgY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.v(TAG, "sticker view action move");
                        float offsetX = event.getRawX() - move_orgX;
                        float offsetY = event.getRawY() - move_orgY;
                        move_orgX = event.getRawX();
                        move_orgY = event.getRawY();
                        StickerView.this.setX(StickerView.this.getX() + offsetX / EditActivity.SCALE);
                        StickerView.this.setY(StickerView.this.getY() + offsetY / EditActivity.SCALE);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.v(TAG, "sticker view action up");
                        break;
                }
            } else if (view.getTag().equals("iv_scale")) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        Log.v(TAG, "iv_scale action down");
                        LayoutParams orgLp = (LayoutParams) StickerView.this.getLayoutParams();

                        float rotation = StickerView.this.getRotation();
                        StickerView.this.setRotation(0);
                        StickerView.this.getLocationInWindow(location);
                        orgRect.set(location[0], location[1], location[0] + orgLp.width, location[1] + orgLp.height);
                        marginLeft = orgLp.leftMargin;
                        marginTop = orgLp.topMargin;
                        StickerView.this.setRotation(rotation);

                        orgMainViewRect = new Rect(orgRect.left + half_btn_size, orgRect.top + half_btn_size, orgRect.right - half_btn_size, orgRect.bottom - half_btn_size);
                        diagonalOrg = diagonalLength(orgMainViewRect.width(), orgMainViewRect.height()) / 2;
                        Log.v(TAG, orgRect.toString() + orgMainViewRect.toString() + "  rotate = " + StickerView.this.getRotation());
                        Log.v(TAG, "marginLeft = " + marginLeft + "  marginTop = " + marginTop + "   diagonalOrg = " + diagonalOrg);
                        Log.v(TAG, "getRawX = " + event.getRawX() + "  getRawY = " + event.getRawY());
                        Log.v(TAG, "EditActivity.SCALE = " + EditActivity.SCALE);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.v(TAG, "iv_scale action move");
                        move_orgX = event.getRawX();
                        move_orgY = event.getRawY();

                        float diagonalTo = diagonalLength((int) move_orgX - orgMainViewRect.centerX(), (int) move_orgY - orgMainViewRect.centerY());

                        Log.v(TAG, "diagonal = " + diagonalOrg + " diagonalTo = " + diagonalTo);

                        float scale = diagonalTo / diagonalOrg;
                        Log.v(TAG, "scale = " + scale);
                        int width_to = (int) (orgMainViewRect.width() * scale + half_btn_size * 2);
                        int height_to = (int) (orgMainViewRect.height() * scale + half_btn_size * 2);
                        LayoutParams lp = (LayoutParams) StickerView.this.getLayoutParams();
                        lp.width = width_to;
                        lp.height = height_to;
                        lp.leftMargin = marginLeft + (orgRect.width() - width_to) / 2;
                        lp.topMargin = marginTop + (orgRect.height() - height_to) / 2;
                        double angle = getAngle(orgMainViewRect.centerX(), orgMainViewRect.centerY(), orgMainViewRect.right, orgMainViewRect.bottom, move_orgX, move_orgY);

                        Log.v(TAG, "x = " + StickerView.this.getX() + " y = " + StickerView.this.getY());
                        Log.v(TAG, "width_to = " + width_to + " height_to = " + height_to);
                        Log.v(TAG, "leftMargin = " + lp.leftMargin + " topMargin = " + lp.topMargin);
                        setRotation((float) angle);

                        postInvalidate();
                        requestLayout();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.v(TAG, "iv_scale action up");
                        break;
                }
            }
            return true;
        }
    };

    /**
     * 感谢牛逼烘烘放光彩的茆老师
     * 计算夹角
     *
     * @param x1 顶点x
     * @param y1 顶点y
     * @param x2 起点x
     * @param y2 起点y
     * @param x3 终点x
     * @param y3 终点y
     * @return 旋转角度
     */
    public double getAngle(double x1, double y1, double x2, double y2, double x3, double y3) {
        double ab_ac = (x2 - x1) * (x3 - x1) + (y2 - y1) * (y3 - y1);
        double ab = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
        double ac = Math.sqrt(Math.pow((x1 - x3), 2) + Math.pow((y1 - y3), 2));
        double cosA = ab_ac / (ab * ac);
        double k = (y2 - y1) / (x2 - x1);
        boolean is180in = (y3 <= k * x3 + y1 - k * x1);
        if (is180in)
            return Math.toDegrees(2 * Math.PI - Math.acos(cosA));
        else
            return Math.toDegrees(Math.acos(cosA));
    }

    /**
     * 直角三角形斜边长度
     */
    private float diagonalLength(int x, int y) {
        return (float) Math.hypot(x, y);
    }

    private double getLength(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
    }

    private float[] getRelativePos(float absX, float absY) {
        Log.v("ken", "getRelativePos getX:" + ((View) this.getParent()).getX());
        Log.v("ken", "getRelativePos getY:" + ((View) this.getParent()).getY());
        float[] pos = new float[]{
                absX - ((View) this.getParent()).getX(),
                absY - ((View) this.getParent()).getY()
        };
        Log.v(TAG, "getRelativePos absY:" + absY);
        Log.v(TAG, "getRelativePos relativeY:" + pos[1]);
        return pos;
    }

    public TFOBookElementModel getFixedElementModel() {
        int sW = this.getWidth();
        int sH = this.getHeight();
        int eW = sW - half_btn_size * 2;
        int eH = sH - half_btn_size * 2;
        float scale = eW / elementModel.getElementWidth();

        elementModel.moveParams(getTranslationX(), getTranslationY(), eW, eH, scale, this.getRotation());
        return elementModel;
    }

    public void canMove(boolean canMove) {
        this.canMove = canMove;
    }

    public void showControlItems(boolean show) {
        if (show) {
            iv_border.setVisibility(View.VISIBLE);
            if (!canMove) {
                return;
            }

            switch (elementModel.getElementType()) {
                case TFOBookElementModel.TYPE_IMAGE:
                case TFOBookElementModel.TYPE_WEB:
                    iv_scale.setVisibility(View.VISIBLE);
                    iv_delete.setVisibility(View.VISIBLE);
                    iv_beauty.setVisibility(View.INVISIBLE);
                    break;
                case TFOBookElementModel.TYPE_TEXT:
                    iv_scale.setVisibility(View.INVISIBLE);
                    iv_delete.setVisibility(View.VISIBLE);
                    iv_beauty.setVisibility(View.INVISIBLE);
                    break;
                case TFOBookElementModel.TYPE_PENDANT:
                    iv_scale.setVisibility(View.VISIBLE);
                    iv_delete.setVisibility(View.VISIBLE);
                    iv_beauty.setVisibility(View.INVISIBLE);
                    break;
                case TFOBookElementModel.TYPE_AUDIO:
                case TFOBookElementModel.TYPE_VIDEO:
                    break;

            }
        } else {
            iv_border.setVisibility(View.INVISIBLE);
            iv_scale.setVisibility(View.INVISIBLE);
            iv_delete.setVisibility(View.INVISIBLE);
            iv_beauty.setVisibility(View.INVISIBLE);
        }
    }

    public boolean isShowControlItems() {
        return iv_border.getVisibility() == VISIBLE;
    }

    protected void onScaling(boolean scaleUp) {
    }

    protected void onRotating() {
    }

    private class BorderView extends View {
        Rect border = new Rect();
        Paint borderPaint = new Paint();

        public BorderView(Context context) {
            super(context);
        }

        public BorderView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public BorderView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // Draw sticker border

            LayoutParams params = (LayoutParams) this.getLayoutParams();

            border.left = this.getLeft() - params.leftMargin;
            border.top = this.getTop() - params.topMargin;
            border.right = this.getRight() - params.rightMargin;
            border.bottom = this.getBottom() - params.bottomMargin;
            borderPaint.setStrokeWidth(6);
            borderPaint.setColor(Color.RED);
            borderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(border, borderPaint);
        }
    }
}
