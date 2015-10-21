package com.example.ndk_opencv_androidstudio;

public class NativeClass {
    public native static String getStringFromNative();
    public native static int processMat(long address);
}
