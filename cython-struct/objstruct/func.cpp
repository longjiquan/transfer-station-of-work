#include "header.h"

struct Obj
getDefaultObject() { 
    struct Obj obj;
    return obj;
}

struct Obj
getObjByParam(int i, double d) {
    struct Obj obj{i, d};
    return obj;
}