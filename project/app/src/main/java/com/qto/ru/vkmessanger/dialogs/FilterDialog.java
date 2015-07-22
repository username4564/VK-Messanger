package com.qto.ru.vkmessanger.dialogs;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.fragments.AllFriendsFragment;

/**
 * Используется для отображения диалога
 * предоставляющего фильтры поиска
 */
public class FilterDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    /** Константа хранящая номер для общего пола */
    public static final int SEX_ALL = 0;
    /** Константа хранящая номер для женского пола */
    public static final int SEX_GIRL = 1;
    /** Константа хранящая номер для мужского пола */
    public static final int SEX_MAN = 2;
    /** Константа хранящая минимально возможный возраст - 1 */
    public static final int MINIMAL_AGE = 13;

    /** Переключатель для общего пола */
    private RadioButton mAll;
    /** Переключатель для мужского пола */
    private RadioButton mMan;
    /** Переключатель для женского пола */
    private RadioButton mGirl;
    /** Выборка возраста от */
    private Spinner mFrom;
    /** Выборка возраста до */
    private Spinner mTo;


    public FilterDialog() {}


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.fragment_filter, null);

        mFrom = (Spinner)view.findViewById(R.id.from);
        mTo = (Spinner)view.findViewById(R.id.to);
        mAll = (RadioButton)view.findViewById(R.id.all);
        mMan = (RadioButton)view.findViewById(R.id.man);
        mGirl = (RadioButton)view.findViewById(R.id.girl);

        String[] yearFrom = new String[67];
        String[] yearTo = new String[67];
        for (int i = 1; i < yearFrom.length; i++){
            yearFrom[i] = yearTo[i] = String.valueOf(13 + i);
        }
        yearFrom[0] = getString(R.string.text_age_from);
        yearTo[0] = getString(R.string.text_age_to);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, yearFrom);
        mFrom.setAdapter(adapter);
        mFrom.setSelection(0);

        adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, yearTo);
        mTo.setAdapter(adapter);
        mTo.setSelection(0);
        mTo.setOnItemSelectedListener(this);

        if (getArguments() != null){
            int sex = getArguments().getInt("sex");
            int ageFrom = getArguments().getInt("age_from");
            int ageTo = getArguments().getInt("age_to");
            switch (sex){
                case SEX_ALL:
                    mAll.setChecked(true);
                    break;
                case SEX_MAN:
                    mMan.setChecked(true);
                    break;
                case SEX_GIRL:
                    mGirl.setChecked(true);
            }
            mFrom.setSelection(ageFrom);
            mTo.setSelection(ageTo);
        }


        builder.setView(view);

        Dialog dialog = builder.create();
        return dialog;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < mFrom.getSelectedItemPosition()){
            mTo.setSelection(mFrom.getSelectedItemPosition());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getTargetFragment().getClass() == AllFriendsFragment.class){
            int sex = SEX_ALL;
            if (mMan.isChecked()){
                sex = SEX_MAN;
            } else
            if (mGirl.isChecked()){
                sex = SEX_GIRL;
            }
            int from = mFrom.getSelectedItemPosition();
            int to = mTo.getSelectedItemPosition();
            ((AllFriendsFragment)getTargetFragment()).invokeSearch(sex, from, to);
        }
    }

}
