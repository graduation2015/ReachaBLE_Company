package jp.ac.it_college.std.ikemen.reachable.company.coupon;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.com.google.gson.Gson;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.it_college.std.ikemen.reachable.company.CouponListAdapter;
import jp.ac.it_college.std.ikemen.reachable.company.CreateCouponActivity;
import jp.ac.it_college.std.ikemen.reachable.company.EmptySupportRecyclerView;
import jp.ac.it_college.std.ikemen.reachable.company.MainActivity;
import jp.ac.it_college.std.ikemen.reachable.company.OnActionClickListener;
import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.RecyclerItemClickListener;
import jp.ac.it_college.std.ikemen.reachable.company.UploadObservers;
import jp.ac.it_college.std.ikemen.reachable.company.aws.AwsUtil;
import jp.ac.it_college.std.ikemen.reachable.company.aws.S3UploadManager;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.json.JsonManager;

/**
 * クーポン登録画面のFragmentクラス
 */
public class CouponSelectFragment extends BaseCouponFragment
        implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener, OnActionClickListener, DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    /* Constants */
    private static final int REQUEST_GALLERY = 0;
    public static final int CREATE_COUPON = 0x002;

    /* Views */
    private View mContentView;
    private FloatingActionButton mFab;
    private EmptySupportRecyclerView mCouponListView;
    private TextView mEmptyView;

    /* Json */
    private JsonManager mJsonManager;

    /* Coupon */
    private CouponInfo mSelectedCoupon;
    private ProgressDialog mProgressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_coupon_select, container, false);

        //初期設定
        initSettings();
        return mContentView;
    }

    /**
     * 各種初期設定
     */
    private void initSettings() {
        //クーポンリストのアダプターをセット
        setCouponListAdapter(new CouponListAdapter(getCouponInfoList(PREF_SAVED_COUPON_INFO_LIST),
                R.layout.coupon_card, this));
        //クーポンリストをセットアップ
        setUpCouponListView(getCouponListView());

        //クーポンアイテムクリック時のリスナーをセット
        getCouponListView().addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), this));

        //FABのOnClickListenerをセット
        getFab().setOnClickListener(this);

        //JsonManagerのインスタンスを生成
        mJsonManager = new JsonManager(getActivity());
    }

    public View getContentView() {
        return mContentView;
    }

    public FloatingActionButton getFab() {
        if (mFab == null) {
            mFab = (FloatingActionButton) getContentView().findViewById(R.id.fab);
        }
        return mFab;
    }

    public TextView getEmptyView() {
        if (mEmptyView == null) {
            mEmptyView = (TextView) getContentView().findViewById(R.id.txt_empty_view);
        }
        return mEmptyView;
    }

    public EmptySupportRecyclerView getCouponListView() {
        if (mCouponListView == null) {
            mCouponListView = (EmptySupportRecyclerView) getContentView().findViewById(R.id.coupon_list);
            //リストが空の際に表示するViewをセット
            mCouponListView.setEmptyView(getEmptyView());
        }
        return mCouponListView;
    }

    public JsonManager getJsonManager() {
        return mJsonManager;
    }

    /**
     * クーポンをギャラリーから選択する
     */
    private void couponSelect() {
        Intent intent = new Intent();

        //APILevelが19(version 4.4)以上の場合
        if (Build.VERSION.SDK_INT >= 19) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setType("*/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        } else {
            //APILevelが18(version 4.3)以下の場合
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setType("image/*");
        }

        startActivityForResult(intent, REQUEST_GALLERY);
    }

    /**
     * クーポンリストにクーポンを追加する
     * @param info クーポン作成画面で作成されたクーポン
     */
    private void addCoupon(CouponInfo info) {
        //クーポンリストにクーポンを追加
        getCouponInfoList(PREF_SAVED_COUPON_INFO_LIST).add(info);
        //追加をアダプターに通知
        getCouponListAdapter().notifyItemInserted(
                getCouponInfoList(PREF_SAVED_COUPON_INFO_LIST).size() - 1);

        //追加したクーポンまでスクロールする
        getCouponListView().scrollToPosition(getCouponListAdapter().getItemCount() - 1);

        //クーポンリストをSharedPreferencesに保存
        saveCouponInstance(
                getCouponInfoList(PREF_SAVED_COUPON_INFO_LIST), PREF_SAVED_COUPON_INFO_LIST);
    }

    /**
     * SharedPreferencesにクーポンのインスタンスを保存
     * @param infoList 保存するクーポン情報リスト
     * @param key SharedPreferencesに保存する際のキー名
     */
    private void saveCouponInstance(List<CouponInfo> infoList, String key) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                CouponInfo.PREF_INFO, Context.MODE_PRIVATE).edit();
        Set<String> instances = new HashSet<>();

        for (CouponInfo info : infoList) {
            instances.add(gson.toJson(info));
        }

        editor.putStringSet(key, instances);
        editor.apply();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                Intent intent = new Intent(getActivity(), CreateCouponActivity.class);
                intent.setData(data.getData());
                startActivityForResult(intent, CREATE_COUPON);
            }

            if (requestCode == CREATE_COUPON) {
                //作成されたクーポンの情報を取得
                CouponInfo couponInfo = (CouponInfo) data.getSerializableExtra(
                        CreateCouponActivity.CREATED_COUPON_DATA);
                //クーポンをクーポンリストに追加
                addCoupon(couponInfo);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                //FABボタン押下時の処理
                couponSelect();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        //クーポンリストのアイテムクリック時の処理
    }

    @Override
    public void onItemLongPress(View view, int position) {
        //クーポンアイテム長押し時の処理
    }

    @Override
    public void onAdvertiseClick(View view, int position) {
        /* ADVERTISEボタン押下時の処理 */
        //選択されたクーポンを取得
        mSelectedCoupon = getCouponInfoList(PREF_SAVED_COUPON_INFO_LIST).get(position);

        //選択されたクーポンをJSONファイルに書き込む
        if (putCouponToJson(mSelectedCoupon)) {
            //書き込みが成功した場合クーポンファイルをS3にアップロードする
            File couponFile = new File(mSelectedCoupon.getFilePath());
            List<File> fileList = Arrays.asList(couponFile, getJsonManager().getFile());
            beginUpload(fileList);
        } else {
            //JSONへの書き込みが失敗した時の処理
        }
    }

    @Override
    public void onDeleteClick(View view, int position) {
        /* DELETEボタン押下時の処理 */
        //クーポンを削除
        getCouponInfoList(PREF_SAVED_COUPON_INFO_LIST).remove(position);
        //削除をアダプターに通知
        getCouponListAdapter().notifyItemRemoved(position);
        //クーポンリストを保存
        saveCouponInstance(getCouponInfoList(PREF_SAVED_COUPON_INFO_LIST), PREF_SAVED_COUPON_INFO_LIST);
    }

    /**
     * クーポンの情報をjsonに書き込む
     * @param info 書き込むクーポン
     * @return 書き込みが成功した場合はtrueを返す
     */
    private boolean putCouponToJson(CouponInfo info) {
        try {
            getJsonManager().putJsonObj(info);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }

        return  true;
    }

    /**
     * ProgressDialogを生成して返す
     * @return progressDialog
     */
    public ProgressDialog getProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle(getString(R.string.dialog_title_coupon_upload));
            mProgressDialog.setMessage(getString(R.string.dialog_message_coupon_upload));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgress(0);
            mProgressDialog.setOnCancelListener(this);
            mProgressDialog.setOnDismissListener(this);
        }
        return mProgressDialog;
    }

    /**
     * ProgressDialogが中止されたタイミングで呼ばれる
     * @param dialog
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        Toast.makeText(
                getActivity(), getString(R.string.coupon_upload_failed), Toast.LENGTH_SHORT).show();
    }

    /**
     * ProgressDialogが破棄されたタイミングで呼ばれる
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        //クーポンアップロード完了後の処理
        if (getProgressDialog().getProgress() >= getProgressDialog().getMax()) {
            //アップロード完了を通知するToastを表示
            Toast.makeText(
                    getActivity(), getString(R.string.coupon_upload_completed), Toast.LENGTH_SHORT).show();

            //ProgressDialogを破棄
            mProgressDialog = null;

            //選択されたクーポンを保存
            List<CouponInfo> selectedList = Arrays.asList(mSelectedCoupon);
            saveCouponInstance(selectedList, PREF_SELECTED_COUPON);

            //クーポン宣伝画面に切り替える
            changeFragment(new AdvertiseCouponFragment());
        }

    }

    /**
     * Fragmentをアニメーションしながら遷移させる
     * @param destination 遷移先のFragment
     */
    private void changeFragment(Fragment destination) {
        //TransitionSetを設定
        TransitionSet transitionSet = new TransitionSet();
        //Slideアニメーションを使用
        Slide slide = new Slide();
        //画面右からスライドするように設定
        slide.setSlideEdge(Gravity.END);
        //TransitionSetにTransitionを追加
        transitionSet.addTransition(slide);

        //FragmentにTransitionをセット
        destination.setEnterTransition(transitionSet);

        //Fragment変更時にNavigationViewのチェックアイテムが変わらないので明示的に変更する
        ((MainActivity) getActivity()).getNavigationView().setCheckedItem(R.id.menu_advertise_coupon);

        //Fragmentを切り替える
        ((MainActivity) getActivity()).changeFragment(destination);
    }

    /**
     * 選択されたクーポンをS3にアップロードする
     * @param files アップロードするファイルのリスト
     */
    private void beginUpload(List<File> files) {
        //アップロードを実行しObserverListを取得
        List<TransferObserver> observerList = new S3UploadManager(getActivity(),
                AwsUtil.getTransferUtility(getActivity()), files).execute(getProgressDialog());
        //UploadObserversを生成
        UploadObservers uploadObservers = new UploadObservers(observerList);

        //ProgressDialogの最大値にアップロードするファイルの合計サイズをセット
        getProgressDialog().setMax((int) uploadObservers.getBytesTotal());
        //ProgressDialogを表示
        getProgressDialog().show();
    }

}
