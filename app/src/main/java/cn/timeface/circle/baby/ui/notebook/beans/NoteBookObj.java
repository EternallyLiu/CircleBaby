package cn.timeface.circle.baby.ui.notebook.beans;

import java.util.ArrayList;

import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookImageModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;

/**
 * Created by JieGuo on 16/11/21.
 */

public class NoteBookObj extends TFOBookModel {


    public NoteBookObj(TFOBookModel tfoBookModel) {

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
        setContentList(new ArrayList<>(tfoBookModel.getContentList()));
    }


    public static void copyElement(TFOBookElementModel source, TFOBookElementModel target) {
        target.setElementId(source.getElementId());
        target.setElementIndex(source.getElementIndex());
        target.setElementFlag(source.getElementFlag());
        target.setElementTop((int) source.getElementTop());
        target.setElementLeft(source.getElementLeft());
        target.setElementWidth(source.getElementWidth());
        target.setElementHeight(source.getElementHeight());
        target.setElementDepth(source.getElementDepth());
        target.setElementRotation(source.getElementRotation());
        target.setElementType(source.getElementType());
        target.setElementContent(source.getElementContent());
        target.setElementDeleted(source.getElementDeleted() == 1);
        target.setElementAssist(source.getElementAssist());

        target.setElementBackground(source.getElementBackground());
        target.setElementContentTop(source.getElementContentTop());
        target.setElementContentLeft(source.getElementContentLeft());
        target.setElementContentRight(source.getElementContentRight());
        target.setElementContentBottom(source.getElementContentBottom());
        target.setElementMaskImage(source.getElementMaskImage());
        target.setElementExceedAlpha(source.getElementExceedAlpha());
        target.setImageContentExpand(source.getImageContentExpand());
        target.setTextContentExpand(source.getTextContentExpand());
        target.setElementFrontMaskImage(source.getElementFrontMaskImage());
    }

    public static void replaceImageElement(ImgObj source, TFOBookElementModel target) {

        TFOBookImageModel imageModel = target.getImageContentExpand();
        if (imageModel == null) {
            return;
        }
        target.setElementDeleted(false);
        // FIXME: 2017/1/9 记事本功能完善
//        target.setElementContent(source.getImage_url());
//        imageModel.setImageUrl(source.getImage_url());
//        imageModel.setImageWidth(source.getImage_width());
//        imageModel.setImageHeight(source.getImage_height());
//        imageModel.setImageOrientation(source.getImage_orientation());
        imageModel.setImageFlipHorizontal(0);
        imageModel.setImageFlipVertical(0);
        imageModel.setImageRotation(0);

        // FIXME: 2017/1/9 记事本功能完善
//        float xScale = target.getContentWidth() / source.getImage_width();
//        float yScale = target.getElementHeight() / source.getImage_height();
//        float scale = Math.max(xScale, yScale);
//        imageModel.setImageScale(scale);
    }

    public static void replaceImageElment(TFOBookImageModel source, TFOBookElementModel target) {

        TFOBookImageModel imageModel = target.getImageContentExpand();

        target.setElementDeleted(false);
        target.setElementContent(source.getImageUrl());

        imageModel.setImageUrl(source.getImageUrl());
        imageModel.setImageWidth(source.getImageWidth());
        imageModel.setImageHeight(source.getImageHeight());
        imageModel.setImageOrientation(source.getImageOrientation());
        imageModel.setImageFlipHorizontal(0);
        imageModel.setImageFlipVertical(0);
        imageModel.setImageRotation(0);
        float xScale = target.getContentWidth() / source.getImageWidth();
        float yScale = target.getElementHeight() / source.getImageHeight();

        float scale = Math.max(xScale, yScale);
        imageModel.setImageScale(scale);
    }

    public static void copyContent2Content(TFOBookContentModel source, TFOBookContentModel target) {

        target.getElementList().clear();
        target.setElementList(new ArrayList<>(source.getElementList()));
        target.setPageColor(source.getPageColor());
        target.setPageImage(source.getPageImage());
        target.setPageZoom(source.getPageZoom());
        target.setTemplateFileName(source.getTemplateFileName());
        target.setTemplateId(source.getTemplateId());
        target.setWebContent(source.getWebContent());
    }
}
