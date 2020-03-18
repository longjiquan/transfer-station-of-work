#include "header.h"
#include <stdio.h>

struct Obj
getDefaultObj(void) {
    struct Obj obj{0, 0};
    return obj;
}

struct Obj
getObjByParam(int i, double d) {
    struct Obj obj{i, d};
    return obj;
}