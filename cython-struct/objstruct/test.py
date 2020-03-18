from objstruct import wrapper

default_obj = wrapper.getDefaultObject()
param_obj = wrapper.getObjByParam(2, 3)

print(dir(default_obj))
print(param_obj.getInt())
print(param_obj.getDouble())