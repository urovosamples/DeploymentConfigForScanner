**Deployment**

    ScanWedge configurations can be deployed to devices either manually or via mass deployment. There are 2 methods for secure mass deployment: UHome or Soti MobiControl (MDM) etc.

**1.Prerequisites**

Generate ScanWedge configuration file(s) as follows:

    (1).Configure ScanWedge as desired on a device.
    (2).Create a ScanWedge profile, specifying how to acquire, process, associated app and output data.
    (3).To distribute ScanWedge configurations including multiple profiles and settings, export the config file (MultipleProfile_scanner_property.xml).
    (4).To distribute a single ScanWedge profile, export the profile ([ProfileName]_scanner_property.xml, where [ProfileName] is the name of the profile).

**2. Deployment Methods**

After fulfilling the prerequisites to generate the ScanWedge configuration file(s), select one of the following methods of deployment.

2.1 Manual Deployment

To deploy the ScanWedge configuration file manually:

    (1).Copy the ScanWedge configuration file to the target device, e.g. the SD card folder.
    (2).Import the file by browsing to the folder location and selecting the file.
    

2.2 Mass Deployment

There are 2 methods for mass deployment of ScanWedge configurations:

    Using UHome or Soti MobiControl (MDM) - create a UHome profile and generate the barcode to scan and deploy the configuration:

    Host the ScanWedge configuration file on an FTP or HTTP server.
    Create a UHome profile to:
    2.2.1. Select one of the following:
         (1) If using the device boot completed auto import, copy the configuration file from the host server to the auto import folder on the device as follows: Using File Manager, specify the source file path, the path of the configuration file on the host server. Specify the target full path and file name, using the Auto Import feature: /sdcard/autoimport_scanner_property.xml.To automatically import the configuration when the device next reboot completed.
         (2) If using a specific folder location, copy the configuration file from a specific location accessible by ScanWedge as follows: Using File Manager, specify the source file path, the path of the configuration file on the host server. Using ScanWedge Intent API, import the configuration file from a specified path.
         (3) If using auto import, copy the configuration file from the host server to the auto import folder on the device as follows: Using File Manager, specify the source file path, the path of the configuration file on the host server. Specify the target file path, using the Auto Import feature: /sdcard/enterprise/scanwedge/autoimport.introduced in ScanWedge V2.1.17_20221024
    2.2.2. Generate the staging barcode.
    Scan the generated barcode with UHome client on the device.


2.3 Using ScanWedge Intent API　Import configuration file from specified path

