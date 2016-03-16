/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <android/log.h>

#include "stringGen-jni.h"

#define LOG_TAG "com.hdfc.libs.Libs"

#define LOG_F(fn_name) __android_log_write(ANDROID_LOG_DEBUG, LOG_TAG, "Called : " fn_name )

static JavaVM *java_vm;

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    LOG_F ("JNI_OnLoad");

    return JNI_VERSION_1_6;
}

JNIEXPORT jstring  JNICALL Java_com_hdfc_libs_Libs_getString(JNIEnv* env,jobject thiz)
{
	#if defined(__arm__)
		#if defined(__ARM_ARCH_7A__)
		  #if defined(__ARM_NEON__)
			#if defined(__ARM_PCS_VFP)
			  #define ABI "armeabi-v7a/NEON (hard-float)"
			#else
			  #define ABI "armeabi-v7a/NEON"
			#endif
		  #else
			#if defined(__ARM_PCS_VFP)
			  #define ABI "armeabi-v7a (hard-float)"
			#else
			  #define ABI "armeabi-v7a"
			#endif
		  #endif
		#else
		 #define ABI "armeabi"
		#endif
	#elif defined(__i386__)
		#define ABI "x86"
	#elif defined(__x86_64__)
		#define ABI "x86_64"
	#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
		#define ABI "mips64"
	#elif defined(__mips__)
		#define ABI "mips"
	#elif defined(__aarch64__)
	#define ABI "arm64-v8a"
	#else
		#define ABI "unknown"
	#endif

    LOG_F ("getString");

    //TODO generate key dynamic with a logic no hardcode
/*
    char dest[8];
    char charset[] = "0123456789"
                         "abcdefghijklmnopqrstuvwxyz"
                         "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    LOG_F ("getString 0 ");


    dest[0] = charset[46];

    LOG_F ("getString 1 ");
    dest[1] = charset[10];
    dest[2] = charset[40];
    dest[3] = charset[50];

    dest[4] = charset[1];
    dest[5] = charset[9];
    dest[6] = charset[41];
    dest[7] = charset[12];

    LOG_F ("getString 2 ");

    dest[8] = '\0';

    LOG_F ("getString 3 ");

    return (*env)->NewStringUTF(env, dest);*/
    return (*env)->NewStringUTF(env, "KaEO19Fc");
}
/*
void rand_str(char *dest, size_t length) {
    char charset[] = "0123456789"
                     "abcdefghijklmnopqrstuvwxyz"
                     "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    while (length-- > 0) {
        size_t index = (double) rand() / RAND_MAX * (sizeof charset - 1);
        *dest++ = charset[index];
    }
    *dest = '\0';
}*/
