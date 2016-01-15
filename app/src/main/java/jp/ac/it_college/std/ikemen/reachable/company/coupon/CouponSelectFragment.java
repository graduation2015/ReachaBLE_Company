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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.ac.it_college.std.ikemen.reachable.company.MainActivity;
import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.aws.AwsUtil;
import jp.ac.it_college.std.ikemen.reachable.company.aws.S3UploadManager;
import jp.ac.it_college.std.ikemen.reachable.company.aws.UploadObservers;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.adapter.CouponListAdapter;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.listener.OnActionClickListener;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.json.JsonManager;
import jp.ac.it_college.std.ikemen.reachable.company.util.Utils;
import jp.ac.it_college.std.ikemen.reachable.company.view.EmptySupportRecyclerView;
import jp.ac.it_college.std.ikemen.reachable.company.view.listener.RecyclerItemClickListener;

/**
 * クーポン登録画面のFragmentクラス
 */
public class CouponSelectFragment extends BaseCouponFragment
        implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener,
        OnActionClickListener, DialogInterface.OnCancelListener, DialogInterface.OnDismissListener,
        MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener {

    /* Constants */
    private static final int REQUEST_GALLERY = 0;
    public static final int CREATE_COUPON = 0x002;
    private static final int ITEM_DELETED = -1;

    /* Views */
    private View mContentView;
    private FloatingActionButton mFab;
    private EmptySupportRecyclerView mCouponListView;
    private TextView mEmptyView;
    private SearchView mSearchView;

    /* Json */
    private JsonManager mJsonManager;

    /* Coupon */
    private CouponInfo mSelectedCoupon;
    private ProgressDialog mProgressDialog;
    private ActionMode mActionMode;


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
        //ツールバーにメニューを表示する
        setHasOptionsMenu(true);
        //クーポンリストのアダプターをセット
        setCouponListAdapter(new CouponListAdapter(getActivity(), getCouponInfoList()));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        final MenuItem item = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) item.getActionView();

        //SearchView開閉時のリスナーをセットする
        MenuItemCompat.setOnActionExpandListener(item, this);
        //SearchViewにキーワードが入力された時のリスナーをセットする
        mSearchView.setOnQueryTextListener(this);

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //SearchViewからフォーカスが外れた際にメニューを閉じる
                    item.collapseActionView();
                }
            }
        });


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

    public ActionMode getActionMode() {
        return mActionMode;
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
     *
     * @param info クーポン作成画面で作成されたクーポン
     */
    private void addCoupon(CouponInfo info) {
        //クーポンリストにクーポンを追加
        getCouponInfoList().add(0, info);
        getCouponListAdapter().add(info);
        //追加したクーポンまでスクロールする
        getCouponListView().getLayoutManager()
                .smoothScrollToPosition(getCouponListView(), null, 0);
        //クーポンリストをSharedPreferencesに保存
        saveCouponInstance(getCouponInfoList(), PREF_SAVED_COUPON_INFO_LIST);
    }

    /**
     * クーポンリストからクーポンを削除する
     *
     * @param position 削除するクーポンのインデックス
     */
    private void deleteCoupon(int position) {
        //アダプターのクーポンリストからクーポンを削除
        CouponInfo target = getCouponListAdapter().remove(position);
        //オリジナルのクーポンリストからクーポンを削除
        getCouponInfoList().remove(target);
        //クーポンリストをSharedPreferencesに保存
        saveCouponInstance(getCouponInfoList(), PREF_SAVED_COUPON_INFO_LIST);
    }

    /**
     * 選択されたリストアイテムを削除する
     * @param selectedItem アダプターの選択済みリスト
     */
    private void deleteSelectedItem(List<Integer> selectedItem) {
        //選択リストを逆順にソート
        Collections.reverse(selectedItem);
        //最後尾から削除していく
        for (Integer position : selectedItem) {
            deleteCoupon(position);
        }

        //アダプターにリストの変更を通知
        getCouponListAdapter().notifyDataSetChanged();
    }

    /**
     * SharedPreferencesにクーポンのインスタンスを保存
     *
     * @param infoList 保存するクーポン情報リスト
     * @param key      SharedPreferencesに保存する際のキー名
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

    /**
     * リストアイテムがタップされた際のトグル処理を実装
     * @param position タップされたアイテムの位置
     */
    private void toggleSelection(int position) {
        //アダプターにチェックアイテムの変更を通知
        getCouponListAdapter().toggleSelection(position);
        //チェックされているアイテムの数を取得
        int count = getCouponListAdapter().getSelectedItemCount();

        if (count == 0) {
            //チェックアイテムが0になった場合ActionModeを終了する
            getActionMode().finish();
        } else {
            //チェックアイテムの数をActionModeのタイトルにセットする
            getActionMode().setTitle(String.valueOf(count));
        }
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

    /*
     * クーポンアイテムがクリックされた時に呼ばれる
     */
    @Override
    public void onItemClick(View view, int position) {
        if (getActionMode() != null) {
            toggleSelection(position);
        }
    }

    /*
     * クーポンアイテムが長押しされた時に呼ばれる
     */
    @Override
    public void onItemLongPress(View view, int position) {
        if (getActionMode() == null) {
            mActionMode = ((AppCompatActivity) getActivity())
                    .startSupportActionMode(new ActionModeCallback());
        }

        toggleSelection(position);
    }

    /**
     * ADVERTISEボタン押下時に呼ばれる
     *
     * @param view     クリックされたボタンのView
     * @param position 選択されたクーポンのインデックス
     */
    @Override
    public void onAdvertiseClick(View view, int position) {
        if (position == ITEM_DELETED) {
            //選択されたクーポンが既にリストにない場合は処理を実行せずにメソッドを抜ける
            return;
        }
        //選択されたクーポンを取得
        mSelectedCoupon = getCouponListAdapter().getCouponInfoList().get(position);

        //選択されたクーポンをJSONファイルに書き込む
        if (putCouponToJson(mSelectedCoupon)) {
            //書き込みが成功した場合クーポンファイルをS3にアップロードする
            File couponFile = new File(mSelectedCoupon.getFilePath());
            List<File> fileList = Arrays.asList(couponFile, getJsonManager().getFile());
            beginUpload(fileList);
        } else {
            //JSONへの書き込みが失敗した時の処理
            Toast.makeText(getActivity(), R.string.failed_to_write_coupon, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * DELETEボタン押下時に呼ばれる
     *
     * @param view     クリックされたボタンのView
     * @param position 選択されたクーポンのインデックス
     */
    @Override
    public void onDeleteClick(View view, int position) {
        /* DELETEボタン押下時の処理 */
        if (position == ITEM_DELETED) {
            //選択されたクーポンが既にリストにない場合は処理を実行せずにメソッドを抜ける
            return;
        }

        //クーポンを削除
        deleteCoupon(position);
    }

    /**
     * クーポンの情報をjsonに書き込む
     *
     * @param info 書き込むクーポン
     * @return 書き込みが成功した場合はtrueを返す
     */
    private boolean putCouponToJson(CouponInfo info) {
        try {
            getJsonManager().putJsonObj(info);
        } catch (IOException | JSONException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * ProgressDialogを生成して返す
     *
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
     *
     * @param dialog
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        Toast.makeText(
                getActivity(), getString(R.string.coupon_upload_failed), Toast.LENGTH_SHORT).show();
    }

    /**
     * ProgressDialogが破棄されたタイミングで呼ばれる
     *
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

            // クーポンアップロード後に宣伝用のクーポンを保存する
            saveAdvertiseCoupon(mSelectedCoupon);
        }
    }

    /**
     * クーポンアップロード後に宣伝用のクーポンを保存する
     *
     * @param selectedCoupon 選択されたクーポン
     */
    private void saveAdvertiseCoupon(CouponInfo selectedCoupon) {
        //選択されたクーポンを保存
        List<CouponInfo> selectedList = Arrays.asList(selectedCoupon);
        saveCouponInstance(selectedList, PREF_ADVERTISE_COUPON_LIST);

        //クーポン宣伝画面に切り替える
        changeFragment(new AdvertiseCouponFragment());
    }

    /**
     * Fragmentをアニメーションしながら遷移させる
     *
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
        ((MainActivity) getActivity()).changeFragment(destination, R.string.menu_title_advertise_coupon);
    }

    /**
     * 選択されたクーポンをS3にアップロードする
     *
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

    /**
     * ツールバーのメニューアイテムが開かれた際に呼ばれる
     *
     * @param item OnActionExpandListenerにセットされたメニューアイテム
     * @return メニューアイテムが開かれた際の処理を実行する場合はtrueを返す
     */
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                //FABを非表示にする
                getFab().hide();
                break;
        }

        return true;
    }

    /**
     * ツールバーのメニューアイテムが閉じられた際に呼ばれる
     *
     * @param item OnActionExpandListenerにセットされたメニューアイテム
     * @return メニューアイテムが閉じられた際の処理を実行する場合はtrueを返す
     */
    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                //クーポンリストのアイテムを元に戻す
                getCouponListAdapter().replaceList(getCouponInfoList());
                //FABを表示する
                getFab().show();

                if (isAdded()) {
                    //EmptyViewのテキストを戻す
                    getEmptyView().setText(getString(R.string.no_coupon_data));
                }
                break;
        }

        return true;
    }

    /**
     * SearchViewでエンターボタンが押された際に呼ばれる
     *
     * @param query SearchViewに入力されたテキスト
     * @return 処理を実行する場合はtrue
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        //クーポンリストのアイテムを元に戻す
        getCouponListAdapter().replaceList(getCouponInfoList());
        //フィルターの実行
        getCouponListAdapter().getFilter().filter(query);
        //RecyclerViewを一番上までスクロールする
        getCouponListView().scrollToPosition(0);
        //ソフトウェアキーボードを非表示にする
        Utils.hideKeyboard(getActivity());
        //EmptyViewにクーポンが見つからなかった際のメッセージをセット
        getEmptyView().setText(getString(R.string.coupon_not_found, query));
        return true;
    }

    /**
     * SearchViewにテキストが入力される度に呼ばれる
     *
     * @param newText SearchViewに入力されたテキスト
     * @return 処理を実行する場合はtrue
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    private class ActionModeCallback implements ActionMode.Callback {

        private int mStatusBarColor;

       /*
        * ActionModeが初めて呼び出された時に発生　trueを返さないとその後何もしない
        */
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //FABを非表示にする
            getFab().hide();
            //コンテキストメニューを生成
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);

            //ステータスバーの背景色を取得
            mStatusBarColor = getActivity().getWindow().getStatusBarColor();
            //ステータスバーの背景色を変更
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(
                    getActivity(), R.color.action_mode_status_bar_background));

            return true;
        }

        /*
         * ActionModeが呼び出される度に発生　Menuだのなんだのを動的に変更する場合はここに記述する
         * trueを返すと変更が反映される
         */
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        /*
         * onCreateActionMode / onPrepareActionModeで登録されたMenuItemがクリックされると発生
         * 正常に処理できたらtrueを返す
         */
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete: //delete(ゴミ箱)ボタン押下時の処理
                    //選択されたアイテムを削除する
                    deleteSelectedItem(getCouponListAdapter().getSelectedItems());
                    //ActionModeを閉じる
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        /*
         * ActionMode#finish()が呼び出されたりするなどしてActionModeが終了する時に発生
         */
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //ステータスバーの背景色を元に戻す
            getActivity().getWindow().setStatusBarColor(mStatusBarColor);
            //選択リストをクリアする
            getCouponListAdapter().clearSelection();
            //ActionModeを破棄
            mActionMode = null;
            //FABを表示する
            getFab().show();
        }
    }
}
