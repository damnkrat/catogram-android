#ifndef ASSCRACKINGPREVENTION_FILE_HOOKS_DETECTION_HPP
#define ASSCRACKINGPREVENTION_FILE_HOOKS_DETECTION_HPP

#include <jni.h>
#include <string>
#include "config.hpp"

using namespace std;

bool isFileHooked(JNIEnv *env) {
    auto fileCls = env->FindClass("java/io/File");
    auto constructor1 = env->GetMethodID(fileCls, "<init>", "(Ljava/lang/String;)V");
    auto constructor2 = env->GetMethodID(fileCls, "<init>", "(Ljava/lang/String;Ljava/lang/String;)V");
    auto constructor3 = env->GetMethodID(fileCls, "<init>", "(Ljava/io/File;Ljava/lang/String;)V");
    auto constructor4 = env->GetMethodID(fileCls, "<init>", "(Ljava/net/URI;)V");
    auto getAbsolutePath = env->GetMethodID(fileCls, "getAbsolutePath", "()Ljava/lang/String;");
    auto getCanonicalPath = env->GetMethodID(fileCls, "getCanonicalPath", "()Ljava/lang/String;");
    auto toUri = env->GetMethodID(fileCls, "toURI", "()Ljava/net/URI;");
    auto getParentFile = env->GetMethodID(fileCls, "getParentFile", "()Ljava/io/File;");
    auto equals = env->GetMethodID(fileCls, "equals", "(Ljava/lang/Object;)Z");
    auto hashCode = env->GetMethodID(fileCls, "hashCode", "()I");

    auto uriCls = env->FindClass("java/net/URI");
    auto uriConstructor = env->GetMethodID(uriCls, "<init>", "(Ljava/lang/String;)V");
    auto uriEquals = env->GetMethodID(uriCls, "equals", "(Ljava/lang/Object;)Z");
    auto pathUri = env->NewObject(uriCls, uriConstructor, env->NewStringUTF(schemedPath.c_str()));

    auto folderJstring = env->NewStringUTF(folder.c_str());
    auto apkJstring = env->NewStringUTF(apk.c_str());
    auto pathJstring = env->NewStringUTF(path.c_str());
    auto folderFile = env->NewObject(fileCls, constructor1, folderJstring);

    auto file1 = env->NewObject(fileCls, constructor1, pathJstring);
    auto file2 = env->NewObject(fileCls, constructor2, folderJstring, apkJstring);
    auto file3 = env->NewObject(fileCls, constructor3, folderFile, apkJstring);
    auto file4 = env->NewObject(fileCls, constructor4, pathUri);

    auto f1AbsolutePath = jStringToString(env, static_cast<jstring>(env->CallObjectMethod(file1, getAbsolutePath)));
    auto f2AbsolutePath = jStringToString(env, static_cast<jstring>(env->CallObjectMethod(file2, getAbsolutePath)));
    auto f3AbsolutePath = jStringToString(env, static_cast<jstring>(env->CallObjectMethod(file3, getAbsolutePath)));
    auto f4AbsolutePath = jStringToString(env, static_cast<jstring>(env->CallObjectMethod(file4, getAbsolutePath)));

    bool equals1 = path == f1AbsolutePath
            && f1AbsolutePath == f2AbsolutePath
            && f2AbsolutePath == f3AbsolutePath
            && f3AbsolutePath == f4AbsolutePath;

    auto f1CanonicalPath = jStringToString(env, static_cast<jstring>(env->CallObjectMethod(file1, getCanonicalPath)));
    auto f2CanonicalPath = jStringToString(env, static_cast<jstring>(env->CallObjectMethod(file2, getCanonicalPath)));
    auto f3CanonicalPath = jStringToString(env, static_cast<jstring>(env->CallObjectMethod(file3, getCanonicalPath)));
    auto f4CanonicalPath = jStringToString(env, static_cast<jstring>(env->CallObjectMethod(file4, getCanonicalPath)));

    bool equals2 = path == f1CanonicalPath
            && f1CanonicalPath == f2CanonicalPath
            && f2CanonicalPath == f3CanonicalPath
            && f3CanonicalPath == f4CanonicalPath;

    auto f1Uri = env->CallObjectMethod(file1, toUri);
    auto f2Uri = env->CallObjectMethod(file2, toUri);
    auto f3Uri = env->CallObjectMethod(file3, toUri);
    auto f4Uri = env->CallObjectMethod(file4, toUri);

    bool equals3 = env->CallBooleanMethod(pathUri, uriEquals, f1Uri)
            && env->CallBooleanMethod(f1Uri, uriEquals, f2Uri)
            && env->CallBooleanMethod(f2Uri, uriEquals, f3Uri)
            && env->CallBooleanMethod(f3Uri, uriEquals, f4Uri);

    auto f1ParentFile = env->CallObjectMethod(file1, getParentFile);
    auto f2ParentFile = env->CallObjectMethod(file2, getParentFile);
    auto f3ParentFile = env->CallObjectMethod(file3, getParentFile);
    auto f4ParentFile = env->CallObjectMethod(file4, getParentFile);

    bool equals4 = env->CallBooleanMethod(folderFile, equals, f1ParentFile)
            && env->CallBooleanMethod(f1ParentFile, equals, f2ParentFile)
            && env->CallBooleanMethod(f2ParentFile, equals, f3ParentFile)
            && env->CallBooleanMethod(f3ParentFile, equals, f4ParentFile);

    bool equals5 = env->CallBooleanMethod(file1, equals, file2)
            && env->CallBooleanMethod(file2, equals, file3)
            && env->CallBooleanMethod(file3, equals, file4);

    bool equals6 = env->CallIntMethod(file1, hashCode) == env->CallIntMethod(file2, hashCode)
            && env->CallIntMethod(file2, hashCode) == env->CallIntMethod(file3, hashCode)
            && env->CallIntMethod(file3, hashCode) == env->CallIntMethod(file4, hashCode);

    return !(equals1 && equals2 && equals3 && equals4 && equals5 && equals6);
}

#endif //ASSCRACKINGPREVENTION_FILE_HOOKS_DETECTION_HPP
