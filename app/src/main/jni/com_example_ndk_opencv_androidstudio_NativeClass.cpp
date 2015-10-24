#include <com_example_ndk_opencv_androidstudio_NativeClass.h>

#include <opencv2/core/core.hpp>
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;

void detect(cv::Mat &, cv::CascadeClassifier &, cv::CascadeClassifier &, cv::CascadeClassifier &);
void detectFaces(std::vector<cv::Rect> &, cv::Mat &, cv::CascadeClassifier &);
void detectEyes(cv::Rect &, cv::Mat &, cv::Mat &, cv::CascadeClassifier &);
void detectSmile(cv::Rect &, cv::Mat &, cv::Mat &, cv::CascadeClassifier &);

std::string const static EXTERNAL_STORAGE_BASE_PATH = "/storage/emulated/0/";
std::string const static FACE_CASCADE_NAME = "haarcascade_frontalface_alt.xml";
std::string const static EYES_CASCADE_NAME = "haarcascade_eye.xml";
std::string const static SMILE_CASCADE_NAME = "haarcascade_smile.xml";



bool setup = false;



JNIEXPORT jint JNICALL Java_com_example_ndk_1opencv_1androidstudio_NativeClass_processMat(JNIEnv * env, jobject obj, jlong adress)
{
    cv::Mat & frame = * (cv::Mat *) adress;

    cv::CascadeClassifier static face_cascade;
    cv::CascadeClassifier static eyes_cascade;
    cv::CascadeClassifier static smile_cascade;

    if(!setup)
    {
        if(!face_cascade.load(EXTERNAL_STORAGE_BASE_PATH + FACE_CASCADE_NAME)) { return -1; };
        if(!eyes_cascade.load(EXTERNAL_STORAGE_BASE_PATH + EYES_CASCADE_NAME)) { return -1; };
        if(!smile_cascade.load(EXTERNAL_STORAGE_BASE_PATH + SMILE_CASCADE_NAME)) { return -1; };

        setup = !setup;

        return 1;
    }
    else
    {
        detect(frame, face_cascade, eyes_cascade, smile_cascade);

        return 0;
    }
}

void detect(cv::Mat & frame, cv::CascadeClassifier & face_cascade, cv::CascadeClassifier & eyes_cascade, cv::CascadeClassifier & smile_cascade)
{
    cv::Mat frame_gray;
    cv::cvtColor(frame, frame_gray, cv::COLOR_BGR2GRAY);
    cv::equalizeHist(frame_gray, frame_gray);

    std::vector<cv::Rect> faces;

    detectFaces(faces, frame_gray, face_cascade);

    for(std::vector<cv::Rect>::iterator it = faces.begin(); it != faces.end(); ++it)
    {
        detectEyes(* it, frame, frame_gray, eyes_cascade);
        detectSmile(* it, frame, frame_gray, smile_cascade);
    }
}

/**
 * \brief detects faces in image
 *
 * @param[out] faces a data structure holding data of found faces
 * @param[in] frame holds the current camera image
 * @param[in] classifier detects faces in images
 *
 * retrieves all faces in an image and saves them to a given data structure
 *
 * @see detect()
 */
void detectFaces(std::vector<cv::Rect> & faces, cv::Mat & frame, cv::CascadeClassifier & classifier)
{
    classifier.detectMultiScale(frame, faces, 1.1, 2, 2, cv::Size(30, 30));
}

/**
 * \brief detects eyes in an image
 *
 * @param[in] face represents the current face to detect eyes in
 * @param[out] frame holds the data of the current image
 * @param[in] frame_gray contains the equalized grayscale image of the current image
 * @param[in] classifier detects eyes in images
 *
 * retrieves all eyes detected in a face and highlights them in the current camera image
 *
 * @see detect()
 */
void detectEyes(cv::Rect & face, cv::Mat & frame, cv::Mat & frame_gray, cv::CascadeClassifier & classifier)
{
    std::vector<cv::Rect> eyes;

    cv::Point center(face.x + face.width / 2, face.y + face.height / 2);
    cv::ellipse(frame, center, cv::Size(face.width / 2, face.height / 2 ), 0, 0, 360, cv::Scalar(0, 255, 255), 4, 8, 0);

    cv::Mat faceROI = frame_gray(face);

    classifier.detectMultiScale(faceROI, eyes, 1.1, 20, 0 | cv::CASCADE_SCALE_IMAGE, cv::Size(30, 30));

    for(std::vector<cv::Rect>::iterator it = eyes.begin(); it != eyes.end(); ++it)
    {
        cv::Point eye_center(face.x + (* it).x + (* it).width / 2, face.y + (* it).y + (* it).height / 2);

        int radius = cvRound(((* it).width + (* it).height) * 0.25);

        cv::circle(frame, eye_center, radius, cv::Scalar(0, 0, 255), 4, 8, 0);
    }
}

/**
 * \brief detects smile in an image
 *
 * @param[in] face represents the current face to detect smiles in
 * @param[out] frame holds the data of the current image
 * @param[in] frame_gray contains the equalized grayscale image of the current image
 * @param[in] classifier detects smiles in images
 *
 * retrieves all smiles detected in a face and highlights them in the current camera image
 *
 * @see detect()
 */
void detectSmile(cv::Rect & face, cv::Mat & frame, cv::Mat & frame_gray, cv::CascadeClassifier & classifier)
{
    std::vector<cv::Rect> smile;

    cv::Mat faceROI = frame_gray(face);

    classifier.detectMultiScale(faceROI, smile, 1.1, 125, 0 | cv::CASCADE_SCALE_IMAGE, cv::Size(30, 30));

    for(std::vector<cv::Rect>::iterator it = smile.begin(); it != smile.end(); ++it)
    {
        cv::Point smileCenter(face.x + (* it).x + (* it).width / 2, face.y + (* it).y + (* it).height / 2);
        cv::ellipse(frame, smileCenter, cv::Size((* it).width / 2, (* it).height / 2), 0, 0, 360, cv::Scalar(0, 255, 0), 4, 8, 0);
    }
}