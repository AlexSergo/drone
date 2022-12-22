package com.example.dronevision.utils;

import android.app.Activity;
import android.content.res.Resources;

import com.example.dronevision.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class HgtLoader {

    public HgtLoader(Resources r) {
        writeToFile("data/data/com.example.dronevision/files/n55e037.hgt", getStringFromRawFile(r));
    }

    private byte[] getStringFromRawFile(Resources r) {
        //Resources r = activity.getResources();
        InputStream is = r.openRawResource(R.raw.n55e037);
        byte[] myText = null;
        try {
            myText = convertStreamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myText;
    }

    private byte[]  convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = is.read();
        while( i != -1)
        {
            baos.write(i);
            i = is.read();
        }
        return baos.toByteArray();
    }

    private void writeToFile(String filename, byte[] data) {
        Path p;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            p = Paths.get(".", filename);
            try (OutputStream os = new BufferedOutputStream(
                    Files.newOutputStream(p, StandardOpenOption.CREATE))) {
                os.write(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
