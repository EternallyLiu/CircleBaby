package cn.timeface.circle.baby.api.models.objs;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * @author YW.SUN
 * @from 2015/5/26
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PrintPropertyTypeObj extends BasePrintProperty {
    private String bookId;
    private int bookType;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }
}
