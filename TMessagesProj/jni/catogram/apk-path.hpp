#ifndef ASSCRACKINGPREVENTION_APK_PATH_HPP
#define ASSCRACKINGPREVENTION_APK_PATH_HPP

#include <jni.h>
#include <string>
#include <vector>
#include <unistd.h>
#include <fstream>

#include "config.hpp"

using namespace std;

__attribute__((always_inline))
inline string checkAppDir(const string &path) {
    bool valid = true;

    if (path.find("/../") != string::npos)
        valid = false;

    if (path.find("/mnt/expand/") == 0) {
        auto slashPos = path.find('/', 12);
        auto appPos = path.find("app/", slashPos);
        if (appPos - slashPos != 1)
            valid = false;
    } else if (path.find("/data/app") != 0)
        valid = false;

    return valid ? path : randomString(10);
}

__attribute__((always_inline))
inline string extractApkPathFromAppInfo(JNIEnv *env, jobject appInfo) {
    jclass appInfoCls = env->GetObjectClass(appInfo);
    auto publicSrcDirFld = env->GetFieldID(appInfoCls, "publicSourceDir", "Ljava/lang/String;");
    auto srcDirFld = env->GetFieldID(appInfoCls, "sourceDir", "Ljava/lang/String;");
    auto publicSrcDir = jStringToString(env, static_cast<jstring>(env->GetObjectField(appInfo, publicSrcDirFld)));
    auto srcDir = jStringToString(env, static_cast<jstring>(env->GetObjectField(appInfo, srcDirFld)));

    if (srcDir != publicSrcDir)
        return randomString(10);

    return checkAppDir(publicSrcDir);
}

string getApkPathFromAppInfoFromContext(JNIEnv *env) {
    jobject context = getContext(env);
    jclass contextCls = env->GetObjectClass(context);
    jmethodID getApplicationInfoMtd =
            env->GetMethodID(contextCls, "getApplicationInfo", "()Landroid/content/pm/ApplicationInfo;");
    auto appInfo = env->CallObjectMethod(context, getApplicationInfoMtd);
    return extractApkPathFromAppInfo(env, appInfo);
}

string getApkPathFromAppInfoFromPackageManager(JNIEnv *env) {
    jobject context = getContext(env);
    jclass contextCls = env->GetObjectClass(context);

    jmethodID getPackageManagerMtd =
            env->GetMethodID(contextCls, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    auto packageManager = env->CallObjectMethod(context, getPackageManagerMtd);
    jclass packageManagerCls = env->GetObjectClass(packageManager);

    auto packageBytes = package.c_str();
    auto packageJstring = env->NewStringUTF(packageBytes);
    jmethodID getApplicationInfoMtd = env->GetMethodID(packageManagerCls, "getApplicationInfo",
                                                       "(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;");
    auto appInfo = env->CallObjectMethod(packageManager, getApplicationInfoMtd, packageJstring, 0);

    return extractApkPathFromAppInfo(env, appInfo);
}

string getApkPathFromAppInfoFromPackageManagerAsUser(JNIEnv *env) {
    jobject context = getContext(env);
    jclass contextCls = env->GetObjectClass(context);

    jmethodID getPackageManagerMtd =
            env->GetMethodID(contextCls, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    auto packageManager = env->CallObjectMethod(context, getPackageManagerMtd);
    jclass packageManagerCls = env->GetObjectClass(packageManager);

    jmethodID getUserIdMtd = env->GetMethodID(contextCls, "getUserId", "()I");
    int userId = env->CallIntMethod(context, getUserIdMtd);

    auto packageBytes = package.c_str();
    auto packageJstring = env->NewStringUTF(packageBytes);
    jmethodID getApplicationInfoMtd = env->GetMethodID(packageManagerCls, "getApplicationInfoAsUser",
                                                       "(Ljava/lang/String;II)Landroid/content/pm/ApplicationInfo;");
    auto appInfo = env->CallObjectMethod(packageManager, getApplicationInfoMtd, packageJstring, 0, userId);

    return extractApkPathFromAppInfo(env, appInfo);
}

string getApkPathFromPackageResourcePath(JNIEnv *env) {
    jobject context = getContext(env);
    jclass contextCls = env->GetObjectClass(context);
    jmethodID getPackageResourcePathMtd = env->GetMethodID(contextCls, "getPackageResourcePath", "()Ljava/lang/String;");
    jstring apkPath = static_cast<jstring>(env->CallObjectMethod(context, getPackageResourcePathMtd));
    return checkAppDir(jStringToString(env, apkPath));
}

string getApkPathFromPackageCodePath(JNIEnv *env) {
    jobject context = getContext(env);
    jclass contextCls = env->GetObjectClass(context);
    jmethodID getPackageCodePathMtd = env->GetMethodID(contextCls, "getPackageCodePath", "()Ljava/lang/String;");
    jstring apkPath = static_cast<jstring>(env->CallObjectMethod(context, getPackageCodePathMtd));
    return checkAppDir(jStringToString(env, apkPath));
}

string getApkPathFromShellPmPath() {
    auto result = shell("pm path " + package);
    auto start = result.find('/');
    auto end = result.rfind(".apk") + 4;
    return checkAppDir(result.substr(start, end - start));
}

string getApkPathFromShellPmList() {
    auto lines = split(shell("pm list packages -f " + package), '\n');
    for (auto line: lines) {
        if (line.find(package + '-') != string::npos) {
            auto start = line.find('/');
            auto end = line.find(".apk", start) + 4;
            return checkAppDir(line.substr(start, end - start));
        }
    }
    return "";
}

string getApkPathFromProcSelfMaps() {
    ifstream file("/proc/self/maps");
    string result = "";
    if (file.is_open()) {
        string line;
        while (getline(file, line))
            if (line.find(".apk") == line.size() - 4
                    && line.find("/app/") != string::npos
                    && line.find(package) != string::npos) {
                result = line.substr(line.find("/"));
                break;
            }
        file.close();
    }
    return checkAppDir(result);
}

#endif //ASSCRACKINGPREVENTION_APK_PATH_HPP
