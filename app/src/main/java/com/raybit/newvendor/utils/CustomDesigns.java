package com.raybit.newvendor.utils;

import static androidx.core.graphics.TypefaceCompatUtil.getTempFile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;


import com.raybit.newvendor.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Aamir Bashir on 05-03-2021.
 */
public class CustomDesigns {
    public static void setupSpinner(ArrayList<String> list, Spinner spinner, View view, Context context, SpinnerItemSelectedListener listener, int selected) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(selected);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                listener.onItemSelected(list.get(i), view, spinner.getId(), i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public static void pickDate(Context context, DateSelectorListener listener, View view) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String isToday = "";
                if (i == Calendar.YEAR) {
                    if (i1 == Calendar.MONTH) {
                        if (i2 == Calendar.DAY_OF_MONTH) {
                            isToday = " Today";
                        }
                    }
                }
                String day = String.valueOf(i2);
                String month = String.valueOf(i1 + 1);
                if (i2 < 10) {
                    day = "0" + day;
                }
                if (i1 < 10) {
                    month = "0" + month;
                }
                listener.onDateSelected(day + " " + DateTimeUtils.INSTANCE.getMonth(Integer.parseInt(month) - 1) + "," + i + isToday, view);
            }
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();

    }
    public static void pickDateBoth(Context context, DateSelectorListener listener, View view) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String isToday = "";
                if (i == Calendar.YEAR) {
                    if (i1 == Calendar.MONTH) {
                        if (i2 == Calendar.DAY_OF_MONTH) {
                            isToday = " Today";
                        }
                    }
                }
                String day = String.valueOf(i2);
                String month = String.valueOf(i1 + 1);
                if (i2 < 10) {
                    day = "0" + day;
                }
                if (i1 < 10) {
                    month = "0" + month;
                }
                listener.onDateSecondSelected(i + "-" +month + "-"+day  , view);
                listener.onDateSelected(day + " " + DateTimeUtils.INSTANCE.getMonth(Integer.parseInt(month) - 1) + "," + i + isToday, view);
            }
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();

    }
    @SuppressLint("RestrictedApi")
    public static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.pick_image_intent_text));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }
    public interface SpinnerItemSelectedListener {
        void onItemSelected(String text, View view, int spinnerId, int position);
    }

    public interface DateSelectorListener {
        abstract void onDateSelected(String string, View view);
        abstract void onDateSecondSelected(String string, View view);
    }
}
