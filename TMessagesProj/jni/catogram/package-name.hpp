#ifndef ASSCRACKINGPREVENTION_PACKAGE_NAME_HPP
#define ASSCRACKINGPREVENTION_PACKAGE_NAME_HPP

#include <jni.h>
#include <string>
#include <vector>
#include <unistd.h>

#include "config.hpp"

string getPackageNameFromContext(JNIEnv *env) {
    jobject context = getContext(env);
    jclass contextCls = env->GetObjectClass(context);
    jmethodID getPackageNameMtd = env->GetMethodID(contextCls, "getPackageName", "()Ljava/lang/String;");
    auto packageName = static_cast<jstring>(env->CallObjectMethod(context, getPackageNameMtd));
    return jStringToString(env, packageName);
}

string getPackageNameFromBuildConfig(JNIEnv *env) {
    jclass buildConfigCls = env->FindClass((packageSlashed + "/BuildConfig").c_str());
    jfieldID appIdFld = env->GetStaticFieldID(buildConfigCls, "APPLICATION_ID", "Ljava/lang/String;");
    auto appId = static_cast<jstring>(env->GetStaticObjectField(buildConfigCls, appIdFld));
    return jStringToString(env, appId);
}

string getPackageNameFromActivityThreadPackageName(JNIEnv *env) {
    jclass activityThreadCls = env->FindClass("android/app/ActivityThread");
    jmethodID currentPackageNameMtd =
            env->GetStaticMethodID(activityThreadCls, "currentPackageName", "()Ljava/lang/String;");
    auto packageName = static_cast<jstring>(env->CallStaticObjectMethod(activityThreadCls, currentPackageNameMtd));
    return jStringToString(env, packageName);
}

string getPackageNameFromActivityThreadProcessName(JNIEnv *env) {
    jclass activityThreadCls = env->FindClass("android/app/ActivityThread");
    jmethodID currentPackageNameMtd =
            env->GetStaticMethodID(activityThreadCls, "currentProcessName", "()Ljava/lang/String;");
    auto packageName = static_cast<jstring>(env->CallStaticObjectMethod(activityThreadCls, currentPackageNameMtd));
    return jStringToString(env, packageName);
}

string getPackageNameFromShellPs() {
    auto pid = to_string(getpid());
    auto ps = shell("ps");
    auto lines = split(ps, '\n');

    for (const string &line : lines)
        if (line.find(pid) != string::npos)
            return line.substr(line.find_last_of(' ') + 1);

    return "";
}

#endif //ASSCRACKINGPREVENTION_PACKAGE_NAME_HPP
