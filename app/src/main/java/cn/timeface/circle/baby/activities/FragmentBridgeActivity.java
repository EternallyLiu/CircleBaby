package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.wechat.photopicker.fragment.BigImageFragment;
import com.wechat.photopicker.utils.IntentUtils.BigImageShowIntent;

import java.util.ArrayList;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.fragments.AddAddressFragment;
import cn.timeface.circle.baby.fragments.AddBookFragment;
import cn.timeface.circle.baby.fragments.AddBookListFragment;
import cn.timeface.circle.baby.fragments.BookSizeListFragment;
import cn.timeface.circle.baby.fragments.CardPreviewFragment;
import cn.timeface.circle.baby.fragments.ChangeInfoFragment;
import cn.timeface.circle.baby.fragments.DiaryPreviewFragment;
import cn.timeface.circle.baby.fragments.DiaryTextFragment;
import cn.timeface.circle.baby.fragments.FamilyMemberFragment;
import cn.timeface.circle.baby.fragments.FamilyMemberInfoFragment;
import cn.timeface.circle.baby.fragments.InviteFragment;
import cn.timeface.circle.baby.fragments.MessageFragment;
import cn.timeface.circle.baby.fragments.SelectAddressFragment;
import cn.timeface.circle.baby.fragments.SettingFragment;
import cn.timeface.circle.baby.fragments.SettingMsgFragment;
import cn.timeface.circle.baby.fragments.SystemMessageFragment;
import cn.timeface.circle.baby.fragments.WebViewFragment;
import cn.timeface.circle.baby.support.api.models.objs.BookTypeListObj;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.ui.babyInfo.fragments.CreateBabyFragment;
import cn.timeface.circle.baby.ui.babyInfo.fragments.IconHistoryFragment;
import cn.timeface.circle.baby.ui.images.TagAddFragment;
import cn.timeface.circle.baby.ui.kiths.KithFragment;
import cn.timeface.circle.baby.ui.settings.fragments.BindPhoneFragment;
import cn.timeface.circle.baby.ui.settings.fragments.NotifyPwdFragment;
import cn.timeface.circle.baby.ui.timelines.fragments.TimeFaceDetailFragment;
import cn.timeface.circle.baby.ui.timelines.fragments.TimeLineDayFragment;
import cn.timeface.circle.baby.ui.timelines.fragments.TimeLineFragment;

/**
 * Created by JieGuo on 1/22/16.
 */
public class FragmentBridgeActivity extends BaseAppCompatActivity {

    public static final String FRAGMENT_NAME = "fragment_name";
    public static final String ACTION_BAR_TITLE = "action_bar_title";

