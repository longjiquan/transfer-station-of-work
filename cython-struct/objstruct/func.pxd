cdef extern from "/home/ljq/mess/cython-struct/objstruct/func.cpp":
    pass

cdef extern from "header.h":
    cdef cppclass Obj:
        Obj()
        Obj(int i, double d)
        int i
        double d
    Obj getDefaultObj()
    Obj getObjByParam(int i, double d)