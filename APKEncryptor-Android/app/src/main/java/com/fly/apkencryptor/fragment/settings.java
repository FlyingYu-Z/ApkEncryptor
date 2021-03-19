package com.fly.apkencryptor.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.fly.apkencryptor.R;
import com.fly.apkencryptor.activity.MainActivity;
import com.fly.apkencryptor.application.MyApp;
import com.fly.apkencryptor.dialog.SetKeyStore;
import com.fly.apkencryptor.utils.LanguageUtil;

public class settings extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_settings);

        EditTextPreference ed_confKey=(EditTextPreference)findPreference("confKey");
        ed_confKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if ("confKey".equals(preference.getKey())) {
                    new SetKeyStore(getActivity());
                }

                ed_confKey.getDialog().dismiss();
                return true;
            }
        });


        Preference languagePreference=findPreference("pre_settings_language");
        languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object obj) {


                String language=obj.toString();
                changeLanguage(language);
                return true;
            }
        });


    }


    /**
     * 如果是7.0以下，我们需要调用changeAppLanguage方法，
     * 如果是7.0及以上系统，直接把我们想要切换的语言类型保存在SharedPreferences中,然后重新启动MainActivity即可
     * @param language
     */
    private void changeLanguage(String language) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(MyApp.getContext(), language);
        }
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        return false;
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {

        //ToastUtils.show("test");

        return true;
    }




}
