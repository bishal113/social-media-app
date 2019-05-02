package com.example.lenovo.uploadpic.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by Ravi on 08/07/15.
 */
public class PrefManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Myapp";

    // All Shared Preferences Keys
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_USER_CODE= "user_code";
    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_EMAIL_ADDRESS = "email_address";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PROFILE_IMAGE= "profile_image";
    private static final String KEY_PASSWORD= "profile_pass";
    private static final String KEY_USER_LIKE= "user_like";
    private static final String KEY_TOP_CATEGORY= "top_category";
    private static final String KEY_ALL_CATEGORY= "all_category";
    private static final String KEY_TOP_SERVICES= "top_services";
    private static final String KEY_TEMP_KEY= "TEMP_KEY";


    private static final String KEY_CALL_SUPPORT_LIST= "call_support_list";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public String getTempKey(){
        return pref.getString(KEY_TEMP_KEY, null);
    }

    public boolean getLike(){
        return  pref.getBoolean(KEY_USER_LIKE,false);
    }

    public void setTempKey(String data) {
        editor.putString(KEY_TEMP_KEY, data);
        editor.commit();
    }

    public void saveCallSupportData(String data) {
        editor.putString(KEY_CALL_SUPPORT_LIST, data);
        editor.commit();
    }
    public String getCallSupportData() {
        return pref.getString(KEY_CALL_SUPPORT_LIST, null);
    }




    public void setUserCode(int code) {
        editor.putString(KEY_USER_CODE, code+"");
        editor.commit();
    }
    public void setUserName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }
    public void setMobile(String mobile) {
        editor.putString(KEY_MOBILE, mobile);
        editor.commit();
    }
    public void setEmailAddress(String email) {
        editor.putString(KEY_EMAIL_ADDRESS, email);
        editor.commit();
    }
    public void setApiKey(String key) {
        editor.putString(KEY_API_KEY, key);
        editor.commit();
    }
    public void setProfileImage(String val) {
        editor.putString(KEY_PROFILE_IMAGE, val);
        editor.commit();
    }
    public void removeprofileImg(){
        editor.putString(KEY_PROFILE_IMAGE,null);
        editor.commit();
    }
    public void setUserPass(String code) {
        editor.putString(KEY_PASSWORD, code);
        editor.commit();
    }

    public void setLike(){
        editor.putBoolean(KEY_USER_LIKE,true);
        editor.commit();
    }

    public String getUserKey() {
        return pref.getString(KEY_API_KEY, null);
    }
    public String getUserId() {
        return pref.getString(KEY_USER_CODE, "");
    }
    public String getUserName() {
        return pref.getString(KEY_NAME, null);
    }
    public String getEmailAddress() {

        return pref.getString(KEY_EMAIL_ADDRESS, "--");
    }
    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE, "--");
    }
    public String getProfileImage() {
        return pref.getString(KEY_PROFILE_IMAGE, null);
    }
    public String getUserPass() {

        return pref.getString(KEY_PASSWORD, null);
    }

    public void createLogin(int code, String name, String email,String mobile,String profile) {
        String id=""+code;
        editor.putString(KEY_USER_CODE,id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL_ADDRESS, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_PROFILE_IMAGE,profile);



       // editor.putString(KEY_API_KEY, key);
       // editor.putString(KEY_PROFILE_IMAGE, profile_image);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
       // editor.putString(KEY_USER_CATEGORY, user_category);
        editor.commit();
    }


    public boolean isLoggedIn() {

        return pref.getBoolean(KEY_IS_LOGGED_IN, false);    //false here is default value.
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        //profile.put("key", pref.getString(KEY_API_KEY, null));
       // profile.put("user_code", pref.getString(KEY_USER_CODE, null));
        profile.put("name", pref.getString(KEY_NAME, null));
       // profile.put("mobile", pref.getString(KEY_MOBILE, null));
        profile.put("email", pref.getString(KEY_EMAIL_ADDRESS, null));
        profile.put("profile_image", pref.getString(KEY_PROFILE_IMAGE, null));
        return profile;
    }

    public  String getTopCategories(){
        return pref.getString(KEY_TOP_CATEGORY, null);
    }
    public  String getKeyTopServices(){
        return pref.getString(KEY_TOP_SERVICES, null);
    }
    public void setKeyTopCategory(String data) {
        editor.putString(KEY_TOP_CATEGORY, data);
        editor.commit();
    }
    public void setKeyTopServices(String data) {
        editor.putString(KEY_TOP_SERVICES, data);
        editor.commit();
    }
    public  String getKeyAllCategory(){
        return pref.getString(KEY_ALL_CATEGORY, null);
    }
    public void setKeyAllCategory(String data) {
        editor.putString(KEY_ALL_CATEGORY, data);
        editor.commit();
    }
}
