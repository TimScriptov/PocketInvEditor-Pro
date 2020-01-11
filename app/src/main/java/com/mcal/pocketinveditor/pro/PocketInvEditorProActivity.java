package com.mcal.pocketinveditor.pro;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import com.mcal.pocketinveditor.PocketInvEditorActivity;
import java.io.File;

public final class PocketInvEditorProActivity extends PocketInvEditorActivity {
    protected void openWorld(File worldFile) {
        Intent intent = new Intent(this, EditorProActivity.class);
        intent.putExtra("world", worldFile.getAbsolutePath());
        startActivity(intent);
    }

    protected boolean grabPermissions() {
        if (checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1234);
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 1234) {
            return;
        }
        if (permissions.length < 0 || grantResults[0] != 0) {
            new Builder(this).setMessage(R.string.storage_permission_required).setPositiveButton(android.R.string.ok, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return;
        }
        hasPermission = true;
        loadWorlds();
    }

    public static Dialog createBackupsNotSupportedDialog(final Activity activity) {
        return new Builder(activity).setMessage("Backed up versions of PocketInvEditor are not supported, as PocketInvEditor depends on updates from the application store.  Please reinstall PocketInvEditor. If you believe you received this message in error, contact zhuowei_applications@yahoo.com").setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                activity.finish();
            }
        }).setCancelable(false).create();
    }
}
