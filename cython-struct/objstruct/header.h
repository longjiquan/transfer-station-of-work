#include <string>

#ifndef H_HEADER_H
#define H_HEADER_H

struct Obj {
    int i;
    double d;
    Obj() { 
        this->i = 0;
        this->d = 0;
    }
    Obj(int i, double d) {
        this->i = i;
        this->d = d;
    }
};

struct Obj
getDefaultObject();

struct Obj
getObjByParam(int i, double d);

#endif  // H_HEADER_H