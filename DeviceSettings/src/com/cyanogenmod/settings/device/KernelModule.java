package com.cyanogenmod.settings.device;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.content.Context;
import android.view.*;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.File;

import com.cyanogenmod.settings.device.R;

public class KernelModule extends Fragment {

    public static String TAG = "Module Switcher";
    private static final String SEC_DOC_KEYBOARD = "sec_dock_keyboard";
    public static final String SEC_DOC_KEYBOARD_PATH = "/system/lib/modules/sec_dock_keyboard.ko";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, root, savedInstanceState);

        View view = inflater.inflate(R.layout.kernel_module, root, false);

        if (!new File(SEC_DOC_KEYBOARD_PATH).exists()) {
            Toast.makeText(getActivity(), SEC_DOC_KEYBOARD + " module does not exist", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        SU su = new SU();
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
        checkBox.setChecked(isModuleActive(SEC_DOC_KEYBOARD, su));
        su.close();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SU su = new SU();
                if (isChecked) insmod(SEC_DOC_KEYBOARD_PATH, su);
                else rmmod(SEC_DOC_KEYBOARD, su);
                su.close();
                saveConfig(isChecked);
            }
        });
        return view;
    }

    private boolean isModuleActive(String module, SU su) {
        return su.runCommand("lsmod | grep module").contains(module);
    }

    private void insmod(String module, SU su) {
        su.runCommand("insmod " + module);
    }

    private void rmmod(String module, SU su) {
        su.runCommand("rmmod " + module);
    }

    private void saveConfig(boolean active) {
        getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().putBoolean("moduleactive", active).apply();
    }

}
