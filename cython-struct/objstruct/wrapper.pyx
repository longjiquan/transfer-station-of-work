# distutils: language = c++

from libcpp.string cimport string
cimport func
from func cimport *

cdef class PyObj:
    cdef Obj obj;

    def __cinit__(self, int i, double d):
        self.obj = Obj(i, d)

    def getInt(self):
        return self.obj.i

    def getDouble(self):
        return self.obj.d

def getDefaultObject():
    cdef Obj obj = func.getDefaultObject()
    cdef PyObj pyobj = PyObj(obj.i, obj.d)
    return pyobj

def getObjByParam(i, d):
    cdef Obj obj = func.getObjByParam(i, d)
    cdef PyObj pyobj = PyObj(obj.i, obj.d)
    return pyobj