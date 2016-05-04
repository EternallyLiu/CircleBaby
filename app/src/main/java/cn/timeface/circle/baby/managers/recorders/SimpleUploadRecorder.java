package cn.timeface.circle.baby.managers.recorders;

import android.text.TextUtils;

import java.util.ArrayList;

import cn.timeface.circle.baby.oss.Recorder;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.oss.uploadservice.UploadTaskInfo;
import cn.timeface.circle.baby.utils.FastData;


/**
 * taskid_name  -> task_name
 * current_task_id  -> taskid1
 * all_task ->  taskid1 , taskid2 , taskid3
 * taskid_total_count  -> 10
 * objkey1_key_task_list  ->  taskid1 , taskid2 , taskid3
 * taskid1_objkey1_key_info  ->  filepath , foldername
 * taskid1_task_key_list  -> objkey1 , objkey2 , objkey3
 * <p>
 * author: rayboot  Created on 15/10/10.
 * email : sy0725work@gmail.com
 */
public class SimpleUploadRecorder extends Recorder {

    static final String ALL_TASK = "all_task";
    static final String TASK_NAME = "_name";
    static final String TOTAL_COUNT_SUFFIX = "_total_count";
    static final String TASK_KEY_LIST = "_task_key_list";
    static final String KEY_TASK_LIST = "_key_task_list";
    static final String KEY_INFO = "_key_info";
    static final String CURRENT_TASK_ID = "current_task_id";

    @Override
    public void clear() {
        String allTasks = FastData.getString(ALL_TASK, null);
        if (TextUtils.isEmpty(allTasks)) {
            return;
        }
        String[] tasks = allTasks.split(",");
        if (tasks.length == 0) {
            return;
        }
        for (String task : tasks) {
            deleteTask(task);
        }
        FastData.remove(ALL_TASK);
    }

    @Override
    public void deleteTask(String taskId) {
        //删除主队列中的taskId
        deleteValue(ALL_TASK, taskId);

        //删除任务包含文件的个数字段
        FastData.remove(taskId + TOTAL_COUNT_SUFFIX);

        String[] objkeys = FastData.getString(taskId + TASK_KEY_LIST, "").split(",");
        for (String key : objkeys) {
            FastData.remove(taskId + "_" + key + KEY_INFO);
            FastData.remove(key + KEY_TASK_LIST);
        }

        //删除任务对应的所有key
        FastData.remove(taskId + TASK_KEY_LIST);

        //删除任务标题
        FastData.remove(taskId + TASK_NAME);
    }

    @Override
    public void addTask(UploadTaskInfo uploadTaskInfo) {
        //all_task
        appendValue(ALL_TASK, uploadTaskInfo.getInfoId());

        //添加任务标题
//        FastData.putString(uploadTaskInfo.getInfoId() + TASK_NAME, uploadTaskInfo.getInfoName());
        FastData.putString(uploadTaskInfo.getInfoId() + TASK_NAME, "infoName");

        //id_total_count -> 任务包含多少文件上传子任务
        FastData.putInt(uploadTaskInfo.getInfoId() + TOTAL_COUNT_SUFFIX, uploadTaskInfo.getFileObjs().size());
        //id_task_key_list -> 单任务包含的所有文件objectkey(多个key   ,   分割)
        StringBuilder allKeys = new StringBuilder();
        for (UploadFileObj fileObj : uploadTaskInfo.getFileObjs()) {
            allKeys.append(fileObj.getObjectKey());
            allKeys.append(",");

            //key_key_task_list -> 所有有该key的任务
            appendValue(fileObj.getObjectKey() + KEY_TASK_LIST, uploadTaskInfo.getInfoId());

            //taskid_key_info -> key的信息
            FastData.putString(uploadTaskInfo.getInfoId() + "_" + fileObj.getObjectKey() + KEY_INFO, fileObj.getFilePath());

        }
        if (allKeys.length() > 0) {
            allKeys.deleteCharAt(allKeys.length() - 1);
        }
        FastData.putString(uploadTaskInfo.getInfoId() + TASK_KEY_LIST, allKeys.toString());
    }

