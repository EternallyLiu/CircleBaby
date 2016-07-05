package com.wechat.photopicker.event;


import cn.timeface.circle.baby.api.models.objs.MediaObj;

/**
 * 类描述：
 * 创建人：黄龙源
 * 创建时间：2015/11/05 19:12
 * 修改人：黄龙源
 * 修改备注：
 */
public interface OnItemCheckListener2 {

  /***
   *
   * @param position 所选图片的位置
   * @param photo     所选的图片
   * @param isCheck   当前状态
   * @param selectorPathsSize  所选图片张数
   * @return enable check
   */
  boolean OnItemCheck(int position, MediaObj photo, boolean isCheck, int selectorPathsSize);

}
