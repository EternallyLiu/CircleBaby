package cn.timeface.circle.baby.api.models.base;

/**
 * author: rayboot  Created on 16/1/19.
 * email : sy0725work@gmail.com
 */
public class BaseListResponse extends BaseResponse {
    int currentPage;
    int totalPage;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public boolean more() {
        return currentPage < totalPage;
    }
}