    @Override
    public int oneFileCompleted(String taskId, String objectKey) {
        //删除key 对应的任务
        deleteValue(objectKey + KEY_TASK_LIST, taskId);

        //删除文件信息 taskid_key_info
        FastData.remove(taskId + "_" + objectKey + KEY_INFO);

        //删除任务队列里的objkey
        return deleteValue(taskId + TASK_KEY_LIST, objectKey);
    }

    @Override
    public void oneFileProgress(String objectKey, int byteCount, int totalSize) {

    }

    @Override
    public void oneFileFailure(String objectKey) {

    }

    @Override
    public String[] getTaskIDs(String objectKey) {
        String allTasks = FastData.getString(objectKey + KEY_TASK_LIST, null);
        if (TextUtils.isEmpty(allTasks)) {
            return null;
        }
        return allTasks.split(",");
    }

    @Override
    public int getTaskFileCount(String taskId) {
        return FastData.getInt(taskId + TOTAL_COUNT_SUFFIX, 0);
    }

    @Override
    public String getCurrentTaskId() {
        return FastData.getString(CURRENT_TASK_ID, null);
    }


    @Override
    public String getTaskName(String taskId) {
        return FastData.getString(taskId + TASK_NAME, null);
    }

    @Override
    public ArrayList<UploadFileObj> getUploadFileObjs(String taskId) {
        ArrayList<UploadFileObj> result = new ArrayList<>(10);
        for (String objectKey : getTaskKeys(taskId)) {
            if (!TextUtils.isEmpty(getKeyInfo(taskId, objectKey))) {
//                result.add(new SimpleUploadFileObj(getKeyInfo(taskId, objectKey).split(",")[0]
//                        , getKeyInfo(taskId, objectKey).split(",")[1]
//                        , objectKey));
            }
        }
        return result;
    }

    private String[] getTaskKeys(String taskId) {
        return FastData.getString(taskId + TASK_KEY_LIST, "").split(",");
    }

    private String getKeyInfo(String taskId, String objectKey) {
        return FastData.getString(taskId + "_" + objectKey + KEY_INFO, null);
    }

    /**
     * 对key追加value
     * 多个value使用 , 分割
     *
     * @param key
     * @param value
     */
    private void appendValue(String key, String value) {
        if (checkValue(key, value)) {
            return;
        }

        String result = FastData.getString(key, "");
        if (TextUtils.isEmpty(result)) {
            result = value;
        } else {
            result = result + "," + value;
        }
        FastData.putString(key, result);
    }

    /**
     * 对key删除value
     * 多个value使用 , 分割
     *
     * @param key
     * @param value
     * @return 该key还有多少个value
     */
    private int deleteValue(String key, String value) {
        String allValues = FastData.getString(key, null);
        if (TextUtils.isEmpty(allValues)) {
            return 0;
        }
        String[] values = allValues.split(",");
        if (!checkValue(key, value)) {
            return values.length;
        }

        int count = 0;
        StringBuilder result = new StringBuilder();
        for (String val : values) {
            if (val.equals(value)) {
                continue;
            }
            result.append(val);
            result.append(",");
            count++;
        }

        if (count == 0) {
            FastData.remove(key);
            return 0;
        }

        if (count > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        FastData.putString(key, result.toString());
        return count;
    }

    /**
     * 对key存储的对象，检测是否有value
     * 多个value使用 , 分割
     *
     * @param key
     * @param value
     */
    private boolean checkValue(String key, String value) {
        String allValues = FastData.getString(key, null);
        if (TextUtils.isEmpty(allValues)) {
            return false;
        }
        String[] values = allValues.split(",");
        if (values.length <= 0) {
            return false;
        }
        for (String val : values) {
            if (val.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
