package cn.timeface.circle.baby.views;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.util.ArrayList;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.api.models.objs.MarkOptionPhoto;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.DeviceUtil;

public class PhotoMarkerCluster {
	private Activity activity;
	private MarkerOptions options;
	private ArrayList<MarkOptionPhoto> includeMarkers;
	private LatLngBounds bounds;// 创建区域
	private MarkOptionPhoto photoModel;

	private ArrayList<MediaObj> selectedPhotoList = null;
	
	/**
	 * 
	 * @param activity
	 * @param firstMarkers
	 * @param projection
	 * @param gridSize 区域大小参数
	 */
	public PhotoMarkerCluster(Activity activity, MarkOptionPhoto firstMarkers,
							  Projection projection, int gridSize) {
		this.photoModel = firstMarkers;
		options = new MarkerOptions();
		this.activity = activity;
		Point point = projection.toScreenLocation(firstMarkers.getMarkerOptions().getPosition());
		Point southwestPoint = new Point(point.x - gridSize, point.y + gridSize);
		Point northeastPoint = new Point(point.x + gridSize, point.y - gridSize);
		bounds = new LatLngBounds(
				projection.fromScreenLocation(southwestPoint),
				projection.fromScreenLocation(northeastPoint));
		options.anchor(0.5f, 0.5f)
				.position(new LatLng(firstMarkers.getMediaObj().getLocation().getLat(),
						firstMarkers.getMediaObj().getLocation().getLog()))
				.icon(firstMarkers.getMarkerOptions().getIcon());
		includeMarkers = new ArrayList<>();
		includeMarkers.add(firstMarkers);
	}

	public ArrayList<MarkOptionPhoto> getIncludeMarkers() {
		return includeMarkers;
	}

	public ArrayList<MediaObj> getSelectedPhotoList() {
		selectedPhotoList = new ArrayList<>();
		for (MarkOptionPhoto markOptionPhoto : includeMarkers) {
			selectedPhotoList.add(markOptionPhoto.getMediaObj());
		}
		return selectedPhotoList;
	}

	/**
	 * 添加marker
	 */
	public void addMarker(MarkOptionPhoto markerOptions) {
		includeMarkers.add(markerOptions);// 添加到列表中
	}

	/**
	 * 设置聚合点的中心位置以及图标
	 */
	public void setpositionAndIcon(AMap aMap) {
		int size = includeMarkers.size();
		double lat = 0.0;
		double lng = 0.0;

		for (MarkOptionPhoto op : includeMarkers) {
			lat += op.getMediaObj().getLocation().getLat();
			lng += op.getMediaObj().getLocation().getLog();
		}
		options.position(new LatLng(lat / size, lng / size));// 设置中心位置为聚集点的平均位置
		getView(size, options, aMap);
	}

	public LatLngBounds getBounds() {
		return bounds;
	}

	public MarkerOptions getOptions() {
		return options;
	}

	public void setOptions(MarkerOptions options) {
		this.options = options;
	}

	public void getView(int carNum, MarkerOptions options, AMap aMap) {
		View view = activity.getLayoutInflater().inflate(
				R.layout.item_map_photo, null);
		TextView carNumTextView = (TextView) view.findViewById(R.id.tv_map_photo_num);
		ImageView myCarLayout = (ImageView) view
				.findViewById(R.id.img_map_photo_cover);
		if (carNum == 1) {
			carNumTextView.setVisibility(View.GONE);
		} else {
			carNumTextView.setVisibility(View.VISIBLE);
		}
		try {
			File curPhotoFile = new File(photoModel.getMediaObj().getImgUrl());
			if (curPhotoFile != null && curPhotoFile.exists()) {
				myCarLayout.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeFile(photoModel.getMediaObj().getImgUrl())));
				if (carNum > 0 && carNum <= 99) {
					carNumTextView.setWidth(DeviceUtil.dpToPx(activity.getResources() , 20));
					carNumTextView.setHeight(DeviceUtil.dpToPx(activity.getResources() , 20));
				} else if (carNum > 99 && carNum <= 999) {
					//给当前的数字的标签确定一个合适的尺寸
					carNumTextView.setWidth(DeviceUtil.dpToPx(activity.getResources(), 25));
					carNumTextView.setHeight(DeviceUtil.dpToPx(activity.getResources(), 25));
				} else if (carNum > 999) {
					//给当前的数字的标签确定一个合适的尺寸
					//给当前的数字的标签确定一个合适的尺寸
					carNumTextView.setWidth(DeviceUtil.dpToPx(activity.getResources(), 35));
					carNumTextView.setHeight(DeviceUtil.dpToPx(activity.getResources(), 35));
				}
				carNumTextView.setText(String.valueOf(carNum));
				options.icon(BitmapDescriptorFactory
						.fromBitmap(PhotoMarkerCluster.getViewBitmap(view)));
				aMap.addMarker(options);
			} else {
				if (!TextUtils.isEmpty(photoModel.getMediaObj().getImgUrl())) {
					Glide.with(activity)
							.load(photoModel.getMediaObj().getImgUrl())
							.placeholder(R.drawable.bg_none_photo)
							.into(new SimpleTarget<GlideDrawable>(250, 250) {
								@Override
								public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
									myCarLayout.setBackgroundDrawable(resource);
									if (carNum > 0 && carNum <= 99) {
										carNumTextView.setWidth(DeviceUtil.dpToPx(activity.getResources(), 20));
										carNumTextView.setHeight(DeviceUtil.dpToPx(activity.getResources(), 20));
									} else if (carNum > 99 && carNum <= 999) {
										//给当前的数字的标签确定一个合适的尺寸
										carNumTextView.setWidth(DeviceUtil.dpToPx(activity.getResources(), 25));
										carNumTextView.setHeight(DeviceUtil.dpToPx(activity.getResources(), 25));
									} else if (carNum > 999) {
										//给当前的数字的标签确定一个合适的尺寸
										//给当前的数字的标签确定一个合适的尺寸
										carNumTextView.setWidth(DeviceUtil.dpToPx(activity.getResources(), 35));
										carNumTextView.setHeight(DeviceUtil.dpToPx(activity.getResources(), 35));
									}
									carNumTextView.setText(String.valueOf(carNum));
									options.icon(BitmapDescriptorFactory
											.fromBitmap(PhotoMarkerCluster.getViewBitmap(view)));
									aMap.addMarker(options);
								}
							});
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * 把一个view转化成bitmap对象
	 */
	public static Bitmap getViewBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
}
