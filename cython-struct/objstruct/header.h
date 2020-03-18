#include <string>

#pragma once

struct Obj {
    int i;
    double d;

    Obj() {
        i = 0;
        d = 0;
    }

    Obj(int i, double d) {
        this->i = i;
        this->d = d;
    }
};

struct Obj
getDefaultObj();

struct Obj
getObjByParam(int i, double d);