    public static void openBigimageFragment(Context context, ArrayList<String> paths, int index, boolean download, boolean delete) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BigImageShowIntent.KEY_PHOTO_PATHS, paths);
        bundle.putInt(BigImageShowIntent.KEY_SELECTOR_POSITION, index);
        bundle.putBoolean("download", download);
        bundle.putBoolean("delete", delete);
        open(context, "BigImageFragment", "", bundle);
    }

    public static void openBigimageFragment(Context context,ArrayList<MediaObj> list, ArrayList<String> paths, int index, boolean download, boolean delete) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("mediaList",list);
        bundle.putStringArrayList(BigImageShowIntent.KEY_PHOTO_PATHS, paths);
        bundle.putInt(BigImageShowIntent.KEY_SELECTOR_POSITION, index);
        bundle.putBoolean("download", download);
        bundle.putBoolean("delete", delete);
        open(context, "BigImageFragment", "", bundle);
    }
    public static void openBigimageFragment(Context context, int allDetailsListPosition,ArrayList<MediaObj> list, ArrayList<String> paths, int index, boolean download, boolean delete) {
        Bundle bundle = new Bundle();
        bundle.putInt("allDetailsListPosition",allDetailsListPosition);
        bundle.putParcelableArrayList("mediaList",list);
        bundle.putStringArrayList(BigImageShowIntent.KEY_PHOTO_PATHS, paths);
        bundle.putInt(BigImageShowIntent.KEY_SELECTOR_POSITION, index);
        bundle.putBoolean("download", download);
        bundle.putBoolean("delete", delete);
        open(context, "BigImageFragment", "", bundle);
    }

    public static void openChangeInfoFragment(Fragment context, int requestCode, String info) {
        Bundle bundle = new Bundle();
        bundle.putString("info", info);
        String title = "名字";
        switch (requestCode) {
            case TypeConstants.EDIT_NAME:
                title = "名字";
                break;
            case TypeConstants.EDIT_BLOOD:
                title = "血型";
                break;
            case TypeConstants.EDIT_RELATIONNAME:
                title = "与宝宝的关系";
                break;
            case TypeConstants.EDIT_NICKNAME:
                title = "昵称";
                break;
        }
        openForResult(context, "ChangeInfoFragment", title, bundle, requestCode);
    }

    public static void openChangeInfoFragment(Activity context, int requestCode, String info) {
        Bundle bundle = new Bundle();
        bundle.putString("info", info);
        String title = "名字";
        switch (requestCode) {
            case TypeConstants.EDIT_NAME:
                title = "名字";
                break;
            case TypeConstants.EDIT_BLOOD:
                title = "血型";
                break;
            case TypeConstants.EDIT_RELATIONNAME:
                title = "与宝宝的关系";
                break;
            case TypeConstants.EDIT_NICKNAME:
                title = "昵称";
                break;
        }
        openForResult(context, "ChangeInfoFragment", title, bundle, requestCode);
    }

    public static void openInviteFragment(Context context, String relationName) {
        Bundle bundle = new Bundle();
        bundle.putString("relationName", relationName);
        open(context, "InviteFragment", bundle);
    }

    public static void openFamilyMemberInfoFragment(Context context, UserObj userObj) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userObj", userObj);
        open(context, "FamilyMemberInfoFragment", bundle);
    }

    public static void openDiaryPreviewFragment(Context context, String time, String content, ImgObj imgObj) {
        Bundle bundle = new Bundle();
        bundle.putString("title", time);
        bundle.putString("content", content);
        bundle.putParcelable("imgObj", imgObj);
        open(context, "DiaryPreviewFragment", bundle);
    }

    public static void openCardPreviewFragment(Context context, ImgObj imgObj) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("imgObj", imgObj);
        open(context, "CardPreviewFragment", bundle);
    }

    public static void openAddBookFragment(Context context, BookTypeListObj obj) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("BookTypeListObj", obj);
        open(context, "AddBookFragment", bundle);
    }

    public static void openBookSizeListFragment(Context context) {
        Bundle bundle = new Bundle();
        open(context, "BookSizeListFragment", bundle);
    }

    public static void openWebViewFragment(Context context, String url, String title) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        open(context, "WebViewFragment", title, bundle);
    }


    public static void open(Context context, String fragmentName) {
        Intent intent = generateIntent(context, fragmentName);
        context.startActivity(intent);
    }

    public static void open(Context context, String fragmentName, Bundle fragmentArgs) {
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

    public static void openForResult(Activity context, String fragmentName, int requestId, String content) {
        Intent intent = generateIntent(context, fragmentName);
        intent.putExtra("diary_content", content);
        context.startActivityForResult(intent, requestId);
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

//        setSupportActionBar(toolbar);

//        resetActionBar();

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

            case "FamilyMemberFragment":
                return new FamilyMemberFragment();

            case "MessageFragment":
                return new MessageFragment();

            case "SystemMessageFragment":
                return new SystemMessageFragment();

            case "ChangeInfoFragment":
                return new ChangeInfoFragment();

            case "InviteFragment":
                return new InviteFragment();

            case "FamilyMemberInfoFragment":
                return new FamilyMemberInfoFragment();

            case "DiaryTextFragment":
                return new DiaryTextFragment();

            case "DiaryPreviewFragment":
                return new DiaryPreviewFragment();

            case "CardPreviewFragment":
                return new CardPreviewFragment();

            case "SettingFragment":
                return new SettingFragment();

            case "SettingMsgFragment":
                return new SettingMsgFragment();

            case "AddBookListFragment":
                return new AddBookListFragment();

            case "AddBookFragment":
                return new AddBookFragment();

            case "SelectAddressFragment":
                return new SelectAddressFragment();

            case "AddAddressFragment":
                return new AddAddressFragment();

            case "BookSizeListFragment":
                return new BookSizeListFragment();

            case "WebViewFragment":
                return WebViewFragment.newInstance();
            case "TimeLineFragment":
                return TimeLineFragment.newInstance();
            case "TimeLineDayFragment":

                return TimeLineDayFragment.newInstance();
            case "IconHistoryFragment":

                return new IconHistoryFragment();
            case "NotifyPwdFragment":
                return new NotifyPwdFragment();
            case "BindPhoneFragment":

                return new BindPhoneFragment();
            case "TagAddFragment":

                return new TagAddFragment();
            case "KithFragment":

                return new KithFragment();
            case "CreateBabyFragment":

                return new CreateBabyFragment();
            case "TimeFaceDetailFragment":

                return new TimeFaceDetailFragment();
            default:
                return null;
        }
    }


}
