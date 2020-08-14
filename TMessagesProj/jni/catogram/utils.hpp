#ifndef ASSCRACKINGPREVENTION_UTILS_HPP
#define ASSCRACKINGPREVENTION_UTILS_HPP

#include <jni.h>
#include <array>
#include <string>
#include <sstream>
#include <vector>
#include <sys/system_properties.h>

using namespace std;

__attribute__((always_inline))
inline string jStringToString(JNIEnv *env, jstring jString) {
    if (!jString)
        return "";
    auto stringChars = env->GetStringUTFChars(jString, nullptr);
    string result(stringChars);
    env->ReleaseStringUTFChars(jString, stringChars);
    return result;
}

__attribute__((always_inline))
inline jobject getContext(JNIEnv *env) {
    jclass activityThreadCls = env->FindClass("android/app/ActivityThread");
    jmethodID currentActivityThreadMtd =
            env->GetStaticMethodID(activityThreadCls, "currentActivityThread", "()Landroid/app/ActivityThread;");
    jobject activityThread = env->CallStaticObjectMethod(activityThreadCls, currentActivityThreadMtd);

    jmethodID getApplicationMtd = env->GetMethodID(activityThreadCls, "getApplication", "()Landroid/app/Application;");
    return env->CallObjectMethod(activityThread, getApplicationMtd);
}

__attribute__((always_inline))
inline int sdkVersion() {
    char osVersion[PROP_VALUE_MAX + 1];
    __system_property_get("ro.build.version.sdk", osVersion);
    return atoi(osVersion);
}

__attribute__((always_inline))
inline int random(int start, int range) {
    return (rand() % range) + start;
}

vector<string> split(const string &s, char delimiter) {
    vector<string> result;
    stringstream ss(s);
    string item;
    while (getline(ss, item, delimiter))
        result.push_back(item);
    return result;
}

__attribute__((always_inline))
inline string shell(const string &cmd) {
    array<char, 128> buffer{};
    string result;
    unique_ptr<FILE, decltype(&pclose)> pipe(popen(cmd.c_str(), "r"), pclose);
    while (fgets(buffer.data(), buffer.size(), pipe.get()) != nullptr)
        result += buffer.data();
    return result;
}

__attribute__((always_inline))
inline string randomString(int length) {
    string result;
    while (result.size() != length)
        result += (char) ((rand() % 26) + 97);
    return result;
}

#endif //ASSCRACKINGPREVENTION_UTILS_HPP