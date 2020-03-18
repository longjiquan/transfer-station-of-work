from setuptools import setup, Extension, find_packages
from Cython.Build import cythonize

def gen_modules():
    modules = cythonize(Extension(
        name='objstruct.wrapper',
        sources=['objstruct/wrapper.pyx']
    ))

    return modules

setup(
    name='objstruct',
    packages=find_packages(),
    ext_modules=gen_modules(),
)
