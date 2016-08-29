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

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    private final static String TAG = MyDeviceAdminReceiver.class.getSimpleName();
    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Log.i(TAG,intent.toString());
        Log.i(TAG,"onPasswordChanged");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Log.i(TAG,intent.toString());
        Log.i(TAG,"onPasswordFailed");
        try{
            Log.e(TAG,"failed=" + getManager(context).getCurrentFailedPasswordAttempts());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Log.i(TAG,intent.toString());
        Log.i(TAG,"onPasswordSucceeded");
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Log.i(TAG,intent.toString());
        Log.i(TAG,"onEnabled");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        Log.i(TAG,intent.toString());
        Log.i(TAG,"onDisableRequested");
        return super.onDisableRequested(context, intent);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Log.i(TAG,intent.toString());
        Log.i(TAG,"onDisabled");
    }


}
