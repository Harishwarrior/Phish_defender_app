package com.geeks4ever.phish;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_CODE = 676;
    private static final String TAG = "Phish_MainActivity";
    public boolean enabled;
    public Button btn;
    public TextView status;
    public BottomNavigationView bottomnavigationbar;

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.isEnabled);
        status = findViewById(R.id.ProtectionStatus);
        accessibilityEnabledStatusCheck();
        bottomnavigationbar=(BottomNavigationView)findViewById(R.id.bottom_navigation);
        //findViewById(R.id.buttonCreateWidget).setOnClickListener(this);

        bottomnavigationbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        Toast.makeText(MainActivity.this, "Recents", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.comment:
                        Toast.makeText(MainActivity.this, "Favorites", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.stats:
                        startActivity(new Intent(MainActivity.this, com.geeks4ever.phish.Settings.class));
                        break;

                }
                return true;
            }
        });
    }


    public void accessibilityEnabledStatusCheck(){
        if(enabled = isAccessibilityServiceEnabled(getApplicationContext(), MyAccessibilityService.class)){
            //Enabled
            btn.setText("Disable");
            btn.setBackgroundColor(0xFF00FF00);
            btn.setTextColor(0xFF000000);
            status.setText("You're Protected!");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                askPermission();
            }

        }else{
            //disabled
            //show Enable btn
            btn.setText("Enable");
            btn.setTextColor(0xFFFFFFFF);
            btn.setBackgroundColor(0xFFFF0000);
            status.setText("You're NOT Protected!");
        }
    }

    public void checkEnabled(View view){
        if(enabled){
            //permission given
            Log.d(TAG,"accessibility permission given");
            getAccessibilityPermissions();
        }else{
            //not given show UI to give permission
            Log.d(TAG,"accessibility permission NOT given");
            getAccessibilityPermissions();
        }
    }

    public void getAccessibilityPermissions(){
        Log.d(TAG,"getting accessibility permissions!");
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        // after resuming the activity
        accessibilityEnabledStatusCheck();

    }
}
