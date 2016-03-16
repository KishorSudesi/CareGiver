LOCAL_PATH := $(abspath $(call my-dir))

include $(CLEAR_VARS)

LOCAL_MODULE := stringGen

SOURCE_PATH := stringGen

#Binary Size Compression
LOCAL_CFLAGS := -fvisibility=hidden -ffunction-sections -fdata-sections
LOCAL_LDFLAGS := -Wl,--gc-sections
#end Binary Size Compression

LOCAL_ASMFLAGS := -DELF

LOCAL_LDLIBS    += -lz -llog

LOCAL_SRC_FILES += \
	$(SOURCE_PATH)/stringGen-jni.c \
	
LOCAL_CFLAGS += \
	-DBUILD="201602171104" \

include $(BUILD_SHARED_LIBRARY)