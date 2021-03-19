
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CPP_EXTENSION := .cpp .cc .h
LOCAL_MODULE    := BY
LOCAL_SRC_FILES := Main.cpp


ifeq ($(TARGET_ARCH_ABI),x86)
    LOCAL_CFLAGS += -ffast-math -mtune=atom -mssse3 -mfpmath=sse
endif

LOCAL_CFLAGS:=-fvisibility=hidden

include $(BUILD_SHARED_LIBRARY)
