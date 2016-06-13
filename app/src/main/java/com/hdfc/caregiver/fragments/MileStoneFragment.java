package com.hdfc.caregiver.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.hdfc.caregiver.FeatureActivity;
import com.hdfc.caregiver.R;
import com.hdfc.config.Config;
import com.hdfc.libs.MultiBitmapLoader;
import com.hdfc.libs.Utils;
import com.hdfc.models.ActivityModel;
import com.hdfc.models.MilestoneViewModel;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MileStoneFragment extends Fragment implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnListItemClickListener,
        SlideAndDragListView.OnItemDeleteListener {

    //private static final int PICK_CONTACT = 979;
    public static ArrayList<MilestoneViewModel> milestoneModels = Config.milestoneModels;
    private static MultiBitmapLoader multiBitmapLoader;
    private static Utils utils;
    private static Context context;
    public static BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return milestoneModels.size();
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

                convertView = LayoutInflater.from(context).inflate(R.layout.my_tasks_ms_item, null);
                cvh.imageTiming = (TextView) convertView.findViewById(R.id.imageTiming);
                cvh.textMessage = (TextView) convertView.findViewById(R.id.task_message);
                cvh.textTime = (TextView) convertView.findViewById(R.id.task_time);
                cvh.imagePerson = (ImageView) convertView.findViewById(R.id.imagePerson);
                cvh.linearLayout = (LinearLayout) convertView.findViewById(R.id.llFirst);
                convertView.setTag(cvh);

            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }

            if (Config.milestoneModels.size() > 0) {

                String strMessage = milestoneModels.get(position).getStrMileStoneName();

                if (strMessage != null && strMessage.length() > 20)
                    strMessage = milestoneModels.get(position).getStrMileStoneName().substring(0, 18) + "..";

               /* String strName = milestoneModels.get(position).getStrMileStoneName();

                if (strName.length() > 20)
                    strName = milestoneModels.get(position).getStrMileStoneName().substring(0, 18) + "..";*/

                cvh.textMessage.setText(strMessage);

                //
                //INACTIVE, OPENED, INPROCESS, COMPLETED, REOPENED, PENDING

                cvh.textTime.setText(utils.formatDate(milestoneModels.get(position).getStrMilestoneDate()));

                if (milestoneModels.get(position).getStrMileStoneStatus().equalsIgnoreCase("completed")) {
                    cvh.imageTiming.setBackgroundResource(R.drawable.circle);
                    cvh.imageTiming.setText(utils.formatDateTime(milestoneModels.get(position).getStrMilestoneDate()));
                    cvh.imageTiming.setTextColor(context.getResources().getColor(R.color.gray_holo_dark));
                    cvh.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                }

                ///////////////////////////////////////////////////////////
                if (milestoneModels.get(position).getStrMileStoneStatus().equalsIgnoreCase("pending")) {
                    cvh.imageTiming.setBackgroundResource(R.drawable.circle);
                    cvh.imageTiming.setText(utils.formatDateTime(milestoneModels.get(position).getStrMilestoneDate()));
                    cvh.imageTiming.setTextColor(context.getResources().getColor(R.color.gray_holo_dark));
                    cvh.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
                }

                if (milestoneModels.get(position).getStrMileStoneStatus().equalsIgnoreCase("opened")
                        || milestoneModels.get(position).getStrMileStoneStatus().equalsIgnoreCase("reopened")
                        || milestoneModels.get(position).getStrMileStoneStatus().equalsIgnoreCase("inprocess")
                        ) {
                    cvh.imageTiming.setBackgroundResource(R.drawable.circle);
                    cvh.imageTiming.setText(utils.formatDateTime(milestoneModels.get(position).getStrMilestoneDate()));
                    cvh.imageTiming.setTextColor(context.getResources().getColor(R.color.gray_holo_dark));
                    cvh.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorOrange));
                }
                ///////////////////////////////////////////////////////////

                File fileImage = Utils.createFileInternal("images/" + utils.replaceSpace(milestoneModels.get(position).getStrDependentId()));

                if (fileImage.exists()) {
                    String filename = fileImage.getAbsolutePath();
                    multiBitmapLoader.loadBitmap(filename, cvh.imagePerson);
                } else {
                    cvh.imagePerson.setImageDrawable(context.getResources().getDrawable(R.drawable.person_icon));
                }
            }
            return convertView;
        }

        class CustomViewHolder {
            TextView imageTiming;
            ImageView imagePerson;
            TextView textMessage;
            TextView textTime;
            LinearLayout linearLayout;
        }
    };
    private Menu mMenu;

    public MileStoneFragment() {
    }

    public static MileStoneFragment newInstance() {
        return new MileStoneFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simple_activity, container, false);
        initMenu();
        utils = new Utils(getActivity());
        multiBitmapLoader = new MultiBitmapLoader(getActivity());

        context = getActivity();

        ImageButton add = (ImageButton) view.findViewById(R.id.fab);
        add.setVisibility(View.GONE);

        TextView textViewEmpty = (TextView) view.findViewById(android.R.id.empty);


        SlideAndDragListView mListView = (SlideAndDragListView) view.findViewById(R.id.listViewEdit);

        mListView.setMenu(mMenu);
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(textViewEmpty);
        mListView.setOnListItemLongClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initMenu() {

        mMenu = new Menu(new ColorDrawable(Color.LTGRAY), true);

        //swipe right

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setIcon(getActivity().getResources().getDrawable(R.drawable.pen))
                .build());

        /*mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setText("Done")
                .setTextColor(Color.WHITE)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());*/

       /* mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setText("Undo")
                .setTextColor(Color.WHITE)
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());*/

        /*mMenu.addItem(new MenuItem.Builder()
                .setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img)+50 )
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setIcon(getActivity().getResources().getDrawable(R.drawable.circle))
                .build());*/

        //swipe left

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img) - 30)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.map))
                .build());

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img) - 30)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.message))
                .build());

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img) - 30)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.call1))
                .build());

       /* mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img) - 30)
                .setBackground(getActivity().getResources().getDrawable(R.color.polygonViewCircleStrokeColor))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.drawable.person_icon))
                .build());*/
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

        int iPosition = 0;

        if (milestoneModels != null && milestoneModels.get(itemPosition) != null &&
                milestoneModels.get(itemPosition).getStrActivityId() != null) {
            iPosition = Config.strActivityIds.indexOf(milestoneModels.get(itemPosition).getStrActivityId());
        }

        ActivityModel activityModel = null;

        if (iPosition > -1)
            activityModel = Config.activityModels.get(iPosition);

        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        if (activityModel != null) {
                            Bundle args = new Bundle();
                            args.putSerializable("ACTIVITY", activityModel);

                            if (!activityModel.getStrActivityStatus().equalsIgnoreCase("completed")) {
                                Intent intent = new Intent(getActivity(), FeatureActivity.class);
                                intent.putExtras(args);
                                startActivity(intent);
                            }
                        }
                        return Menu.ITEM_SCROLL_BACK;
                    case 1:
                       /* if (activityModel != null) {
                            Bundle args = new Bundle();
                            args.putSerializable("ACTIVITY", activityModel);

                            if (!activityModel.getStrActivityStatus().equalsIgnoreCase("completed")) {
                                Intent intent = new Intent(getActivity(), FeatureActivity.class);
                                intent.putExtras(args);
                                startActivity(intent);
                            }
                        }*/
                        return Menu.ITEM_SCROLL_BACK;
                    case 2:

                       /* if (activityModels.size() > 0) {
                            Bundle args = new Bundle();
                            args.putSerializable("ACTIVITY", activityModels.get(itemPosition));
                            if (activityModels.get(itemPosition).getStrActivityStatus().equalsIgnoreCase("completed")) {
                                //  updateData();
                            } else {
                                utils.toast(2, 2, "Error. Try Again!!!");
                            }
                        }*/

                        return Menu.ITEM_SCROLL_BACK;
                    case 3:
                        return Menu.ITEM_NOTHING;
                }
                break;

            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                      /*  if (activityModel != null) {
                            int iPosition3 = Config.dependentIds.indexOf(activityModel.getStrDependentID());
                            String strNo4 = Config.dependentModels.get(iPosition3).getStrAddress();
                            Toast.makeText(getContext(), strNo4, Toast.LENGTH_LONG).show();
                        }*/
                        return Menu.ITEM_SCROLL_BACK;
                    case 1:
                        if (activityModel != null) {

                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);

                            int iPosition2 = Config.dependentIds.indexOf(activityModel.getStrDependentID());

                            String strNo2 = "";

                            if (iPosition2 > -1)
                                strNo2 = Config.dependentModels.get(iPosition2).getStrContacts();

                            if (!strNo2.equalsIgnoreCase("")) {

                                sendIntent.putExtra("sms_body", activityModel.getStrActivityName());
                                sendIntent.putExtra("address", strNo2);
                                sendIntent.setType("vnd.android-dir/mms-sms");
                                startActivity(sendIntent);
                            }
                        }

                        return Menu.ITEM_SCROLL_BACK;
                    case 2:
                        if (activityModel != null) {

                            int iPosition3 = Config.dependentIds.indexOf(activityModel.getStrDependentID());

                            String strNo3 = "";

                            if (iPosition3 > -1)
                                strNo3 = Config.dependentModels.get(iPosition3).getStrContacts();

                            if (!strNo3.equalsIgnoreCase("")) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                String strNo1 = "tel:" + String.valueOf(strNo3);
                                callIntent.setData(Uri.parse(strNo1));
                                startActivity(callIntent);
                            }
                        }

                        return Menu.ITEM_SCROLL_BACK;
                    case 3:
                        /*Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, PICK_CONTACT);*/

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
        int iPosition = 0;

        if (milestoneModels != null && milestoneModels.get(position) != null &&
                milestoneModels.get(position).getStrActivityId() != null) {
            iPosition = Config.strActivityIds.indexOf(milestoneModels.get(position).getStrActivityId());
        }

        ActivityModel activityModel = null;

        if (iPosition > -1)
            activityModel = Config.activityModels.get(iPosition);

        if (activityModel != null) {
            Bundle args = new Bundle();
            args.putSerializable("ACTIVITY", activityModel);

            if (!activityModel.getStrActivityStatus().equalsIgnoreCase("completed")) {
                Intent intent = new Intent(getActivity(), FeatureActivity.class);
                intent.putExtras(args);
                startActivity(intent);
            }
        }
    }
}
