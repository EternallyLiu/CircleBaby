package cn.timeface.circle.baby.ui.calendar.events;

import java.io.Serializable;

/**
 * Created by JieGuo on 16/10/12.
 */

public class CommemorationUpdateEvent implements Serializable {
    private static long serialVersionUID = 10L;

    private String oldMonth, oldDay, oldContent;
    private String month, day, content;

    public CommemorationUpdateEvent(String oldContent, String oldDay, String oldMonth) {
        this.oldContent = oldContent;
        this.oldDay = oldDay;
        this.oldMonth = oldMonth;
    }

    public String getOldContent() {
        return oldContent;
    }

    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    public String getOldDay() {
        return oldDay;
    }

    public void setOldDay(String oldDay) {
        this.oldDay = oldDay;
    }

    public String getOldMonth() {
        return oldMonth;
    }

    public void setOldMonth(String oldMonth) {
        this.oldMonth = oldMonth;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getContent() {
        return content;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
