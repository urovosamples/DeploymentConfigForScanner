package com.ubx.sharecontent;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

/**
 * by fileprovider to config.introduced in V2.1.17_20220922
 * get deployment Broadcast result introduced in V2.1.18_20221124
 *
 */
public class MainActivity extends AppCompatActivity {
    public static final String INTENT_SERVICE_ACTION = "action.EXPORT_IMPORT_SCANNER_SERVICE";
    public static final String INTENT_DW_SERVICE_ACTION = "com.ubx.datawedge.EXPORT_IMPORT_SCANNER_SERVICE";

    private static final String ACTION_EXPORT_SCANNER_CONFIG = "com.ubx.datawedge.EXPORT_SCANNER_CONFIG";
    private static final String ACTION_IMPORT_SCANNER_CONFIG = "com.ubx.datawedge.IMPORT_SCANNER_CONFIG";
    private static final String ACTION_IMPORT_FILEPROVIDER_CONFIG = "com.ubx.datawedge.IMPORT_FILEPROVIDER_CONFIG";

    public static final String INTENT_DEPLOYMENT_SWITCH_ACTION = "com.ubx.datawedge.DEPLOYMENT_SWITCH_ACTION";
    public static final String INTENT_DEPLOYMENT_RESULT_ACTION = "com.ubx.datawedge.DEPLOYMENT_RESULT_ACTION";
    public static final String RESULT_CODE = "RESULT_CODE";
    public static final String RESULT_INFO_EXTRA = "RESULT_INFO_EXTRA";
    public static final int Base_Error = -1000;
    public static final int UNKNOWN = Base_Error - 1;
    public static final int PROFILE_NOT_FOUND = Base_Error -2;
    public static final int PROFILE_ALREADY_EXIST = Base_Error -3;
    public static final int PARAMETER_INVALID = Base_Error -4;
    public static final int PROFILE_NAME_EMPTY = Base_Error -5;
    public static final int INVALID_FILE_PATH = Base_Error -6;
    public static final int INVALID_FOLDER_PATH = Base_Error -7;
    public static final int INVALID_CONFIG_FILE = Base_Error -8;
    public static final int CONFIG_FILE_NOT_EXIST = Base_Error -9;
    public static final int INVALID_FILE_NAME = Base_Error -10;
    /**
     * ScanWedge was unable to read the specified database file
     */
    public static final int CANNOT_READ_FILE = Base_Error - 11;
    //执行配置操作
    public static final String IES_CONFIG_ACTION = "config_action";
    public static final int IES_CONFIG_ACTION_UNKNOWN = 0;
    //导入配置文件
    public static final int IES_CONFIG_ACTION_IMPORT = 1;
    //导出配置文件
    public static final int IES_CONFIG_ACTION_EXPORT = 2;
    //开机自动检查导入指定配置文件
    public static final int IES_CONFIG_ACTION_AUTO = 3;
    public static final int IES_CONFIG_ACTION_SWITCH = 10;
    //配置文件的Profile 名称
    public static final String IES_CONFIG_PROFILE_NAME = "profileName";
    //指定导入或导出配置文件路径
    public static final String IES_CONFIG_PROFILE_PATH = "configFilepath";
    public static final String EXIST_PROFILE_NAME_LIST = "profileNameList";
    String my1Profile = "sdcard/myprofile/my1_scanner_property.xml";
    String my2Profile = "sdcard/myprofile/my2_scanner_property.xml";
    String byFileproviderProfile = "sdcard/myprofile/Default_scanner_property.xml";
    TextView uriShare;
    //introduced in V2.1.18_20221124
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = new Bundle();
            String resultInfo = "";
            int command = intent.getIntExtra(IES_CONFIG_ACTION, IES_CONFIG_ACTION_UNKNOWN);
            int Result = intent.getIntExtra(RESULT_CODE, UNKNOWN);
            if(intent.hasExtra(RESULT_INFO_EXTRA)){
                bundle = intent.getBundleExtra(RESULT_INFO_EXTRA);
                Set<String> keys = bundle.keySet();
                for (String key: keys) {
                    resultInfo += key + ": "+bundle.getString(key) + "\n";
                }
            }

