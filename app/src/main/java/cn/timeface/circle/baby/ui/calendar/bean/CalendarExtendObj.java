package cn.timeface.circle.baby.ui.calendar.bean;

import java.util.LinkedList;

import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;

/**
 * Created by JieGuo on 16/10/9.
 */

public class CalendarExtendObj extends TFOBookModel {

    private TFOBookModel frontModel, backModel;

    public CalendarExtendObj() {
    }

    public CalendarExtendObj(TFOBookModel tfoBookModel) {
        //CalendarExtendObj model = new CalendarExtendObj();
        setBookId(tfoBookModel.getBookId());
        setBookCover(tfoBookModel.getBookCover());
        setBookAuthor(tfoBookModel.getBookAuthor());
        setAuthorAvatar(tfoBookModel.getAuthorAvatar());
        setBookTitle(tfoBookModel.getBookTitle());
        setBookSummary(tfoBookModel.getBookSummary());
        setBookType(tfoBookModel.getBookType());
        setBookWidth(tfoBookModel.getBookWidth());
        setBookHeight(tfoBookModel.getBookHeight());
        setContentWidth(tfoBookModel.getContentWidth());
        setContentHeight(tfoBookModel.getContentHeight());
        setContentPaddingLeft(tfoBookModel.getContentPaddingLeft());
        setContentPaddingTop(tfoBookModel.getContentPaddingTop());
        setCreateDate(tfoBookModel.getCreateDate());
        setTotalPage(tfoBookModel.getTotalPage());
        setBookOrientation(tfoBookModel.getBookOrientation());
        setTemplateId(tfoBookModel.getTemplateId());
        setMyViewScale(tfoBookModel.getMyViewScale());
        setContentList(tfoBookModel.getContentList());
    }

    private void initValue(TFOBookModel newModel) {
        newModel.setBookId(getBookId());
        newModel.setBookCover(getBookCover());
        newModel.setBookAuthor(getBookAuthor());
        newModel.setAuthorAvatar(getAuthorAvatar());
        newModel.setBookType(getBookType());
        newModel.setBookTitle(getBookTitle());
        newModel.setBookWidth(getBookWidth());
        newModel.setBookHeight(getBookHeight());
        newModel.setContentWidth(getContentWidth());
        newModel.setContentHeight(getContentHeight());
        newModel.setContentPaddingLeft(getContentPaddingLeft());
        newModel.setContentPaddingTop(getContentPaddingTop());
        newModel.setCreateDate(getCreateDate());
        newModel.setTotalPage(getTotalPage());
        newModel.setBookOrientation(getBookOrientation());
        newModel.setTemplateId(getTemplateId());
        newModel.setMyViewScale(getMyViewScale());
        newModel.setContentList(new LinkedList<>());

    }

    /**
     * 获取正面的数据
     *
     * @return TFOBookModel
     */
    public TFOBookModel getFrontSide() {
        if (frontModel == null) {
            frontModel = new TFOBookModel();
            initValue(frontModel);
        }

        frontModel.getContentList().clear();
        for (int i = 1; i <= getContentList().size(); i++) {
            int index = i - 1;
            if (i % 2 == 0) {
                frontModel.getContentList().add(getContentList().get(index));
            }
        }
        // 封底页过虑掉
        frontModel.getContentList().remove(
                frontModel.getContentList().size() - 1
        );

        return frontModel;
    }

    /**
     * 获取背面数据
     *
     * @return TFOBookModel
     */
    public TFOBookModel getBackSide() {
        if (backModel == null) {
            backModel = new TFOBookModel();
            initValue(backModel);
        }

        backModel.getContentList().clear();
        for (int i = 1; i <= getContentList().size(); i++) {
            int index = i - 1;
            if (i % 2 != 0) {
                backModel.getContentList().add(getContentList().get(index));
            }
        }
        // 过虑掉封面
        backModel.getContentList().remove(0);
        return backModel;
    }

    public void refreshData() {
        getFrontSide();
        getBackSide();
    }

    /**
     * 替换一个Element
     *
     * @param editedEle 数据源
     * @param target    目标类
     */
    public void replaceElement(TFOBookElementModel editedEle, TFOBookElementModel target) {
        //element = elementModel;
        target.setElementId(editedEle.getElementId());
        target.setElementIndex(editedEle.getElementIndex());
        //target.setElementName(editedEle.getElementName());   // 狗日的，服务器会莫名其妙的把一个这个值给弄成空
        target.setElementFlag(editedEle.getElementFlag());
        target.setElementTop((int) editedEle.getElementTop());
        target.setElementLeft(editedEle.getElementLeft());
        target.setElementWidth(editedEle.getElementWidth());
        target.setElementHeight(editedEle.getElementHeight());
        target.setElementDepth(editedEle.getElementDepth());
        target.setElementRotation(editedEle.getElementRotation());
        target.setElementType(editedEle.getElementType());
        target.setElementContent(editedEle.getElementContent());
        target.setElementDeleted(editedEle.getElementDeleted());
        target.setElementAssist(editedEle.getElementAssist());

        target.setElementBackground(editedEle.getElementBackground());
        target.setElementContentTop(editedEle.getElementContentTop());
        target.setElementContentLeft(editedEle.getElementContentLeft());
        target.setElementContentRight(editedEle.getElementContentRight());
        target.setElementContentBottom(editedEle.getElementContentBottom());
        target.setElementMaskImage(editedEle.getElementMaskImage());
        target.setElementExceedAlpha(editedEle.getElementExceedAlpha());
        target.setImageContentExpand(editedEle.getImageContentExpand());
        target.setTextContentExpand(editedEle.getTextContentExpand());
        target.setElementFrontMaskImage(editedEle.getElementFrontMaskImage());


        getFrontSide();
        getBackSide();
    }
}
