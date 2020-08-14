#ifndef ASSCRACKINGPREVENTION_LIB_DETECTION_HPP
#define ASSCRACKINGPREVENTION_LIB_DETECTION_HPP

#include <string>
#include <fstream>
#include <vector>

#include "config.hpp"

using namespace std;

string unknownLibDetected() {
    ifstream file("/proc/self/maps");
    if (file.is_open()) {
        string line;
        while (getline(file, line)) {
            if (line.find(".so") == line.size() - 3 && line.find("/data/app") != string::npos) {
                const string &libName = line.substr(line.rfind("/lib") + 1);
                if (find(whitelist.begin(), whitelist.end(), libName) == whitelist.end())
                    return libName;
            }
        }
        file.close();
    }
    return "";
}

#endif //ASSCRACKINGPREVENTION_LIB_DETECTION_HPP
