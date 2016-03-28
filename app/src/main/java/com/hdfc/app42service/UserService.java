package com.hdfc.app42service;

import android.content.Context;

import com.hdfc.libs.AsyncApp42ServiceApi;
import com.shephertz.app42.paas.sdk.android.App42CallBack;

import java.util.ArrayList;

public class UserService/* implements
        AsyncApp42ServiceApi.App42UserServiceListener*/ {

    private ArrayList<String> roleList = new ArrayList<>();
    private AsyncApp42ServiceApi asyncService;
   /* private Context _ctxt;
    private Libs libs;
    private View formView, progressView;
    private SharedPreferences sPre;*/

    public UserService(Context context) {
        //this._ctxt = context;
        asyncService = AsyncApp42ServiceApi.instance(context);
        // libs = new Libs(context);

        roleList.add("Customer");
    }

    public void authenticate(String userName, String password, App42CallBack app42CallBack) {
        //libs.showProgress(true, formView, progressView);
        asyncService.authenticateUser(userName, password, app42CallBack);
    }

    /*public void addJSONObject(String collectionProvider, String password) {
        //libs.showProgress(true, formView, progressView);
        //asyncService.addJSONObject(collectionProvider, jsonDoc);
    }*/

    public void onChangePassword(String userName, String oldPassword, String confirmPassword, App42CallBack app42CallBack) {
        asyncService.changePassword(userName, oldPassword, confirmPassword, app42CallBack);
    }

    public void getUser(String userName, App42CallBack app42CallBack) {
        //libs.showProgress(true, formView, progressView);
        asyncService.getUser(userName, app42CallBack);
    }

   /* public void onGetUserRoles(String userName) {
        // libs.showProgress(true, formView, progressView);
        asyncService.getRolesByUser(userName, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {
                User user = (User) o;
                StringBuffer strBuffer = new StringBuffer();
                for (int i = 0; i < user.getRoleList().size(); i++) {
                    strBuffer.append(user.getRoleList().get(i) + "\n");
                }
                //libs.showProgress(false, formView, progressView);
                libs.createAlertDialog(strBuffer.toString());
            }

            @Override
            public void onException(Exception e) {
                //libs.showProgress(false, formView, progressView);
                libs.createAlertDialog(e.getMessage());
            }
        });
    }*/

    /*@Override
    public void onUserCreated(User user) {
        //libs.showProgress(false, formView, progressView);
        libs.createAlertDialog("User Created");

        Intent loginActivity = new Intent(_ctxt, LoginActivity.class);
        _ctxt.startActivity(loginActivity);

        //onSigninClicked(user.getUserName().toString(), user.getPassword().toString());
    }

    @Override
    public void onUserAuthenticated(User response) {
        //libs.showProgress(false, formView, progressView);
        //libs.createAlertDialog(response.getUserName().toString().trim());

        sPre = _ctxt.getSharedPreferences("App42Sample", Context.MODE_PRIVATE);

        SharedPreferences.Editor sPreEdit = sPre.edit();

        sPreEdit.putString("USERNAME", response.getUserName().toString().trim());

        sPreEdit.commit();

        Intent mainActivity = new Intent(_ctxt, LoginActivity.class);
        _ctxt.startActivity(mainActivity);
    }*/

   /* public void setData(User response){
            LoginActivity.setData(response);
    }*/


    /*public void createOrUpdateUser(User user, String strFirstName, int intSex, Date dateDob) {
        //libs.showProgress(true, formView, progressView);

        User.Profile profile = user.new Profile();
        profile.setFirstName(strFirstName);
        profile.setLastName("Testing");

        if (intSex == 1)
            profile.setSex(User.UserGender.MALE);

        if (intSex == 2)
            profile.setSex(User.UserGender.FEMALE);

        profile.setDateOfBirth(dateDob);
        profile.setCity("Chennai");
        profile.setState("TamilNadu");
        profile.setPincode("600025");
        profile.setCountry("India");
        profile.setMobile("+91-9789863136");
        profile.setHomeLandLine("+91-04177-230583");
        profile.setOfficeLandLine("+91-04442643418");

        asyncService.createOrUpdateUser(user, new App42CallBack() {
            @Override
            public void onSuccess(Object o) {
                //libs.showProgress(false, formView, progressView);
                libs.createAlertDialog("Details Saved");
            }

            @Override
            public void onException(Exception e) {
                libs.createAlertDialog(e.getMessage());
                //libs.showProgress(false, formView, progressView);
            }
        });
    }*/

    public void onCreateUser(String userName, String password, String email, App42CallBack app42CallBack) {
        asyncService.createUser(userName, password, email, roleList, app42CallBack);
    }

    /*@Override
    public void onCreationFailed(App42Exception exception) {
        libs.createAlertDialog(exception.getMessage());
        //libs.showProgress(false, formView, progressView);
    }

    @Override
    public void onAuthenticationFailed(App42Exception exception) {
        //libs.showProgress(false, formView, progressView);
        libs.createAlertDialog("Login Failed");
    }


    @Override
    public void onGetUserSuccess(User response) {
        //libs.showProgress(false, formView, progressView);
        // setData(response);
    }

    @Override
    public void onGetUserFailed(App42Exception exception) {
        libs.createAlertDialog(exception.getMessage());
        //libs.showProgress(false, formView, progressView);
    }*/

}