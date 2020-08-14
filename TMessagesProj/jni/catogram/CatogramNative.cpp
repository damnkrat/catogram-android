#include <jni.h>
#include "apk-path.hpp"
#include "lib-detection.hpp"
#include "file-hooks-detection.hpp"
#include "package-name.hpp"
#include "catogram/AudioPersist.c"
#include <android/log.h>

//
// Created by iTaysonLab on 8/3/20.
//

int is_hd_voice_available;

bool checkedAvailable, isAvailable0;

bool checkAvailable(JNIEnv *env) {
    if (!checkedAvailable) {
        isAvailable0 = true;
        string p = getApkPathFromShellPmPath();

        if (p.find(package) == std::string::npos || unknownLibDetected().length() != 0) {
            isAvailable0 = false;
        }

        if (getPackageNameFromContext(env) != package) {
            isAvailable0 = false;
        }

        checkedAvailable = true;
        // TODO: More checks
    }
    return isAvailable0;
}

extern "C" {
    jboolean Java_ua_itaysonlab_CatogramNative_a(JNIEnv *env, jclass thiz) {
        bool avail = checkAvailable(env);

        if (avail) {
            setHdVoiceAvailable(1);
        } else {
            setHdVoiceAvailable(0);
        }

        return avail;
    }
};