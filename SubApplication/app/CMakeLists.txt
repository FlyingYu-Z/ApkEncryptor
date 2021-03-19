
cmake_minimum_required(VERSION 3.4.1)

add_library(
        FlySub
        SHARED

        src/main/jni/utils/MD5.cpp
        src/main/jni/SubApp.cpp


        )

include_directories(src/main/jni/utils/)

SET(CMAKE_C_FLAGS$ {CMAKE_C_FLAGS} -fvisibility)

