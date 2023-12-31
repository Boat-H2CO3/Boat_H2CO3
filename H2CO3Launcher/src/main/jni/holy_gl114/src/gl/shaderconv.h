#ifndef _GL4ES_SHADERCONV_H_
#define _GL4ES_SHADERCONV_H_

#include "gles.h"
#include "program.h"

char * ConvertShaderConditionally(struct shader_s * shader_source);
char* ConvertShader(const char* pBuffer, int isVertex, shaderconv_need_t *need, int forwardPort);

int isBuiltinAttrib(const char* name);
int isBuiltinMatrix(const char* name);

const char* hasBuiltinAttrib(const char* vertexShader, int Att);
const char* builtinAttribGLName(const char* name);
const char* builtinAttribInternalName(const char* name);

#endif // _GL4ES_SHADERCONV_H_
