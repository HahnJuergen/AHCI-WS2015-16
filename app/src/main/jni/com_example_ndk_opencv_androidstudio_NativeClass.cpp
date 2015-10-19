#include <com_example_ndk_opencv_androidstudio_NativeClass.h>
#include "opencv2/opencv.hpp"
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <string>

using namespace std;
using namespace cv;

JNIEXPORT jstring JNICALL Java_com_example_ndk_1opencv_1androidstudio_NativeClass_getStringFromNative(JNIEnv * env, jobject obj)
{

    Mat frame(17,7, CV_32FC2, Scalar(1,1));
    char buf[128];
    snprintf(buf, sizeof(buf), "%d", frame.cols);

    return env->NewStringUTF(buf);
}