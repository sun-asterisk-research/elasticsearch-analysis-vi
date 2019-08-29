#include <tokenizer/tokenizer.hpp>
#include "com_coccoc_Tokenizer.h"

static jclass java_nio_ByteBuffer;
static jint JNI_VERSION = JNI_VERSION_10;

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
	JNIEnv* env;
	if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION) != JNI_OK) {
		return JNI_ERR;
	}

	java_nio_ByteBuffer = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/nio/ByteBuffer")));

	return JNI_VERSION;
}

void JNI_OnUnload(JavaVM *vm, void *reserved)
{
	JNIEnv* env;
	vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION);

	env->DeleteGlobalRef(java_nio_ByteBuffer);
}

/**
 * Segment a document and return an array of direct ByteBuffer referring
 * to segmentation result vectors. Further processing happens in Java code.
 * The method returns 3 ByteBuffer. The first one is the normalized text.
 * The second one contains Token structs (see token.hpp). The last one contains
 * pointers to dynamically created vectors, used for clean up when done.
 */
JNIEXPORT jobjectArray JNICALL Java_com_coccoc_Tokenizer_segment(
	JNIEnv *env, jobject obj, jstring jni_text, jint tokenize_option)
{
	const jchar *jtext = env->GetStringCritical(jni_text, nullptr);
	int text_length = env->GetStringLength(jni_text);

	// Use pointer to avoid automatic deallocation
	// Must call `freeMemory` when done to clean up
	std::vector< uint32_t > *normalized = new std::vector< uint32_t >();
	normalized->reserve(text_length);

	std::vector< int > original_pos;
	Tokenizer::instance().normalize_for_tokenization(jtext, text_length, *normalized, original_pos, true);
	env->ReleaseStringCritical(jni_text, jtext);

	// Use pointer here too
	std::vector< Token > *tokens = new std::vector< Token >();
	// space_positions is only used when `for_transforming` is true?
	std::vector< int > space_positions;

	Tokenizer::instance().handle_tokenization_request< Token >(
		*normalized, *tokens, space_positions, original_pos, false, tokenize_option);

	for (size_t i = 0; i < tokens->size(); ++i)
	{
		tokens->at(i).original_start += original_pos[tokens->at(i).normalized_start];
		tokens->at(i).original_end += original_pos[tokens->at(i).normalized_end];
	}

	// Keep pointers to original vectors in another array so we can clean up later
	// When done, pass this pointer (ByteBuffer) to `freeMemory` to clean up
	int64_t *p = new int64_t[2];
	p[0] = (int64_t) normalized;
	p[1] = (int64_t) tokens;

	jobjectArray results = env->NewObjectArray(3, java_nio_ByteBuffer, nullptr);

	env->SetObjectArrayElement(results, 0, env->NewDirectByteBuffer(normalized->data(), normalized->size() * 4));
	env->SetObjectArrayElement(results, 1, env->NewDirectByteBuffer(tokens->data(), tokens->size() * 6 * 4));
	env->SetObjectArrayElement(results, 2, env->NewDirectByteBuffer(p, 0));

	return results;
}

JNIEXPORT void JNICALL Java_com_coccoc_Tokenizer_freeMemory(JNIEnv *env, jobject obj, jobject res_pointer)
{
	// Cast each object pointer to their respective type, must be careful
	int64_t *p = static_cast<int64_t*>(env->GetDirectBufferAddress(res_pointer));
	delete (std::vector< uint32_t > *) (p[0]);
	delete (std::vector< Token > *) (p[1]);
	delete[](int64_t *) p;
}

JNIEXPORT void JNICALL Java_com_coccoc_Tokenizer_initialize(JNIEnv *env, jobject obj, jstring jni_dict_path)
{
	const char *dict_path = env->GetStringUTFChars(jni_dict_path, nullptr);
	int status_code = Tokenizer::instance().initialize(std::string(dict_path));

	if (status_code != 0) {
		jclass java_lang_RuntimeException = env->FindClass("java/lang/RuntimeException");

		env->ThrowNew(java_lang_RuntimeException, "Could not load dictionary");
	}

	env->ReleaseStringUTFChars(jni_dict_path, dict_path);
}
