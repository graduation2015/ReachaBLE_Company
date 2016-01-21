package jp.ac.it_college.std.ikemen.reachable.company.coupon;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import jp.ac.it_college.std.ikemen.reachable.company.MainActivity;
import jp.ac.it_college.std.ikemen.reachable.company.R;
import jp.ac.it_college.std.ikemen.reachable.company.coupon.adapter.CouponListAdapter;
import jp.ac.it_college.std.ikemen.reachable.company.info.CouponInfo;
import jp.ac.it_college.std.ikemen.reachable.company.util.Utils;
import jp.ac.it_college.std.ikemen.reachable.company.view.EmptySupportRecyclerView;
import jp.ac.it_college.std.ikemen.reachable.company.view.listener.RecyclerItemClickListener;

/**
 * クーポン登録画面のFragmentクラス
 */
public class CouponSelectFragment extends BaseCouponFragment
        implements View.OnClickListener, RecyclerItemClickListener.OnItemClickListener,
        MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener,
        MenuItem.OnMenuItemClickListener {

    /* Constants */
    private static final int REQUEST_GALLERY = 0x101;
    public static final int CREATE_COUPON = 0x102;
    public static final int REQUEST_DETAIL = 0x103;
    public static final int SPAN_COUNT = 2;

    /* Views */
    private View mContentView;
    private FloatingActionButton mFab;
    private EmptySupportRecyclerView mCouponListView;
    private TextView mEmptyView;
    private SearchView mSearchView;
    private CoordinatorLayout mCoordinatorLayout;

    /* Actionbar */
    private ActionMode mActionMode;

    /* Coupon */
    private LinkedHashMap<Integer, CouponInfo> mDeletedCouponMap;
    private View.OnClickListener mUndoCouponListener;


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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //メニューをインフレート
        inflater.inflate(R.menu.coupon_select_menu, menu);
        //サーチメニューを取得
        final MenuItem searchMenu = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchMenu.getActionView();

        //SearchView開閉時のリスナーをセットする
        MenuItemCompat.setOnActionExpandListener(searchMenu, this);
        //SearchViewにキーワードが入力された時のリスナーをセットする
        mSearchView.setOnQueryTextListener(this);

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //SearchViewからフォーカスが外れた際にメニューを閉じる
                    searchMenu.collapseActionView();
                }
            }
        });

        //表示形式変更メニューのクリックリスナーを登録
        menu.findItem(R.id.menu_change_view).setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_change_view:
                //RecyclerViewの表示形式を切り替える
                if (getCouponListView().getLayoutManager() instanceof LinearLayoutManager) {
                    //グリット表示に切り替える
                    getCouponListView().setLayoutManager(new StaggeredGridLayoutManager(
                            SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL));
                } else {
                    //リスト表示に切り替える
                    getCouponListView().setLayoutManager(new LinearLayoutManager(getActivity()));
                }
                break;
        }
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //表示形式変更メニューを取得
        MenuItem changeViewMenu = menu.findItem(R.id.menu_change_view);

        //RecyclerViewに設定されているLayoutManagerによって表示形式変更メニューのタイトルを切り替える
        if (getCouponListView().getLayoutManager() instanceof LinearLayoutManager) {
            changeViewMenu.setTitle(R.string.menu_title_grid_view);
        } else {
            changeViewMenu.setTitle(R.string.menu_title_list_view);
        }
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

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public CoordinatorLayout getCoordinatorLayout() {
        if (mCoordinatorLayout == null) {
            mCoordinatorLayout =
                    (CoordinatorLayout) getContentView().findViewById(R.id.coordinator_layout);
        }
        return mCoordinatorLayout;
    }

    public HashMap<Integer, CouponInfo> getDeletedCouponMap() {
        if (mDeletedCouponMap == null) {
            mDeletedCouponMap = new LinkedHashMap<>();
        }
        return mDeletedCouponMap;
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
     * @param location 追加する場所
     * @param info クーポン作成画面で作成されたクーポン
     */
    private void addCoupon(int location, CouponInfo info) {
        //クーポンリストにクーポンを追加
        getCouponInfoList().add(info);
        getCouponListAdapter().add(location, info);
        //追加したクーポンまでスクロールする
        getCouponListView().getLayoutManager()
                .smoothScrollToPosition(getCouponListView(), null, location);
        //クーポンリストをSharedPreferencesに保存
        Utils.saveCouponInstance(getActivity(), getCouponInfoList(), PREF_SAVED_COUPON_INFO_LIST);
    }

    /**
     * クーポンリストからクーポンを削除する
     * @param position 削除するクーポンのインデックス
     * @return 削除したクーポンを返す
     */
    private CouponInfo deleteCoupon(int position) {
        //アダプターのクーポンリストからクーポンを削除
        CouponInfo target = getCouponListAdapter().remove(position);
        //オリジナルのクーポンリストからクーポンを削除
        getCouponInfoList().remove(target);
        //クーポンリストをSharedPreferencesに保存
        Utils.saveCouponInstance(getActivity(), getCouponInfoList(), PREF_SAVED_COUPON_INFO_LIST);

        return target;
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
            getDeletedCouponMap().put(position, deleteCoupon(position));
        }

        //削除アイテムを戻す際のSnackBarを表示する
        showUndoSnackBar(selectedItem.size());
    }

    /**
     * 削除されたクーポンを元に戻す
     */
    private void undoCoupon() {
        //UNDOボタン押下時の処理
        List<Integer> reverseList = new ArrayList<>(getDeletedCouponMap().keySet());
        Collections.reverse(reverseList);
        for (Integer location : reverseList) {
            addCoupon(location, getDeletedCouponMap().get(location));
        }
    }

    private View.OnClickListener getUndoCouponListener() {
        if (mUndoCouponListener == null) {
            mUndoCouponListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    undoCoupon();
                }
            };
        }

        return mUndoCouponListener;
    }

    private void showUndoSnackBar(int deleteCount) {
        Snackbar.make(getCoordinatorLayout(), getString(R.string.deleted_coupon, deleteCount),
                Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, getUndoCouponListener())
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        //SnackBar非表示の際に削除済みクーポンのマップをクリアする
                        getDeletedCouponMap().clear();
                    }
                })
                .show();
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
            //フォトギャラリーからのレスポンス
            if (requestCode == REQUEST_GALLERY) {
                Intent intent = new Intent(getActivity(), CreateCouponActivity.class);
                intent.setData(data.getData());
                startActivityForResult(intent, CREATE_COUPON);
            } else if (requestCode == CREATE_COUPON) { //クーポン作成画面からのレスポンス
                //作成されたクーポンの情報を取得
                CouponInfo couponInfo = (CouponInfo) data.getSerializableExtra(
                        CreateCouponActivity.CREATED_COUPON_DATA);
                //クーポンをクーポンリストに追加
                addCoupon(0, couponInfo);
            }
        }

        //クーポン詳細画面からのレスポンス
        if (requestCode == REQUEST_DETAIL) {
            if (resultCode == CouponDetailActivity.RESULT_DELETE && data != null) {
                //クーポン詳細画面で削除ボタンが押された際の処理
                int targetPosition = data.getIntExtra(CouponDetailActivity.SELECTED_ITEM_POSITION, -1);
                //クーポンを削除して削除済みマップに追加する
                getDeletedCouponMap().put(targetPosition, deleteCoupon(targetPosition));
                //削除したクーポンを戻す際のSnackBarを表示する
                showUndoSnackBar(1);
            } else if (resultCode == CouponDetailActivity.RESULT_UPLOADED) {
                //クーポン宣伝画面へ遷移する
                transitionToAdvertise();
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
            //コンテキストメニュー表示時の処理
            toggleSelection(position);
        } else {
            //リストアイテムクリック時の処理
            transitionToCouponDetails(view, position);
        }
    }

    /**
     * クーポン詳細画面と共有するViewをSharedElementに設定する
     * @param view クリックされたクーポンのView
     * @return SharedElementの情報が入ったActivityOptionsCompatを返す
     */
    private ActivityOptionsCompat makeSharedElementOptions(View view) {
        View title = view.findViewById(R.id.txt_title);
        View image = view.findViewById(R.id.img_coupon_pic);
        View creationDate = view.findViewById(R.id.txt_creation_date);
        View category = view.findViewById(R.id.txt_tags);
        View toolbar = ((MainActivity) getActivity()).getToolbar();
        View fab = getContentView().findViewById(R.id.fab);

        return ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                new Pair<View, String>(image, getString(R.string.transition_image)),
                new Pair<View, String>(toolbar, getString(R.string.transition_toolbar)),
                new Pair<View, String>(creationDate, getString(R.string.transition_creation_date)),
                new Pair<View, String>(category, getString(R.string.transition_category)),
                new Pair<View, String>(fab, getString(R.string.transition_fab)),
                new Pair<View, String>(title, getString(R.string.transition_title))
        );
    }

    /**
     * クーポン詳細画面に遷移する
     * @param view 選択されたクーポンのView
     * @param position 選択されたクーポンのリストポジション
     */
    private void transitionToCouponDetails(View view, int position) {
        Intent intent = new Intent(getActivity(), CouponDetailActivity.class);
        intent.putExtra(CouponDetailActivity.SELECTED_ITEM,
                getCouponListAdapter().getCouponInfoList().get(position));
        intent.putExtra(CouponDetailActivity.SELECTED_ITEM_POSITION, position);

        startActivityForResult(intent, REQUEST_DETAIL, makeSharedElementOptions(view).toBundle());
    }

    /**
     * クーポン宣伝画面に遷移する
     */
    private void transitionToAdvertise() {
        //Slideアニメーションを使用
        Slide slide = new Slide();
        //画面右からスライドするように設定
        slide.setSlideEdge(Gravity.END);
        //宣伝画面用フラグメントのインスタンスを生成
        Fragment fragment = new AdvertiseCouponFragment();
        //FragmentにTransitionをセット
        fragment.setEnterTransition(slide);
        //Fragment変更時にNavigationViewのチェックアイテムが変わらないので明示的に変更する
        ((MainActivity) getActivity()).getNavigationView().setCheckedItem(R.id.menu_advertise_coupon);
        //Fragmentを切り替える
        ((MainActivity) getActivity()).changeFragment(fragment, R.string.menu_title_advertise_coupon);
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
