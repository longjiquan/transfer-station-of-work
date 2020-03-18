cdef extern from "func.cpp":
    pass

cdef extern from "header.h":
    cdef cppclass Obj:
        int i
        double d
        Obj()
        Obj(int i, double d)
    Obj getDefaultObject()
    Obj getObjByParam(int i, double d)