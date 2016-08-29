/*
 * Copyright (c) 2016  Ni YueMing<niyueming@163.com>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 */

package net.nym.devicepolicy;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private boolean isAdminActive;
    private DevicePolicyManager mDevicePolicyManager;
    private Snackbar mSnackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSnackbar = Snackbar.make(findViewById(android.R.id.content),"",Snackbar.LENGTH_SHORT);

        isAdminActive = adminActive();
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

    }

    private boolean adminActive(){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        ComponentName componentName = new ComponentName(this,MyDeviceAdminReceiver.class);
        boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);
        if (isAdminActive){
            return true;
        }else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
            startActivityForResult(intent,1);
            return false;
        }
    }

    private boolean removeAdmin(){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        ComponentName componentName = new ComponentName(this,MyDeviceAdminReceiver.class);
        boolean isAdminActive = devicePolicyManager.isAdminActive(componentName);
        if (isAdminActive){
            devicePolicyManager.removeActiveAdmin(componentName);
            return true;
        }
        return false;
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.getAdmin:
                if (!isAdminActive){
                    isAdminActive = adminActive();
                }else {
                    mSnackbar.setText("您已获取超级用户权限").show();
                }
                break;
            case R.id.removeAdmin:
                if (isAdminActive){
                    isAdminActive = !removeAdmin();
                    mSnackbar.setText("移除超级用户权限成功").show();
                }else {
                    mSnackbar.setText("您未获取超级用户权限").show();
                }
                break;
            case R.id.lock:
                if (isAdminActive){
                    mDevicePolicyManager.lockNow();
                }else {
                    isAdminActive = adminActive();
                }
                break;
            case R.id.reset_password:
                if (isAdminActive){
                    mDevicePolicyManager.resetPassword("123",DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                }else {
                    isAdminActive = adminActive();
                }
                break;
            case R.id.wipe_data:
                if (isAdminActive){
                    Bundle bundle = new Bundle();
                    bundle.putString("title","⚠️警告");
                    bundle.putString("message","清除数据会恢复系统到出厂设置，并格式化sd卡，请慎用！");
                    MyDialog.newInstance(bundle).show(getSupportFragmentManager(),"wipeData");

//                    mDevicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                }else {
                    isAdminActive = adminActive();
                }
                break;
            case R.id.disable_camera:
                if (isAdminActive){
                    ComponentName componentName = new ComponentName(this,MyDeviceAdminReceiver.class);
                    if (!mDevicePolicyManager.getCameraDisabled(componentName)){
                        mDevicePolicyManager.setCameraDisabled(componentName,true);
                    }
                }else {
                    isAdminActive = adminActive();
                }
                break;
            case R.id.enable_camera:
                if (isAdminActive){
                    ComponentName componentName = new ComponentName(this,MyDeviceAdminReceiver.class);
                    if (mDevicePolicyManager.getCameraDisabled(componentName)){
                        mDevicePolicyManager.setCameraDisabled(componentName,false);
                    }
                }else {
                    isAdminActive = adminActive();
                }
                break;
//            case R.id.set_device_owner:
//                if (isAdminActive){
//                    try {
//                        Method isDeviceOwnerApp = reflectMethod(mDevicePolicyManager.getClass(),"isDeviceOwnerApp", String.class);
//                        Boolean isDeviceOwner = (Boolean) isDeviceOwnerApp.invoke(mDevicePolicyManager,getPackageName());
//                        if (isDeviceOwner){
//                            mSnackbar.setText("该app已拥有设备").show();
//                        }else {
//                            Method setDeviceOwner = reflectMethod(mDevicePolicyManager.getClass(),"setDeviceOwner", String.class);
//                            Boolean flag = (Boolean) setDeviceOwner.invoke(mDevicePolicyManager,getPackageName());
//                            Log.e("setDeviceOwner",flag.toString());
//                        }
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }else {
//                    isAdminActive = adminActive();
//                }
//                break;
//            case R.id.clear_device_owner_app:
//                if (isAdminActive){
//                    try {
//                        Method isDeviceOwnerApp = reflectMethod(mDevicePolicyManager.getClass(),"isDeviceOwnerApp", String.class);
//                        Boolean isDeviceOwner = (Boolean) isDeviceOwnerApp.invoke(mDevicePolicyManager,getPackageName());
//                        if (!isDeviceOwner){
//                            mSnackbar.setText("该app没拥有设备").show();
//                        }else {
//                            Method clearDeviceOwnerApp = reflectMethod(mDevicePolicyManager.getClass(),"clearDeviceOwnerApp", String.class);
//                            clearDeviceOwnerApp.invoke(mDevicePolicyManager,getPackageName());
//                        }
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }else {
//                    isAdminActive = adminActive();
//                }
//                break;
//            case R.id.create_user:
//                try {
//                    Method isDeviceOwnerApp = reflectMethod(mDevicePolicyManager.getClass(),"isDeviceOwnerApp", String.class);
//                    Boolean isDeviceOwner = (Boolean) isDeviceOwnerApp.invoke(mDevicePolicyManager,getPackageName());
//                    if (!isDeviceOwner){
//                        mSnackbar.setText("该app还没拥有设备").show();
//                        break;
//                    }
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                    break;
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                    break;
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                    break;
//                }
//
//                if (isAdminActive){
//                    ComponentName componentName = new ComponentName(this,MyDeviceAdminReceiver.class);
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                        UserHandle userHandle = mDevicePolicyManager.createAndManageUser(componentName,"test",componentName,new PersistableBundle(),0);
//                        mDevicePolicyManager.switchUser(componentName,userHandle);
//                    }else {
//                        try {
//                            Class[] paramClasses = {
//                                    ComponentName.class
//                                    ,String.class
//                                    ,String.class
//                                    ,ComponentName.class
//                                    ,Bundle.class
//                            };
//                            Method method = mDevicePolicyManager.getClass().getDeclaredMethod("createAndInitializeUser",paramClasses);
//                            UserHandle userHandle = (UserHandle) method.invoke(mDevicePolicyManager,componentName,"test","test",componentName,new Bundle());
//                            mDevicePolicyManager.switchUser(componentName,userHandle);
//                        } catch (NoSuchMethodException e) {
//                            e.printStackTrace();
//                        } catch (InvocationTargetException e) {
//                            e.printStackTrace();
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }else {
//                    isAdminActive = adminActive();
//                }
//                break;
//            case R.id.set_profile_owner:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
//                    if (isAdminActive){
//                        if (!mDevicePolicyManager.isProfileOwnerApp(getPackageName())){
//                            ComponentName componentName = new ComponentName(this,MyDeviceAdminReceiver.class);
//                            mDevicePolicyManager.setProfileEnabled(componentName);
//                        }else {
//                            mSnackbar.setText("该app已拥有profile").show();
//                        }
//                    }else {
//                        isAdminActive = adminActive();
//                    }
//                }else {
//                    mSnackbar.setText("您的系统版本过低，android5.0才c").show();
//                }
//                break;
//            case R.id.clear_profile_owner:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
//                    if (isAdminActive){
//                        if (mDevicePolicyManager.isProfileOwnerApp(getPackageName())){
//                            mDevicePolicyManager.clearDeviceOwnerApp(getPackageName());
//                        }else {
//                            mSnackbar.setText("该app没拥有profile").show();
//                        }
//                    }else {
//                        isAdminActive = adminActive();
//                    }
//                }else {
//                    mSnackbar.setText("您的系统版本过低，android5.0才c").show();
//                }
//                break;
//            case R.id.disable_screen_capture:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
//                    if (isAdminActive){
//                        if (!mDevicePolicyManager.isProfileOwnerApp(getPackageName())){
//                            mSnackbar.setText("该app还没拥有profile").show();
//                            break;
//                        }
//                        ComponentName componentName = new ComponentName(this,MyDeviceAdminReceiver.class);
//                        if (!mDevicePolicyManager.getScreenCaptureDisabled(componentName)){
//                            mDevicePolicyManager.setScreenCaptureDisabled(componentName,true);
//                        }
//                    }else {
//                        isAdminActive = adminActive();
//                    }
//                }else {
//                    mSnackbar.setText("您的系统版本过低，android5.0才c").show();
//                }
//                break;
//            case R.id.enable_screen_capture:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
//                    if (isAdminActive){
//                        if (!mDevicePolicyManager.isProfileOwnerApp(getPackageName())){
//                            mSnackbar.setText("该app还没拥有profile").show();
//                            break;
//                        }
//                        ComponentName componentName = new ComponentName(this,MyDeviceAdminReceiver.class);
//                        if (!mDevicePolicyManager.getScreenCaptureDisabled(componentName)){
//                            mDevicePolicyManager.setScreenCaptureDisabled(componentName,false);
//                        }
//                    }else {
//                        isAdminActive = adminActive();
//                    }
//                }else {
//                    mSnackbar.setText("您的系统版本过低，android5.0才c").show();
//                }
//                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Main","requestCode=" + requestCode + ",result=" + resultCode);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 1:
                    isAdminActive = true;
                    mSnackbar.setText("获取超级用户权限成功").show();
                    break;
            }
        }else {
            switch (requestCode){
                case 1:
                    mSnackbar.setText("获取超级用户权限失败").show();
                    break;
            }
        }

    }

    private Method reflectMethod(Class clazz,String methodName,Class... params) throws NoSuchMethodException {
        return clazz.getDeclaredMethod(methodName,params);
    }

    public static class MyDialog extends DialogFragment{

        private MyDialog(){

        }

        public static MyDialog newInstance(Bundle arg){
            MyDialog myDialog = new MyDialog();
            myDialog.setArguments(arg);
            return myDialog;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle bundle = savedInstanceState == null ? getArguments(): savedInstanceState;
            String title = bundle == null ? "" :bundle.getString("title");
            String message = bundle == null ? "" :bundle.getString("message");
            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("conform", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Snackbar.make(getActivity().findViewById(android.R.id.content),"就是不让",Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("cancel",null)
                    .create();
//            return super.onCreateDialog(savedInstanceState);
        }
    }
}
