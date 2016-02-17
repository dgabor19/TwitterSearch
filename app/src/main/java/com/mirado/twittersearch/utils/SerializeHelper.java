package com.mirado.twittersearch.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by gabordudas on 11/02/15.
 * Copyright (c) 2016 TwitterSearch. All rights reserved.
 */
public class SerializeHelper {
    private static final String EXTENSION = ".ser";
    public static String TAG = SerializeHelper.class.getSimpleName();

    public interface DeserializeCallback<T> {
        void onDeserialize(T response);
    }

    /**
     * Creates the file in app specific folder (/data/data/se.tv4.vader/files/)
     *
     * @param context
     * @param response
     * @param concatToFileName
     * @param <T>
     */
    public static <T> void serialize(final Context context, final T response, final String concatToFileName) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        String file = "";

        if (response == null) {
            return;
        }

        try {
            if (concatToFileName != null) {
                file = response.getClass().getSimpleName() + concatToFileName + EXTENSION;
            } else {
                file = response.getClass().getSimpleName() + EXTENSION;
            }
            Log.d(TAG, "Serialization file : " + file);
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(response);
            Log.d(TAG, "Serialization Successful : " + file);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "FileNotFoundException : " + e.toString());
//            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Exception : " + e.toString());
//            e.printStackTrace();
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {

            }
        }
    }

    public interface OnSerializeCallback<T> {
        public void onFinish(T object);
    }

    /**
     * Retunrs the de-serialized locations.
     *
     * @param context - context
     * @return - ArrayList of locations.
     */
    public static <T> T deserialize(final Context context, Class<T> clazz, final String concatToFileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        String file = "";
        T response = null;
        try {
            if (concatToFileName != null) {
                file = clazz.getSimpleName() + concatToFileName + EXTENSION;
            } else {
                file = clazz.getSimpleName() + EXTENSION;
            }

            Log.d(TAG, "DeSerialization file : " + file);
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            response = (T) ois.readObject();

            Log.d(TAG, "DeSerialization successful : " + file);
        } catch (Exception e) {
            Log.d(TAG, "Exception : " + e.toString());
//            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return response;
    }

    /**
     * Removes the serialized file
     * @param context
     * @param clazz
     * @param concatToFileName
     * @param <T>
     * @return
     */
    public static <T> boolean deleteFile(final Context context, Class<T> clazz, final String concatToFileName) {
        String filename = "";
        String path = null;
        boolean result = false;

        if (concatToFileName != null) {
            filename = clazz.getSimpleName() + concatToFileName + EXTENSION;
        } else {
            filename = clazz.getSimpleName() + EXTENSION;
        }
        if (context != null) {
            path = context.getFilesDir().getAbsolutePath() + "/" + filename;
        }

        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                result = file.delete();
            }
        }

        return result;
    }
}

