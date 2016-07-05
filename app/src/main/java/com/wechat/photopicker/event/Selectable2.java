package com.wechat.photopicker.event;

import com.wechat.photopicker.endity.Photo;

import java.util.List;

import cn.timeface.circle.baby.api.models.objs.MediaObj;


/**
 * 类描述：选中事件
 */
public interface Selectable2 {
  /**
   * 判断图片是否被选中
   * @param photo
   * @return
   */
  boolean isSelected(MediaObj photo);

  /**
   * 切换图片选中状态
   * @param photo
   */
  void toggleSelection(MediaObj photo);

  /**
   * 清除所有被选中的图片
   */
  void clearSelection();

  /**
   * 获得选中图片的张数
   * @return
   */
  int getSelectedItemCount();

  /**
   * 获取被选中图片
   * @return
   */
  List<MediaObj> getSelectedPhotos();

}
