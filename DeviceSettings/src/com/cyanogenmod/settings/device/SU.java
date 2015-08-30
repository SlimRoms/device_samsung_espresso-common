package com.cyanogenmod.settings.device;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by willi on 30.08.15.
 */
public class SU {

    private static final String TAG = "GalaxyTab2Settings_Su";
    private Process process;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    public SU() {
        try {
            Log.i(TAG, "SU initialized");
            process = Runtime.getRuntime().exec("su");
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (IOException e) {
            Log.e(TAG, "Failed to run shell as su");
        }
    }

    public synchronized String runCommand(final String command) {
        try {
            StringBuilder sb = new StringBuilder();
            String callback = "/shellCallback/";
            bufferedWriter.write(command + "\necho " + callback + "\n");
            bufferedWriter.flush();

            int i;
            char[] buffer = new char[256];
            while (true) {
                sb.append(buffer, 0, bufferedReader.read(buffer));
                if ((i = sb.indexOf(callback)) > -1) {
                    sb.delete(i, i + callback.length());
                    break;
                }
            }
            return sb.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            bufferedWriter.write("exit\n");
            bufferedWriter.flush();

            process.waitFor();
            Log.i(TAG, "SU closed: " + process.exitValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
