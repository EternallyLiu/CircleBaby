package cn.timeface.circle.baby.ui.calendar.events;

import java.io.Serializable;

/**
 * Created by JieGuo on 16/10/12.
 */

public class CommemorationAddedEvent implements Serializable {

    private static long serialVersionUID = 11L;

    private String month, day, content;

    public CommemorationAddedEvent(String month, String day, String content) {
        this.month = month;
        this.day = day;
        this.content = content;
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
}
