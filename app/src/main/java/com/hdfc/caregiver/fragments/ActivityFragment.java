package com.hdfc.caregiver.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hdfc.caregiver.CreatingTaskActivity;
import com.hdfc.caregiver.FeatureActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.CareGiver;
import com.hdfc.config.Config;
import com.hdfc.dbconfig.DbHelper;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActivityFragment extends Fragment
        implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnListItemClickListener,
        SlideAndDragListView.OnItemDeleteListener {

    //private static final int PICK_CONTACT = 979;
    public static ArrayList<ActivityModel> activityModels = Config.activityModels;
    //private static MultiBitmapLoader multiBitmapLoader;
    private static Utils utils;
    private static Context context;

    public static BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return activityModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomViewHolder cvh;

            if (convertView == null) {
                cvh = new CustomViewHolder();

                convertView = LayoutInflater.from(context).inflate(R.layout.my_tasks_item, null);
                cvh.imageTiming = (TextView) convertView.findViewById(R.id.imageTiming);
                cvh.textMessage = (TextView) convertView.findViewById(R.id.task_message);
                cvh.textSubject = (TextView) convertView.findViewById(R.id.task_subject);
                cvh.textTime = (TextView) convertView.findViewById(R.id.task_time);
                cvh.textViewWhat = (TextView) convertView.findViewById(R.id.textViewWhat);
                cvh.imagePerson = (ImageView) convertView.findViewById(R.id.imagePerson);
                cvh.linearLayout = (LinearLayout) convertView.findViewById(R.id.llFirst);
                cvh.linearParent = (LinearLayout) convertView.findViewById(R.id.linearParent);
                convertView.setTag(cvh);

            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            if (Config.activityModels.size() > 0) {

                //ActivityModel activityModel = activityModels.get(position);

                boolean isActivity = activityModels.get(position).getiActivityDisplayFlag();

                if (isActivity)
                    cvh.textViewWhat.setText(context.getString(R.string.activity));
                else
                    cvh.textViewWhat.setText(context.getString(R.string.task));

               /* if (iDisplayFlag == 3)
                    cvh.textViewWhat.setText(context.getString(R.string.activity));*/


                String strMessage = activityModels.get(position).getStrActivityDesc();

                if (strMessage != null && strMessage.length() > 20)
                    strMessage = activityModels.get(position).getStrActivityDesc().substring(0, 18)
                            + "..";

                String strName = activityModels.get(position).getStrActivityName();

                if (strName.length() > 20)
                    strName = activityModels.get(position).getStrActivityName().substring(0, 18)
                            + "..";

                cvh.textSubject.setText(strName);

                cvh.textMessage.setText(strMessage);

                cvh.textTime.setText(activityModels.get(position).getStrDisplayDate());

                if (activityModels.get(position).getStrCreatedBy() != null
                        && !activityModels.get(position).getStrCreatedBy().equalsIgnoreCase("")
                        && activityModels.get(position).getStrCreatedBy().
                        equalsIgnoreCase("customer")
                        ) {
                    //cvh.linearParent.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_customer));
                    cvh.textViewWhat.setTextColor(context.getResources().getColor(
                            R.color.colorRed));
                } else {
                    //cvh.linearParent.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_provider));
                    cvh.textViewWhat.setTextColor(context.getResources().getColor(
                            R.color.colorAccent));
                }

                if (activityModels.get(position).getStrActivityStatus().
                        equalsIgnoreCase("completed")) {
                    //cvh.imageTiming.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.done));
                    //cvh.imageTiming.setBackgroundResource(R.mipmap.ok); //done_action
                    cvh.imageTiming.setBackgroundResource(R.mipmap.done_action);
                    cvh.imageTiming.setTextColor(context.getResources().getColor(
                            R.color.colorWhite));
                    cvh.imageTiming.setText("");
                    cvh.linearLayout.setBackgroundDrawable(context.getResources().getDrawable(
                            R.drawable.status_closed));
                }

                if (activityModels.get(position).getStrActivityStatus().equalsIgnoreCase("new")
                        || activityModels.get(position).getStrActivityStatus().
                        equalsIgnoreCase("open")) {
                    cvh.imageTiming.setBackgroundResource(R.drawable.circle);
                    cvh.imageTiming.setText(context.getString(R.string.new_text));
                    cvh.imageTiming.setTextColor(context.getResources().getColor(R.color.colorRed));
                    cvh.linearLayout.setBackgroundDrawable(context.getResources().
                            getDrawable(R.drawable.status_new));
                }

                if (activityModels.get(position).getStrActivityStatus().
                        equalsIgnoreCase("inprocess")) {
                   /* cvh.imageTiming.setBackgroundResource(R.mipmap.cancel);
                    cvh.imageTiming.setText("");
                    //cvh.imageTiming.setTextAppearance(context, R.style.ActivityPending);
                    cvh.linearLayout.setBackgroundDrawable(context.getResources().getDrawable(
                            R.drawable.status_process));*/
                    cvh.imageTiming.setBackgroundResource(R.drawable.circle);
                    cvh.imageTiming.setText(activityModels.get(position).getStrDisplayTime());
                    cvh.imageTiming.setTextColor(context.getResources().getColor(
                            R.color.colorAccent));
                    cvh.linearLayout.setBackgroundDrawable(context.getResources().getDrawable(
                            R.drawable.status_process));
                }

                //pending status
                if (activityModels.get(position).getStrActivityStatus().
                        equalsIgnoreCase("pending")) {
                   /* cvh.imageTiming.setBackgroundResource(R.drawable.circle);
                    cvh.imageTiming.setText("");
                    //cvh.imageTiming.setTextAppearance(context, R.style.ActivityPending);
                    cvh.linearLayout.setBackgroundDrawable(context.getResources().getDrawable(
                            R.drawable.status_process));*/

                    cvh.imageTiming.setBackgroundResource(R.drawable.circle);
                    cvh.imageTiming.setText(activityModels.get(position).getStrDisplayTime());
                    cvh.imageTiming.setTextColor(context.getResources().getColor(
                            R.color.colorAccent));
                    cvh.linearLayout.setBackgroundDrawable(context.getResources().getDrawable(
                            R.drawable.status_process));
                }

                JSONObject jsonObject = null;
                Cursor cursor = null;
                String strUrl = "";

                try {

                    cursor = CareGiver.getDbCon().fetch(
                        DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                            DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID
                                    + "=?" + " and " + DbHelper.COLUMN_PROVIDER_ID + "=?",
                        new String[]{Config.collectionDependent,
                                activityModels.get(position).getStrDependentID(),
                                activityModels.get(position).getStrProviderID()
                        },
                        null, "0,1", true, null, null
                    );

                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        try {
                            if (cursor.getString(0) != null
                                    && !cursor.getString(0).equalsIgnoreCase("")) {
                                jsonObject = new JSONObject(cursor.getString(0));
                                strUrl = jsonObject.optString("dependent_profile_url");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CareGiver.getDbCon().closeCursor(cursor);
                }

                if (!strUrl.equalsIgnoreCase("")) {

                    if (!((Activity) context).isFinishing()) {
                      /*  Glide.with(context)
                                .load(strUrl)
                                .centerCrop()
                                .bitmapTransform(new CropCircleTransformation(context))
                                .placeholder(R.drawable.person_icon)
                                .crossFade()
                                .into(cvh.imagePerson);*/

                        Utils.loadGlide(context, strUrl, cvh.imagePerson, null);
                    }
                }
            }
            return convertView;
        }

        class CustomViewHolder {
            TextView imageTiming;
            TextView textSubject;
            ImageView imagePerson;
            TextView textMessage;
            TextView textTime;
            TextView textViewWhat;
            LinearLayout linearLayout;
            LinearLayout linearParent;
        }
    };
    private Menu mMenu;

    static ActivityFragment newInstance() {
        return new ActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simple_activity, container, false);

        ImageButton fab = (ImageButton) view.findViewById(R.id.fab);
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        //

        initMenu();

        utils = new Utils(getActivity());

        context = getActivity();

        fab.setVisibility(View.VISIBLE);

        TextView textViewEmpty = (TextView) view.findViewById(android.R.id.empty);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatingTaskActivity.class);
                Config.intSelectedMenu=Config.intDashboardScreen;
                startActivity(intent);
            }
        });

        SlideAndDragListView mListView = (SlideAndDragListView) view.findViewById(R.id.listViewEdit);

        mListView.setMenu(mMenu);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(textViewEmpty);
        mListView.setOnListItemLongClickListener(this);
        //mListView.setOnDragListener(this, mAppList);
        //mListView.setOnListItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);


        mListView.setOnListItemClickListener(new SlideAndDragListView.OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                if (activityModels.size() > 0) {
                    Bundle args = new Bundle();
                    args.putSerializable("ACTIVITY", activityModels.get(position));
                    //args.putInt("ACTIVITY_POSITION", position);
                    Intent intent = new Intent(getActivity(), FeatureActivity.class);
                    args.putInt("WHICH_SCREEN", 3);
                    intent.putExtras(args);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        //refreshData();
    }


    private void initMenu() {

        mMenu = new Menu(new ColorDrawable(Color.LTGRAY), true);

        //swipe right

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(getActivity().getResources().getDrawable(R.color.blue))
                .setIcon(getActivity().getResources().getDrawable(R.drawable.pen))
                .build());


        //swipe left

      /*  mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img)-30)
                .setBackground(getActivity().getResources().getDrawable(R.color.blue))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.location_action))
                .build());*/

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img) - 30)
                .setBackground(getActivity().getResources().getDrawable(R.color.blue))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.message_action))
                .build());

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img)-30)
                .setBackground(getActivity().getResources().getDrawable(R.color.blue))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.call_action))
                .build());
    }

    @Override
    public void onListItemLongClick(View view, int position) {
    }

    @Override
    public void onDragViewStart(int position) {

    }

    @Override
    public void onDragViewMoving(int position) {
    }

    @Override
    public void onDragViewDown(int position) {
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        if (activityModels.size() > 0) {
                            Bundle args = new Bundle();
                            args.putSerializable("ACTIVITY", activityModels.get(itemPosition));
                            Intent intent = new Intent(getActivity(), FeatureActivity.class);
                            args.putInt("WHICH_SCREEN", 3);
                            intent.putExtras(args);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        return Menu.ITEM_SCROLL_BACK;
                }
                break;
            case MenuItem.DIRECTION_RIGHT:

                String strNo = "";

                if (activityModels.get(itemPosition).getStrDependentID() != null &&
                        !activityModels.get(itemPosition).getStrDependentID().equalsIgnoreCase("")) {

                    Cursor cursor1 = CareGiver.getDbCon().fetch(
                            DbHelper.strTableNameCollection, new String[]{DbHelper.COLUMN_DOCUMENT},
                            DbHelper.COLUMN_COLLECTION_NAME + "=? and " + DbHelper.COLUMN_OBJECT_ID
                                    + "=?" + " and " + DbHelper.COLUMN_PROVIDER_ID + "=?",
                            new String[]{Config.collectionDependent,
                                    activityModels.get(itemPosition).getStrDependentID(),
                                    activityModels.get(itemPosition).getStrProviderID()
                            },
                            null, "0,1", true, null, null
                    );


                    if (cursor1.getCount() > 0) {
                        cursor1.moveToFirst();
                        try {
                            if (cursor1.getString(0) != null && !cursor1.getString(0).
                                    equalsIgnoreCase("")) {
                                JSONObject jsonObject = null;
                                jsonObject = new JSONObject(cursor1.getString(0));
                                strNo = jsonObject.optString("dependent_contact_no");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    CareGiver.getDbCon().closeCursor(cursor1);
                }

                switch (buttonPosition) {
                   /* case 0:

                       *//* int iPosition3 = Config.dependentIds.indexOf(activityModel.getStrDependentID());
                        String strNo4 = Config.dependentModels.get(iPosition3).getStrAddress();*//*
                        //Toast.makeText(getContext(), strNo4, Toast.LENGTH_LONG).show();
                        return Menu.ITEM_SCROLL_BACK;*/
                    case 0:
                        try {
                            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);

                            if (!strNo.equalsIgnoreCase("")) {

                                sendIntent.putExtra("sms_body", activityModels.get(itemPosition).
                                        getStrActivityName());
                                sendIntent.putExtra("address", strNo);
                                sendIntent.setType("vnd.android-dir/mms-sms");
                                startActivity(sendIntent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (!strNo.equalsIgnoreCase("")) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("smsto:" + Uri.encode(strNo)));
                                startActivity(intent);
                            }
                        }
                        return Menu.ITEM_SCROLL_BACK;
                    case 1:
                        try {
                            if (!strNo.equalsIgnoreCase("")) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                String strNo1 = "tel:" + String.valueOf(strNo);
                                callIntent.setData(Uri.parse(strNo1));
                                startActivity(callIntent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return Menu.ITEM_SCROLL_BACK;
                }


        }
        return Menu.ITEM_NOTHING;
    }

    @Override
    public void onItemDelete(View view, int position) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(View v, int position) {

    }
}
