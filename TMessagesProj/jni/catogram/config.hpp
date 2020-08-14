#ifndef ASSCRACKINGPREVENTION_CONFIG_HPP
#define ASSCRACKINGPREVENTION_CONFIG_HPP

#include <string>
#include <vector>
#include "utils.hpp"

const inline vector<string> whitelist{"libtmessages.30.so", "libtgvoip3.1.so", "libtgvoip1.1.so"};
const inline string package = "ua.itaysonlab.messenger";
const inline string packageSlashed = "ua/itaysonlab/messenger";


const inline string folder = "/data/app/" + package + "-1";
const inline string apk = "base.apk";
const inline string path = folder + "/" + apk;
const inline string schemedPath = "file://" + path;

#endif //ASSCRACKINGPREVENTION_CONFIG_HPP
