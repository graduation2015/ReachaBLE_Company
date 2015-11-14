package jp.ac.it_college.std.reachable_company;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MultipleCategoryChoiceDialog extends ChoiceDialog {

    private ArrayList<String> checkedCategories = new ArrayList<>();
    public static final String TAG = "MultipleChoiceDialog";

    @Override
    protected Bundle makeArgs(Context context) {
        Bundle args = new Bundle();
        String[] items = context.getResources().getStringArray(R.array.categories);
        boolean[] checkedItems = new boolean[items.length];
        args.putStringArray(MultipleCategoryChoiceDialog.ITEMS, items);
        args.putBooleanArray(MultipleCategoryChoiceDialog.CHECKED_ITEMS, checkedItems);

        return args;
    }

    @Override
    protected Dialog makeDialog() {
        String[] items = getArguments().getStringArray(ITEMS);
        boolean[] checkedItems = getArguments().getBooleanArray(CHECKED_ITEMS);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Select category")
                .setMultiChoiceItems(items, checkedItems, makeMultiChoiceClickListener(items, checkedItems))
                .setPositiveButton("OK", makeConfirmClickListener())
                .setNegativeButton("Cancel", makeCancelClickListener())
                .create();

    }

    /**
     * OK押下時の処理
     * @return
     */
    private DialogInterface.OnClickListener makeConfirmClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //チェックしたカテゴリのリストを親フラグメントに渡す
                Intent intent = new Intent();
                intent.putStringArrayListExtra(CHECKED_ITEMS, checkedCategories);
                getTargetFragment().onActivityResult(getTargetRequestCode(), i, intent);
            }
        };
    }

    /**
     * キャンセル押下時の処理
     * @return
     */
    protected DialogInterface.OnClickListener makeCancelClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "makeCancelClickListener");
            }
        };
    }

    /**
     * チェックボックス押下時の処理
     * @param items
     * @param checkedItems
     * @return
     */
    private OnMultiChoiceClickListener makeMultiChoiceClickListener(
            final String[] items, final boolean[] checkedItems) {
        return new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                checkedItems[position] = isChecked;
                if (isChecked) {
                    checkedCategories.add(items[position]);
                } else {
                    checkedCategories.remove(items[position]);
                }
            }
        };
    }

}