Specifies the full path and file name to import the configuration file (scanner_property.xml) or profile (by default, [ProfileName]_scanner_property.xml). Can be used instead of the default Auto-Import (/sdcard/autoimport_scanner_property.xml) file. The file must adhere to the existing ScanWedge file naming or content convention:

    Config file is always named Default_scanner_property.xml
    Profile naming convention(Not Required): [ProfileName]_scanner_property.xml
    Profile content convention,Main Configurations TAG (not an exhaustive list),All TAG parameters are case sensitive:
    <propertygrouplist></propertygrouplist> This TAG indicates that the content of the xml file contains multiple Profile.introduced in ScanWedge V2.1.17_20221024
    <propertygroup></propertygroup> This TAG indicates the beginning and ending of a Profile.
    <profileName> This TAG indicates a Profile's name.Replace to Default profle if no contains the profileName tag.
    <scanwedgeEnable>true</scanwedgeEnable> This TAG indicates whether the ScanWedge function is enabled.
    <profileEnable> This TAG indicates whether the Profile is enabled.
    <profilePackages packageName="com.android.chrome">*</profilePackages> Associate the [profileName]Profile with App(s) and/or Activities. the asterisk(*) to associate all app activities with the Profile,or add an indvidual activity to use the [profileName]Profile for that specific activity only.A single profile can be associated with one or more activities or apps. However, an activity or app can be associated with only one profile.
    <property id="6" name="SEND_GOOD_READ_BEEP_ENABLE">2</property>Scanner Parameters:id,name,value;A list of parameters allowed to be modified only in a [profileName] file.Refer to SDK doc[SDK doc](https://github.com/urovosamples/SDK_ReleaseforAndroid)  for more information on decoders, decoder parameters, and scan parameters.
*Important: Support for decode parameters can vary depending on the selected scanning device. For device-specific support notes, please refer to the Integrator Guide that accompanied the unit.*

    2.3.1 a single ScanWedge profile
        (1).Compatible with all versions

        <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
        <propertygroup>
            <profileName>cale</profileName>
            <scanwedgeEnable>true</scanwedgeEnable>
            <profileEnable>true</profileEnable>
            <profilePackages packageName="com.android.chrome">*</profilePackages>
            <profilePackages packageName="com.android.mms">com.android.mms.ui.ComposeMessageActivity</profilePackages>
            <property id="6" name="SEND_GOOD_READ_BEEP_ENABLE">2</property>
            <property id="200000" name="WEDGE_INTENT_ACTION_NAME">ACTION_DECODE_DATA</property>
            <property id="200002" name="INTENT_DATA_STRING_TAG">data</property>
        </propertygroup>
        (2).introduced the <propertygrouplist> tag in ScanWedge V2.1.17_20221024 
        <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
        <propertygrouplist>
            <propertygroup>
                <profileName>cale</profileName>
                <scanwedgeEnable>true</scanwedgeEnable>
                <profileEnable>true</profileEnable>
                <profilePackages packageName="com.android.chrome">*</profilePackages>
                <profilePackages packageName="com.ubx.usettings">*</profilePackages>
                <profilePackages packageName="com.android.mms">com.android.mms.ui.ComposeMessageActivity</profilePackages>
                <property id="6" name="SEND_GOOD_READ_BEEP_ENABLE">2</property>
                <property id="200000" name="WEDGE_INTENT_ACTION_NAME">ACTION_DECODE_DATA</property>
                <property id="200002" name="INTENT_DATA_STRING_TAG">data</property>
            </propertygroup>
        </propertygrouplist>
    2.3.2 multiple ScanWedge profile.introduced in ScanWedge V2.1.17_20221024
        <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
        <propertygrouplist>
            <propertygroup>
                <profileName>cale</profileName>
                <scanwedgeEnable>true</scanwedgeEnable>
                <profileEnable>true</profileEnable>
                <profilePackages packageName="com.android.chrome">*</profilePackages>
                <profilePackages packageName="com.android.mms">com.android.mms.ui.ComposeMessageActivity</profilePackages>
                <property id="6" name="SEND_GOOD_READ_BEEP_ENABLE">2</property>
                <property id="200000" name="WEDGE_INTENT_ACTION_NAME">ACTION_DECODE_DATA</property>
                <property id="200002" name="INTENT_DATA_STRING_TAG">data</property>
            </propertygroup>
            <propertygroup>
                <profileName>mms</profileName>
                <scanwedgeEnable>true</scanwedgeEnable>
                <profileEnable>true</profileEnable>
                <property id="0" name="SCANNER_ENABLE">1</property>
                <property id="6" name="SEND_GOOD_READ_BEEP_ENABLE">1</property>
                <property id="7" name="SEND_GOOD_READ_VIBRATE_ENABLE">1</property>
                <property id="8" name="TRIGGERING_MODES">4</property>
                <property id="9" name="GOOD_READ_BEEP_ENABLE">0</property>
            </propertygroup>
        </propertygrouplist>    


**ScanWedge API Import routine:**

    Import the new file(s)
    Replace the existing Config file and like-named Profile(s) (if any)
    Delete the imported files(if /sdcard/autoimport_scanner_property.xml or /sdcard/enterprise/scanwedge/autoimport)
    Put new settings immediately into effect(if profileName is Default)

**3. Intent API**
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
   //ScanWedge was unable to read the specified database file
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
    **3.1 Import Config**
**Function Prototype**
Intent intentService = new Intent("action.IMPORT_SCANNER_CONFIG");
intentService.addFlags(0x01000000);//Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND
File file = new File(my2Profile);
if(file.exists()) {
intentService.putExtra(IES_CONFIG_PROFILE_PATH, my2Profile);
sendBroadcast(intentService);
}
***Parameters***

ACTION [String]:"action.IMPORT_SCANNER_CONFIG"

EXTRA_DATA [String]: IES_CONFIG_PROFILE_PATH

***Result Bundle***
RESULT_CODE [int]:  > 0 is SUCCESS  or  other is FAILURE
RESULT_INFO_EXTRA[Bundle]
EXIST_PROFILE_NAME_LIST[String[]] return all profile lists.
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
**3.2 Export Config**
***Parameters***

ACTION [String]:"action.EXPORT_SCANNER_CONFIG"

EXTRA_DATA [String]: IES_CONFIG_PROFILE_NAME export profile name.

Intent intentService = new Intent("action.EXPORT_SCANNER_CONFIG");
intentService.addFlags(0x01000000);//Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND
intentService.putExtra(IES_CONFIG_PROFILE_NAME, "my2");
sendBroadcast(intentService);