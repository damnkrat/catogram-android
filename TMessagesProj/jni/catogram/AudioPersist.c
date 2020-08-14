//
// Created by iTaysonLab on 8/3/20.
//
#include "android/log.h"

extern int is_hd_voice_available;

static int getHdVoiceAvailable() {
    return is_hd_voice_available;
}

static void setHdVoiceAvailable(int hav) {
    is_hd_voice_available = hav;
}