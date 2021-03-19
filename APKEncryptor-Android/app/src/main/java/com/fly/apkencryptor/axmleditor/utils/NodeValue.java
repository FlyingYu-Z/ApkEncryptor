package com.fly.apkencryptor.axmleditor.utils;

/**
 * Created by zl on 15/9/9.
 */
public final class NodeValue {
    private NodeValue(){}

    public static final String NAME="name";
    public static final String VALUE="value";
    public static final String LABEL="label";

    public static final class Manifest{
        public static final String VERSION_CODE="versionCode";
        public static final String VERSION_NAME="getVersionName";
        public static final String INSTALL_LOCATION="installLocation";
        public static final String SHARDE_USER_ID="sharedUserId";
        public static final String SHARED_USER_LABEL="sharedUserLabel";
        public static final String PACKAGE="package";
    }

    public static final class UsesSDK{
        public static final String MAX_SDK_VERSION="maxSdkVersion";
        public static final String MIN_SDK_VERSION="minSdkVersion";
        public static final String TARGET_SDK_VERSION="targetSdkVersion";
    }

    public static final class UsesPermission{
        public static final String NAME="name";
        public static final String MAX_SDK_VERSION="maxSdkVersion";
    }

    public static final class MetaData{
        public static final String NAME="name";
        public static final String VALUE="value";
        public static final String RESOURCE="resource";
    }


    public static final class Application{
        public static final String NAME="name";
        public static final String VALUE="value";
        public static final String LABEL="label";
        public static final String theme="theme";
        public static final String icon="icon";
        public static final String persistent="persistent";
        public static final String allowBackup="allowBackup";
        public static final String largeHeap="largeHeap";
        public static final String debuggable="debuggable";
        public static final String hardwareAccelerated="hardwareAccelerated";
        public static final String fullBackupOnly="fullBackupOnly";
        public static final String vmSafeMode="vmSafeMode";
        public static final String enabled="enabled";
        public static final String description="description";
        public static final String process="process";
    }


}