            if(intent.hasExtra(EXIST_PROFILE_NAME_LIST)){
                String[] existProfileNameList =intent.getStringArrayExtra(EXIST_PROFILE_NAME_LIST);
                if(existProfileNameList != null) {
                    resultInfo += "Exist Profile Name: ";
                    for (String key: existProfileNameList) {
                        resultInfo += "[ "+ key + " ]";
                    }
                }
            }
            String text = "Command: "+(command == IES_CONFIG_ACTION_IMPORT ? "Import" : "Export")+"\n" +
                    "Result: " +(Result >=0 ? "SUCCESS" : "FAILURE")+"\n"+
                    "Result Info: " +resultInfo + "\n";
            uriShare.setText(text);
            if(Result >=0 && command == IES_CONFIG_ACTION_IMPORT) {
                Intent intentService = new Intent(INTENT_DEPLOYMENT_SWITCH_ACTION);
                intentService.addFlags(0x01000000);//Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND
                String profileName = bundle.getString(IES_CONFIG_PROFILE_NAME);
                if(TextUtils.isEmpty(profileName) == false) {
                    intentService.putExtra(IES_CONFIG_PROFILE_NAME, profileName);
                    //sendBroadcast(intentService);
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uriShare = (TextView) findViewById(R.id.uriShare);
        Button shareXml = (Button) findViewById(R.id.button);

        final String intentkey = "com.ubx.datawedge.IMPORT_FILEPROVIDER";//introduced in V2.1.17_20220922
        shareXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intentService = new Intent(intentkey);
                    Intent eintent = getExplicitIntent(MainActivity.this, intentService);
                    if(eintent == null) {
                        Toast.makeText(MainActivity.this, "no found service package", Toast.LENGTH_SHORT).show();
                        ComponentName component = new ComponentName("com.ubx.datawedge", "com.ubx.datawedge.service.ImportExoprtService");
                        eintent = intentService;
                        eintent.setComponent(component);

                    }

                    File file = new File(byFileproviderProfile);
                    Uri uri;
                    if(Build.VERSION.SDK_INT > 24){
                        uri = FileProvider.getUriForFile(MainActivity.this,"com.ubx.sharecontent",file);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    if(uri != null)
                        uriShare.setText(uri.toString());
                    eintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    eintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    eintent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    eintent.setDataAndType(uri,"text/plain");
                    //startActivity(intent);
                    startService(eintent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Button shareBroadcast = (Button) findViewById(R.id.button2);
        shareBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentService = new Intent("com.ubx.datawedge.IMPORT_FILEPROVIDER_CONFIG");
                /*Intent eintent = getExplicitIntent(MainActivity.this, intentService);
                if(eintent == null) eintent = intentService;*/
                //ComponentName component = new ComponentName("com.ubx.datawedge", "com.ubx.datawedge.service.ImportExoprtService");
                //intentService.setComponent(component);
                File file = new File(byFileproviderProfile);
                Uri uri;
                if(Build.VERSION.SDK_INT > 24){
                    uri = FileProvider.getUriForFile(MainActivity.this,"com.ubx.sharecontent",file);
                } else {
                    uri = Uri.fromFile(file);
                }
                if(uri != null)
                    uriShare.setText(uri.toString());
                intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentService.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intentService.addFlags(0x01000000);//Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND
                intentService.setDataAndType(uri,"text/plain");
                sendBroadcast(intentService);
            }
        });
        Button ByServiceImport = (Button) findViewById(R.id.button3);
        ByServiceImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intentService = new Intent(INTENT_SERVICE_ACTION);
                    Intent eintent = getExplicitIntent(MainActivity.this, intentService);
                    if(eintent == null) {
                        Toast.makeText(MainActivity.this, "no found service package", Toast.LENGTH_SHORT).show();
                        ComponentName component = new ComponentName("com.ubx.datawedge", "com.ubx.datawedge.service.ImportExoprtService");
                        eintent = intentService;
                        eintent.setComponent(component);

                    }

                    File file = new File(my1Profile);
                    if(file.exists()) {
                        eintent.putExtra(IES_CONFIG_ACTION, IES_CONFIG_ACTION_IMPORT);
                        eintent.putExtra(IES_CONFIG_PROFILE_PATH, my1Profile);
                        startService(eintent);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        Button ByServiceExport = (Button) findViewById(R.id.button4);
        ByServiceExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intentService = new Intent(INTENT_SERVICE_ACTION);
                    Intent eintent = getExplicitIntent(MainActivity.this, intentService);
                    if(eintent == null) {
                        Toast.makeText(MainActivity.this, "no found service package", Toast.LENGTH_SHORT).show();
                        ComponentName component = new ComponentName("com.ubx.datawedge", "com.ubx.datawedge.service.ImportExoprtService");
                        eintent = intentService;
                        eintent.setComponent(component);

                    }
                    eintent.putExtra(IES_CONFIG_ACTION, IES_CONFIG_ACTION_EXPORT);
                    eintent.putExtra(IES_CONFIG_PROFILE_NAME, "my1");//export to sdcard/my1_scanner_property.xml
                    startService(eintent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        Button ByBroadcastImport = (Button) findViewById(R.id.button5);
        ByBroadcastImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intentService = new Intent("action.IMPORT_SCANNER_CONFIG");
                    //ComponentName component = new ComponentName("com.ubx.datawedge", "com.ubx.datawedge.service.ImportExoprtReceiver");
                    //intentService.setComponent(component);
                    intentService.addFlags(0x01000000);//Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND
                    File file = new File(my2Profile);
                    if(file.exists()) {
                        intentService.putExtra(IES_CONFIG_PROFILE_PATH, my2Profile);
                        sendBroadcast(intentService);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        Button ByBroadcastExport = (Button) findViewById(R.id.button6);
        ByBroadcastExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intentService = new Intent("action.EXPORT_SCANNER_CONFIG");
                    intentService.addFlags(0x01000000);//Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND
                    intentService.putExtra(IES_CONFIG_PROFILE_NAME, "my2");//export to sdcard/my2_scanner_property.xml
                    sendBroadcast(intentService);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        requestPermissions(99);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_DEPLOYMENT_RESULT_ACTION);
        registerReceiver(mScanReceiver, filter);
    }
    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
    // 请求权限
    public void requestPermissions(int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                ArrayList<String> requestPerssionArr = new ArrayList<>();
                /*int hasCamrea = checkSelfPermission(Manifest.permission.CAMERA);
                if (hasCamrea != PackageManager.PERMISSION_GRANTED) {
                    requestPerssionArr.add(Manifest.permission.CAMERA);
                }*/

                int hasSdcardRead = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                if (hasSdcardRead != PackageManager.PERMISSION_GRANTED) {
                    requestPerssionArr.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                int hasSdcardWrite = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasSdcardWrite != PackageManager.PERMISSION_GRANTED) {
                    requestPerssionArr.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                // 是否应该显示权限请求
                if (requestPerssionArr.size() >= 1) {
                    String[] requestArray = new String[requestPerssionArr.size()];
                    for (int i = 0; i < requestArray.length; i++) {
                        requestArray[i] = requestPerssionArr.get(i);
                    }
                    requestPermissions(requestArray, requestCode);
                } else {
                    copyAssetsToDst(this, "myprofile","myprofile");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                flag = true;
            }
        }
        copyAssetsToDst(this, "myprofile","myprofile");
    }
    private void copyAssetsToDst(Context context, String srcPath, String dstPath) {
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File("sdcard/", dstPath);
                if (!file.exists()) file.mkdirs();
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    } else { // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File("sdcard/", dstPath);
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}