package cn.timeface.circle.baby.support.oss.uploadservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Abstract broadcast receiver from which to inherit when creating a receiver for {@link UploadService}.
 * <p>
 * It provides the boilerplate code to properly handle broadcast messages coming from the uploadWithProgress service and dispatch
 * them to the proper handler method.
 *
 * @author alexbbb (Alex Gotev)
 * @author eliasnaur
 */
public abstract class AbstractUploadServiceReceiver extends BroadcastReceiver {

    protected Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.intent = intent;
        if (intent != null) {
            if (UploadService.getActionBroadcast().equals(intent.getAction())) {
                final int uploadType = intent.getIntExtra(UploadService.PARAM_UPLOAD_TYPE, UploadService.TYPE_PUBLISH);
                final int status = intent.getIntExtra(UploadService.STATUS, 0);
                final String uploadId = intent.getStringExtra(UploadService.UPLOAD_ID);

                switch (status) {
                    case UploadService.STATUS_ERROR:
                        String exception = (String) intent
                                .getSerializableExtra(UploadService.ERROR_EXCEPTION);
                        onError(uploadId, new Exception(exception));
                        break;
                    case UploadService.STATUS_COMPLETED:
                        onCompleted(uploadId, uploadType);
                        break;
                    case UploadService.STATUS_IN_PROGRESS:
                        final int progress = intent.getIntExtra(UploadService.PROGRESS, 0);
                        onProgress(uploadId, progress);
                        final long uploadedBytes = intent.getLongExtra(UploadService.PROGRESS_UPLOADED_BYTES, 0);
                        final long totalBytes = intent.getLongExtra(UploadService.PROGRESS_TOTAL_BYTES, 1);
                        onProgress(uploadId, uploadedBytes, totalBytes);
                        break;
                    default:
                        break;
                }
            }
        }

    }

    /**
     * Register this uploadWithProgress receiver. It's recommended to register the receiver in Activity's onResume method.
     *
     * @param context context in which to register this receiver
     */
    public void register(final Context context) {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UploadService.getActionBroadcast());
        context.registerReceiver(this, intentFilter);
    }

    /**
     * Unregister this uploadWithProgress receiver. It's recommended to unregister the receiver in Activity's onPause method.
     *
     * @param context context in which to unregister this receiver
     */
    public void unregister(final Context context) {
        context.unregisterReceiver(this);
    }

    /**
     * Called when the uploadWithProgress progress changes.
     *
     * @param uploadId unique ID of the uploadWithProgress request
     * @param progress value from 0 to 100
     */
    public void onProgress(final String uploadId, final int progress) {
    }

    /**
     * Called when the uploadWithProgress progress changes.
     *
     * @param uploadId      unique ID of the uploadWithProgress request
     * @param uploadedBytes the count of the bytes uploaded so far
     * @param totalBytes    the total expected bytes to uploadWithProgress
     */
    public void onProgress(final String uploadId, final long uploadedBytes, final long totalBytes) {
    }

    /**
     * Called when an error happens during the uploadWithProgress.
     *
     * @param uploadId  unique ID of the uploadWithProgress request
     * @param exception exception that caused the error
     */
    public void onError(final String uploadId, final Exception exception) {
    }

    /**
     * Called when the uploadWithProgress is completed successfully.
     *
     * @param uploadId unique ID of the uploadWithProgress request
     */
    public void onCompleted(final String uploadId) {
    }

    public void onCompleted(final String uploadId, final int uploadType){

    }

}
