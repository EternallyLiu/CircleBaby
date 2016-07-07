package cn.timeface.open.api.models.objs;

/**
 * author: rayboot  Created on 16/6/1.
 * email : sy0725work@gmail.com
 */
public class TFOBookTypeModel {
    String book_type_id;//版式类型编号",
    int book_type;//1, //排版类型
    int[] book_pack_types;//[1,2,3], //支持的装订方式
    int[] book_paper_types;//[1,2,3], //支持的纸张类型
    int[] book_size_types;//[1,2,3] //支持的印刷开本尺寸
}
