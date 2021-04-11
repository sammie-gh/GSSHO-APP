package com.gh.sammie.ghanastatisticalservice;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class App extends Application {

    @Override
    public void onCreate() {

//        if (MissingSplitsManagerFactory.create(this)
//                .disableAppIfMissingRequiredSplits()) {
//            return;
//        }

        super.onCreate();
        SweetAlertDialog.DARK_STYLE = true;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
