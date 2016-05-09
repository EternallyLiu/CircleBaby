package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.wechat.photopicker.fragment.BigImageFragment;
import com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.ActivityObj;

/**
 * Created by JieGuo on 1/22/16.
 */
public class FragmentBridgeActivity extends BaseAppCompatActivity {

    public static final String FRAGMENT_NAME = "fragment_name";
    public static final String ACTION_BAR_TITLE = "action_bar_title";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

//    public static void openSinUpFragment(Context context, MyActivityListItemResponse activityObj) {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("activityObj", activityObj);
//        open(context, "SinUpActivityFragment", activityObj.getActivityInfo().getName(), bundle);
//    }



    public static void openArticleDetailFragment(Context context, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        open(context, "ArticleDetailFragment", "文章详情", bundle);
    }
    public static void openBigimageFragment(Context context, ArrayList<String> paths, int index) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BigImageShowIntent.KEY_PHOTO_PATHS, paths);
        bundle.putInt(BigImageShowIntent.KEY_SELECTOR_POSITION, index);
        open(context, "BigImageFragment", "", bundle);
    }



    public static void open(Context context, String fragmentName) {
        Intent intent = generateIntent(context, fragmentName);
        context.startActivity(intent);
    }

    public static void open(Context context, String fragmentName,Bundle fragmentArgs) {
        Intent intent = generateIntent(context, fragmentName);
        intent.putExtras(fragmentArgs);
        context.startActivity(intent);
    }

    public static void open(Context context, String fragmentName, String actionBarTitle) {
        Intent intent = generateIntent(context, fragmentName);
        intent.putExtra(ACTION_BAR_TITLE, actionBarTitle);
        context.startActivity(intent);
    }

    public static void open(Context context, String fragmentName, String actionBarTitle, Bundle fragmentArgs) {
        Intent intent = generateIntent(context, fragmentName);
        intent.putExtra(ACTION_BAR_TITLE, actionBarTitle);
        intent.putExtras(fragmentArgs);
        context.startActivity(intent);
    }

    public static void openForResult(Fragment context, String fragmentName, String actionBarTitle, Bundle args, int requestId) {
        Intent intent = generateIntent(context.getContext(), fragmentName);
        intent.putExtra(ACTION_BAR_TITLE, actionBarTitle);
        intent.putExtras(args);
        context.startActivityForResult(intent, requestId);
    }

    public static void openForResult(Activity context, String fragmentName, String actionBarTitle, Bundle args, int requestId) {
        Intent intent = generateIntent(context, fragmentName);
        intent.putExtra(ACTION_BAR_TITLE, actionBarTitle);
        intent.putExtras(args);
        context.startActivityForResult(intent, requestId);
    }

    private void replaceFragment(String fragmentName) {
        Fragment fragment = initFragmentByName(fragmentName);
        if (fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment).commit();
        }
    }

    private Fragment initFragmentByName(String fragmentName) {

        Fragment fragment = generateFragment(fragmentName);
        if (fragment != null) {
            fragment.setArguments(getIntent().getExtras());
        } else {
            throw new RuntimeException("please add a fragment implementation.");
        }
        return fragment;
    }

    private static Intent generateIntent(Context context, String fragmentName) {
        Intent intent = new Intent(context, FragmentBridgeActivity.class);
        intent.putExtra(FRAGMENT_NAME, fragmentName);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_bridge);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        resetActionBar();

        if (getIntent().hasExtra(FRAGMENT_NAME)) {

            String fragmentName = getIntent().getStringExtra(FRAGMENT_NAME);
            replaceFragment(fragmentName);
        } else {

            replaceFragment("OtherFragment");
        }
    }

    private void resetActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (getIntent().hasExtra(ACTION_BAR_TITLE)) {
                actionBar.setTitle(getIntent().getStringExtra(ACTION_BAR_TITLE));
            }
        }
    }

    /**
     * white list fragment
     *
     * @param fragmentName
     * @return
     */
    private Fragment generateFragment(String fragmentName) {
        switch (fragmentName) {
            case "BigImageFragment":
                return new BigImageFragment();

//            case "SubtractGroupUsersFragment":
//                return new SubtractGroupUsersFragment();

            default:
                return null;
        }
    }


}
