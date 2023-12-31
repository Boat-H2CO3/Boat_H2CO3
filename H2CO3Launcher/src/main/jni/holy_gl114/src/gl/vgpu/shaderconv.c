//
// Created by serpentspirale on 13/01/2022.
//
#include <math.h>
#include <string.h>
#include <malloc.h>
#include "shaderconv.h"
#include "../string_utils.h"
#include "../logs.h"
#include "../const.h"
#include "../../glx/hardext.h"
#include "../shaderconv.h"

int NO_OPERATOR_VALUE = 9999;

/**
 * Makes more and more destructive conversions to make the shader compile
 * @return The shader as a string
 */
char * ConvertShaderConditionally(struct shader_s * shader_source){
    int shaderCompileStatus;

    // First, vanilla gl4es, no forward port
    shader_source->converted = ConvertShader(shader_source->source, shader_source->type == GL_VERTEX_SHADER ? 1 : 0,&shader_source->need, 0);
    shaderCompileStatus = testGenericShader(shader_source);

    // Then, attempt back porting if desired of constrained to do so
    if(!shaderCompileStatus && globals4es.vgpu_backport) {
        shader_source->converted = ConvertShader(shader_source->source, shader_source->type == GL_VERTEX_SHADER ? 1 : 0,&shader_source->need, 0);
        shader_source->converted = ConvertShaderVgpu(shader_source);
        shaderCompileStatus = testGenericShader(shader_source);
    }

    // At last resort, use forward porting
    if(!shaderCompileStatus && hardext.glsl300es){
        shader_source->converted = ConvertShader(shader_source->source, shader_source->type == GL_VERTEX_SHADER ? 1 : 0, &shader_source->need, 1);
        shader_source->converted = ConvertShaderVgpu(shader_source);
    }

    return shader_source->converted;
}


/** Convert the shader through multiple steps
 * @param source The start of the shader as a string*/
char * ConvertShaderVgpu(struct shader_s * shader_source){

    if (globals4es.vgpu_dump){
        printf("New VGPU Shader source:\n%s\n", shader_source->converted);
    }

    // Get the shader source
    char * source = shader_source->converted;
    int sourceLength = strlen(source) + 1;

    // For now, skip stuff
    if(FindString(source, "#version 100")){
        if(globals4es.vgpu_force_conv || globals4es.vgpu_backport){
            if (shader_source->type == GL_VERTEX_SHADER){
                source = ReplaceVariableName(source, &sourceLength, "in", "attribute");
                source = ReplaceVariableName(source, &sourceLength, "out", "varying");
            }else{
                source = ReplaceVariableName(source, &sourceLength, "in", "varying");
                source = ReplaceFragmentOut(source, &sourceLength);
            }

            // Well, we don't have gl_VertexID on OPENGL 1
            source = ReplaceVariableName(source, &sourceLength, "gl_VertexID", "0");
            source = InplaceReplaceSimple(source, &sourceLength, "ivec", "vec");
            source = InplaceReplaceSimple(source, &sourceLength, "bvec", "vec");
            source = InplaceReplaceSimple(source, &sourceLength, "flat ", "");

            source = BackportConstArrays(source, &sourceLength);
            int insertPoint = FindPositionAfterVersion(source);
            source = InplaceInsertByIndex(source, &sourceLength, insertPoint + 1, "#define texelFetch(a, b, c) vec4(1.0,1.0,1.0,1.0) \n");

            source = ReplaceModOperator(source, &sourceLength);

            if (globals4es.vgpu_dump){
                printf("New VGPU Shader conversion:\n%s\n", source);
            }

            return source;
        }

        // Else, skip the conversion
        if (globals4es.vgpu_dump){
            printf("SKIPPING OLD SHADER CONVERSION \n");
        }
        return source;
    }


    // Remove 'const' storage qualifier
    //printf("REMOVING CONST qualifiers");
    //source = RemoveConstInsideBlocks(source, &sourceLength);
    //source = ReplaceVariableName(source, &sourceLength, "const", " ");




    // Avoid keyword clash with gl4es #define blocks
    //printf("REPLACING KEYWORDS");
    source = InplaceReplaceSimple(source, &sourceLength, "#define texture2D texture\n", "");
    source = ReplaceVariableName(source, &sourceLength, "sample", "vgpu_Sample");
    source = ReplaceVariableName(source, &sourceLength, "texture", "vgpu_texture");

    source = ReplaceFunctionName(source, &sourceLength, "texture2D", "texture");
    source = ReplaceFunctionName(source, &sourceLength, "texture2DLod", "textureLod");


    //printf("REMOVING \" CHARS ");
    // " not really supported here
    source = InplaceReplaceSimple(source, &sourceLength, "\"", "");

    // For now let's hope no extensions are used
    // TODO deal with extensions but properly
    //printf("REMOVING EXTENSIONS");
    //source = RemoveUnsupportedExtensions(source);

    // OpenGL natively supports non const global initializers, not OPENGL ES except if we add an extension
    //printf("ADDING EXTENSIONS\n");
    source = InsertExtensions(source, &sourceLength);

    //printf("REPLACING mod OPERATORS");
    // No support for % operator, so we replace it
    source = ReplaceModOperator(source, &sourceLength);

    //printf("COERCING INT TO FLOATS");
    // Hey we don't want to deal with implicit type stuff
    source = CoerceIntToFloat(source, &sourceLength);

    //printf("FIXING ARRAY ACCESS");
    // Avoid any weird type trying to be an index for an array
    source = ForceIntegerArrayAccess(source, &sourceLength);

    //printf("WRAPPING FUNCTION");
    // Since everything is a float, we need to overload WAY TOO MANY functions
    source = WrapIvecFunctions(source, &sourceLength);

    //printf("REMOVING DUBIOUS DEFINES");
    source = InplaceReplaceSimple(source, &sourceLength, "#define texture texture2D\n", "");
    source = InplaceReplaceSimple(source, &sourceLength, "#define attribute in\n", "");
    source = InplaceReplaceSimple(source, &sourceLength, "#define varying out\n", "");

    if (shader_source->type == GL_VERTEX_SHADER){
        source = ReplaceVariableName(source, &sourceLength, "attribute", "in");
        source = ReplaceVariableName(source, &sourceLength, "varying", "out");
    }else{
        source = ReplaceVariableName(source, &sourceLength, "varying", "in");
    }

    // Draw buffers aren't dealt the same on OPEN GL|ES
    if(shader_source->type == GL_FRAGMENT_SHADER && doesShaderVersionContainsES(source) ){
        //printf("REPLACING FRAG DATA");
        source = ReplaceGLFragData(source, &sourceLength);
        //printf("REPLACING FRAG COLOR");
        source = ReplaceGLFragColor(source, &sourceLength);
    }

    //printf("FUCKING UP PRECISION");
    source = ReplacePrecisionQualifiers(source, &sourceLength, shader_source->type == GL_VERTEX_SHADER);

    if (globals4es.vgpu_dump){
        printf("New VGPU Shader conversion:\n%s\n", source);
    }

    return source;
}

/**
 * Turn const arrays and its accesses into a function and function calls
 * @param source The shader as a string
 * @param sourceLength The shader allocated length
 * @return The shader as a string, maybe in a different memory location
 */
char * BackportConstArrays(char *source, int * sourceLength){
    unsigned long startPoint = strstrPos(source, "const");
    if(startPoint == 0){
        return source;
    }
    int constStart, constStop;
    GetNextWord(source, startPoint, &constStart, &constStop); // Catch the "const"

    int typeStart, typeStop;
    GetNextWord(source, constStop, &typeStart, &typeStop); // Catch the type, without []

    int variableNameStart, variableNameStop;
    GetNextWord(source, typeStop, &variableNameStart, &variableNameStop); // Catch the var name
    char * variableName = ExtractString(source, variableNameStart, variableNameStop);

    //Now, verify the data type is actually an array
    char * tokenStart = strstr(source + typeStop, "[");
    if( tokenStart != NULL && (tokenStart - source) < variableNameStart){
        // We've found an array. So we need to get to the starting parenthesis and isolate each member
        int startArray = GetNextTokenPosition(source, variableNameStop, '(', "");
        int endArray = GetClosingTokenPosition(source, startArray);

        // First pass, to count the amount of entries in the array
        int arrayEntryCount = -1;
        int currentPoint = startArray;
        while (currentPoint < endArray){
            ++arrayEntryCount;
            currentPoint = GetClosingTokenPositionTokenOverride(source, currentPoint, ',');
        }
        if(currentPoint == endArray){
            ++arrayEntryCount;
        }

        // Now we know how many entries we have, we can copy data
        int entryStart = startArray + 1;
        int entryEnd;
        for(int j=0; j<arrayEntryCount; ++j){
            // First, isolate the array entry
            entryEnd = GetClosingTokenPositionTokenOverride(source, entryStart, ',');

            // Replace the entry and jump to the end of the replacement
            source = InplaceReplaceByIndex(source, sourceLength, entryEnd , entryEnd +1, ";}"); // +2 - 1
            // Build the string to insert
            int indexStringLength = (j == 0 ? 1 : (int)(log10(j)+1));
            char * replacementString = malloc(19 + indexStringLength + 1);
            replacementString[19 + indexStringLength + 1] = '\0';
            memcpy(replacementString, "if(index==", 10);
            sprintf(replacementString + 10, "%d", j);
            strcpy(replacementString + 10 + indexStringLength, "){return ");

            // Insert the correct index in the replacement string
            source = InplaceInsertByIndex(source, sourceLength, entryStart, replacementString);

            entryStart = entryEnd + 19 + indexStringLength + 2;
            free(replacementString);
        }

        // The replacement string is not needed anymore


        // Add The last "}" to close the function
        source = InplaceInsertByIndex(source, sourceLength, entryStart, "}");
        // Add the argument section of the function
        source = InplaceReplaceByIndex(source, sourceLength, variableNameStop, startArray , "(int index){");
        // Remove the []
        source = InplaceReplaceByIndex(source, sourceLength, typeStop, variableNameStart - 1, " ");
        // Finally, remove the "const" keyword
        source = InplaceReplaceByIndex(source, sourceLength, startPoint, startPoint + 5, " ");

        // Now, we have to turn every array access to a function call
        // TODO change the start position to be more accurate to the end of the function !
        for(int k = strstrPos(source + endArray, variableName) + endArray; k < strlen(source); ){
            int startAccess = GetNextTokenPosition(source, k, '[', "");
            int endAccess = GetClosingTokenPosition(source, startAccess);
            source = InplaceReplaceByIndex(source, sourceLength, endAccess, endAccess, ")");
            source = InplaceReplaceByIndex(source, sourceLength, startAccess, startAccess, "(");

            int nextPos = strstrPos(source + k, variableName) + k;
            if(nextPos == k) break;
            k = nextPos;
        }

        free(variableName);

    }else{
        // Nothing, go to the next loop iteration
    }

    return source;
}

/**
 * Extract a substring from the provided string
 * @param source The shader as a string
 * @param startString The start of the substring
 * @param endString The end of the substring
 * @return A newly allocated substring. Don't forget to free() it !
 */
char * ExtractString(char * source, int startString, int endString){
    char * subString = malloc((endString - startString) +1);
    subString[(endString - startString) +1] = '\0';
    memcpy(subString, source + startString, (endString - startString));
    return subString;
}

/**
 * Replace the out vec4 from a fragment shader by the gl_FragColor constant
 * @param source The shader as a string
 * @return The shader, maybe in a different memory location
 */
char * ReplaceFragmentOut(char * source, int *sourceLength){
    int startPosition = strstrPos(source, "out");
    if(startPosition == 0) return source; // No "out" keyword
    int t1, t2;
    GetNextWord(source, startPosition, &t1, &t2); // Catches "out"
    GetNextWord(source, t2, &t1, &t2); // Catches "vec4"
    GetNextWord(source, t2, &t1, &t2); // Catches the variableName

    // Load the variable inside another string
    char * variableName = malloc(t2 - t1 + 1);
    variableName[t2 - t1 + 1] = '\0';
    memcpy(variableName, source + t1, t2 - t1);

    // Removing the declaration
    source = InplaceReplaceByIndex(source, sourceLength, startPosition, t2 + 1, "");

    // Replacing occurrences of the variable
    source = ReplaceVariableName(source, sourceLength, variableName, "gl_FragColor");

    free(variableName);

    return source;
}

/**
 * Get to the start, then end of the next of current word.
 * @param source The shader as a string
 * @param startPoint The start point to look at
 * @param startWord Will point to the start of the word
 * @param endWord Will point to the end of the word
 */
void GetNextWord(char *source, int startPoint, int * startWord, int * endWord){
    // Step 1: Find the real start point
    int start = 0;
    while(!start){
        if(isValidFunctionName(source[startPoint] ) || isDigit(source[startPoint])){
            start = startPoint;
            break;
        }
        ++startPoint;
    }

    // Step 2: Find the end of a word
    int end = 0;
    while (!end){
        if(!isValidFunctionName(source[startPoint] ) && !isDigit(source[startPoint])){
            end = startPoint;
            break;
        }
        ++startPoint;
    }

    // Then return values
    *startWord = start;
    *endWord = end;
}

char * InsertExtensions(char *source, int *sourceLength){
    int insertPoint = FindPositionAfterDirectives(source);
    //printf("INSERT POINT: %i\n", insertPoint);

    source = InsertExtension(source, sourceLength, insertPoint+1, "GL_EXT_shader_non_constant_global_initializers");
    source = InsertExtension(source, sourceLength, insertPoint+1, "GL_EXT_texture_cube_map_array");
    source = InsertExtension(source, sourceLength, insertPoint+1, "GL_EXT_texture_buffer");
    source = InsertExtension(source, sourceLength, insertPoint+1, "GL_OES_texture_storage_multisample_2d_array");
    return source;
}

char * InsertExtension(char * source, int * sourceLength, const int insertPoint, const char * extension){
    // First, insert the model, then the extension
    source = InplaceInsertByIndex(source, sourceLength, insertPoint, "#ifdef __EXT__ \n#extension __EXT__ : enable\n#endif\n");
    source = InplaceReplaceSimple(source, sourceLength, "__EXT__", extension);
    return source;
}

int doesShaderVersionContainsES(const char * source){
    return GetShaderVersion(source) >= 300;
}

char * WrapIvecFunctions(char * source, int * sourceLength){
    source = WrapFunction(source, sourceLength, "texelFetch", "vgpu_texelFetch", "\nvec4 vgpu_texelFetch(sampler2D sampler, vec2 P, float lod){return texelFetch(sampler, ivec2(int(P.x), int(P.y)), int(lod));}\n"
                                                                                 "vec4 vgpu_texelFetch(sampler3D sampler, vec3 P, float lod){return texelFetch(sampler, ivec3(int(P.x), int(P.y), int(P.z)), int(lod));}\n"
                                                                                 "vec4 vgpu_texelFetch(sampler2DArray sampler, vec3 P, float lod){return texelFetch(sampler, ivec3(int(P.x), int(P.y), int(P.z)), int(lod));}\n"
                                                                                 "#ifdef GL_EXT_texture_buffer\n"
                                                                                 "vec4 vgpu_texelFetch(samplerBuffer sampler, float P){return texelFetch(sampler, int(P));}\n"
                                                                                 "#endif\n"
                                                                                 "#ifdef GL_OES_texture_storage_multisample_2d_array\n"
                                                                                 "vec4 vgpu_texelFetch(sampler2DMS sampler, vec2 P, float _sample){return texelFetch(sampler, ivec2(int(P.x), int(P.y)), int(_sample));}\n"
                                                                                 "vec4 vgpu_texelFetch(sampler2DMSArray sampler, vec3 P, float _sample){return texelFetch(sampler, ivec3(int(P.x), int(P.y), int(P.z)), int(_sample));}\n"
                                                                                 "#endif\n");

    source = WrapFunction(source, sourceLength, "textureSize", "vgpu_textureSize", "\nvec2 vgpu_textureSize(sampler2D sampler, float lod){ivec2 size = textureSize(sampler, int(lod));return vec2(size.x, size.y);}\n"
                                                                                   "vec3 vgpu_textureSize(sampler3D sampler, float lod){ivec3 size = textureSize(sampler, int(lod));return vec3(size.x, size.y, size.z);}\n"
                                                                                   "vec2 vgpu_textureSize(samplerCube sampler, float lod){ivec2 size = textureSize(sampler, int(lod));return vec2(size.x, size.y);}\n"
                                                                                   "vec2 vgpu_textureSize(sampler2DShadow sampler, float lod){ivec2 size = textureSize(sampler, int(lod));return vec2(size.x, size.y);}\n"
                                                                                   "vec2 vgpu_textureSize(samplerCubeShadow sampler, float lod){ivec2 size = textureSize(sampler, int(lod));return vec2(size.x, size.y);}\n"
                                                                                   "#ifdef GL_EXT_texture_cube_map_array\n"
                                                                                   "vec3 vgpu_textureSize(samplerCubeArray sampler, float lod){ivec3 size = textureSize(sampler, int(lod));return vec3(size.x, size.y, size.z);}\n"
                                                                                   "vec3 vgpu_textureSize(samplerCubeArrayShadow sampler, float lod){ivec3 size = textureSize(sampler, int(lod));return vec3(size.x, size.y, size.z);}\n"
                                                                                   "#endif\n"
                                                                                   "vec3 vgpu_textureSize(sampler2DArray sampler, float lod){ivec3 size = textureSize(sampler, int(lod));return vec3(size.x, size.y, size.z);}\n"
                                                                                   "vec3 vgpu_textureSize(sampler2DArrayShadow sampler, float lod){ivec3 size = textureSize(sampler, int(lod));return vec3(size.x, size.y, size.z);}\n"
                                                                                   "#ifdef GL_EXT_texture_buffer\n"
                                                                                   "float vgpu_textureSize(samplerBuffer sampler){return float(textureSize(sampler));}\n"
                                                                                   "#endif\n"
                                                                                   "#ifdef GL_OES_texture_storage_multisample_2d_array\n"
                                                                                   "vec2 vgpu_textureSize(sampler2DMS sampler){ivec2 size = textureSize(sampler);return vec2(size.x, size.y);}\n"
                                                                                   "vec3 vgpu_textureSize(sampler2DMSArray sampler){ivec3 size = textureSize(sampler);return vec3(size.x, size.y, size.z);}\n"
                                                                                   "#endif\n");

    source = WrapFunction(source, sourceLength, "textureOffset", "vgpu_textureOffset", "\nvec4 vgpu_textureOffset(sampler2D tex, vec2 P, vec2 offset, float bias){ivec2 Size = textureSize(tex, 0);return texture(tex, P+offset/vec2(float(Size.x), float(Size.y)), bias);}\n"
                                                                                       "vec4 vgpu_textureOffset(sampler2D tex, vec2 P, vec2 offset){return vgpu_textureOffset(tex, P, offset, 0.0);}\n"
                                                                                       "vec4 vgpu_textureOffset(sampler3D tex, vec3 P, vec3 offset, float bias){ivec3 Size = textureSize(tex, 0);return texture(tex, P+offset/vec3(float(Size.x), float(Size.y), float(Size.z)), bias);}\n"
                                                                                       "vec4 vgpu_textureOffset(sampler3D tex, vec3 P, vec3 offset){return vgpu_textureOffset(tex, P, offset, 0.0);}\n"
                                                                                       "float vgpu_textureOffset(sampler2DShadow tex, vec3 P, vec2 offset, float bias){ivec2 Size = textureSize(tex, 0);return texture(tex, P+vec3(offset.x, offset.y, 0)/vec3(float(Size.x), float(Size.y), 1.0), bias);}\n"
                                                                                       "float vgpu_textureOffset(sampler2DShadow tex, vec3 P, vec2 offset){return vgpu_textureOffset(tex, P, offset, 0.0);}\n"
                                                                                       "vec4 vgpu_textureOffset(sampler2DArray tex, vec3 P, vec2 offset, float bias){ivec3 Size = textureSize(tex, 0);return texture(tex, P+vec3(offset.x, offset.y, 0)/vec3(float(Size.x), float(Size.y), float(Size.z)), bias);}\n"
                                                                                       "vec4 vgpu_textureOffset(sampler2DArray tex, vec3 P, vec2 offset){return vgpu_textureOffset(tex, P, offset, 0.0);}\n");

    source = WrapFunction(source, sourceLength, "shadow2D", "vgpu_shadow2D", "\nvec4 vgpu_shadow2D(sampler2DShadow shadow, vec3 coord){return vec4(texture(shadow, coord), 0.0, 0.0, 0.0);}\n"
                                                                              "vec4 vgpu_shadow2D(sampler2DShadow shadow, vec3 coord, float bias){return vec4(texture(shadow, coord, bias), 0.0, 0.0, 0.0);}\n");
    return source;
}

/**
 * Replace a function and its calls by a wrapper version, only if needed
 * @param source The shader code as a string
 * @param functionName The function to be replaced
 * @param wrapperFunctionName The replacing function name
 * @param function The wrapper function itself
 * @return The shader as a string, maybe in a different memory location
 */
char * WrapFunction(char * source, int * sourceLength, char * functionName, char * wrapperFunctionName, char * wrapperFunction){
    int originalSize = strlen(source);
    source = ReplaceFunctionName(source, sourceLength, functionName, wrapperFunctionName);
    // If some calls got replaced, add the wrapper
    if(originalSize != strlen(source)){
        int insertPoint = FindPositionAfterDirectives(source);
        source = InplaceInsertByIndex(source, sourceLength, insertPoint + 1, wrapperFunction);
    }

    return source;
}

/**
 * Replace the % operator with a mathematical equivalent (x - y * floor(x/y))
 * @param source The shader as a string
 * @return The shader as a string, maybe in a different memory location
 */
char * ReplaceModOperator(char * source, int * sourceLength){
    char * modelString = " mod(x, y) ";
    int startIndex, endIndex = 0;
    int * startPtr = &startIndex, *endPtr = &endIndex;

    for(int i=0;i<*sourceLength; ++i){
        if(source[i] != '%') continue;
        // A mod operator is found !
        char * leftOperand = GetOperandFromOperator(source, i, 0, startPtr);
        char * rightOperand = GetOperandFromOperator(source,  i, 1, endPtr);

        // Generate a model string to be inserted
        char * replacementString = malloc(strlen(modelString) + 1);
        strcpy(replacementString, modelString);
        int replacementSize = strlen(replacementString);
        replacementString = InplaceReplace(replacementString, &replacementSize, "x", leftOperand);
        replacementString = InplaceReplace(replacementString, &replacementSize, "y", rightOperand);

        // Insert the new string
        source = InplaceReplaceByIndex(source, sourceLength, startIndex, endIndex, replacementString);

        // Free all the temporary strings
        free(leftOperand);
        free(rightOperand);
        free(replacementString);
    }

    return source;
}

/**
 * Change all (u)ints to floats.
 * This is a hack to avoid dealing with implicit conversions on common operators
 * @param source The shader as a string
 * @return The shader as a string, maybe in a new memory location
 * @see ForceIntegerArrayAccess
 */
char * CoerceIntToFloat(char * source, int * sourceLength){
    // Let's go the "freestyle way"

    // Step 1 is to translate keywords
    // Attempt and loop unrolling -> worked well, time to fix my shit I guess
    source = ReplaceVariableName(source, sourceLength, "int", "float");
    source = WrapFunction(source, sourceLength, "int", "float", "\n ");
    source = ReplaceVariableName(source, sourceLength, "uint", "float");
    source = WrapFunction(source, sourceLength, "uint", "float", "\n ");

    // TODO Yes I could just do the same as above but I'm lazy at times
    source = InplaceReplaceSimple(source, sourceLength, "ivec", "vec");
    source = InplaceReplaceSimple(source, sourceLength, "uvec", "vec");

    source = InplaceReplaceSimple(source, sourceLength, "isampleBuffer", "sampleBuffer");
    source = InplaceReplaceSimple(source, sourceLength, "usampleBuffer", "sampleBuffer");

    source = InplaceReplaceSimple(source, sourceLength, "isampler", "sampler");
    source = InplaceReplaceSimple(source, sourceLength, "usampler", "sampler");


    // Step 3 is slower.
    // We need to parse hardcoded values like 1 and turn it into 1.(0)
    for(int i=0; i<*sourceLength; ++i){

        // Avoid version/line directives
        if(source[i] == '#' && (source[i + 1] == 'v' || source[i + 1] == 'l') ){
            // Look for the next line
            while (source[i] != '\n' && source[i] != '\0'){
                i++;
            }
        }

        if(!isDigit(source[i])){ continue; }
        // So there is a few situations that we have to distinguish:
        // functionName1 (      ----- meaning there is SOMETHING on its left side that is related to the number
        // function(1,          ----- there is something, and it ISN'T related to the number
        // float test=3;        ----- something on both sides, not related to the number.
        // float test=X.2       ----- There is a dot, so it is part of a float already
        // float test = 0.00000 ----- I have to backtrack to find the dot

        if(source[i-1] == '.' || source[i+1] == '.') continue;// Number part of a float
        if(isValidFunctionName(source[i - 1])) continue; // Char attached to something related
        if(isDigit(source[i+1])) continue; // End of number not reached
        if(isDigit(source[i-1])){
            // Backtrack to check if the number is floating point
            int shouldBeCoerced = 0;
            for(int j=1; 1; ++j){
                if(isDigit(source[i-j])) continue;
                if(isValidFunctionName(source[i-j])) break; // Function or variable name, don't coerce
                if(source[i-j] == '.' || ((source[i-j] == '+' || source[i-j] == '-') && source[i-j-1] == 'e')) break; // No coercion, float or scientific notation already
                // Nothing found, should be coerced then
                shouldBeCoerced = 1;
                break;
            }

            if(!shouldBeCoerced) continue;
        }

        // Now we know there is nothing related to the digit, turn it into a float
        source = InplaceInsertByIndex(source, sourceLength, i+1, ".0");
    }

    // TODO Hacks for special built in values and typecasts ?
    source = InplaceReplaceSimple(source, sourceLength, "gl_VertexID", "float(gl_VertexID)");
    source = InplaceReplaceSimple(source, sourceLength, "gl_InstanceID", "float(gl_InstanceID)");

    return source;
}

/** Force all array accesses to use integers by adding an explicit typecast
 * @param source The shader as a string
 * @return The shader as a string, maybe at a new memory location */
char * ForceIntegerArrayAccess(char* source, int * sourceLength){
    char * markerStart = "$";
    char * markerEnd = "`";

    // Step 1, we need to mark all [] that are empty and must not be changed
    int leftCharIndex = 0;
    for(int i=0; i< *sourceLength; ++i){
        if(source[i] == '['){
            leftCharIndex = i;
            continue;
        }
        // If a start has been found
        if(leftCharIndex){
            if(source[i] == ' ' || source[i] == '\n'){
                continue;
            }
            // We find the other side and mark both ends
            if(source[i] == ']'){
                source[leftCharIndex] = *markerStart;
                source[i] = *markerEnd;
            }
        }
        //Something else is there, abort the marking phase for this one
        leftCharIndex = 0;
    }

    // Step 2, replace the array accesses with a forced typecast version
    source = InplaceReplaceSimple(source, sourceLength, "]", ")]");
    source = InplaceReplaceSimple(source, sourceLength, "[", "[int(");

    // Step 3, restore all marked empty []
    source = InplaceReplaceSimple(source, sourceLength, markerStart, "[");
    source = InplaceReplaceSimple(source, sourceLength, markerEnd, "]");

    return source;
}


/** Small helper to help evaluate whether to continue or not I guess
 * Values over 9900 are not for real operators, more like stop indicators*/
int GetOperatorValue(char operator){
    if(operator == ',' || operator == ';') return 9998;
    if(operator == '=') return 9997;
    if(operator == '+' || operator == '-') return 3;
    if(operator == '*' || operator == '/' || operator == '%') return 2;
    return NO_OPERATOR_VALUE; // Meaning no value;
}

/** Get the left or right operand, given the last index of the operator
 * It bases its ability to get operands by evaluating the priority of operators.
 * @param source The shader as a string
 * @param operatorIndex The index the operator is found
 * @param rightOperand Whether we get the right or left operator
 * @param limit The left or right index of the operand
 * @return newly allocated string with the operand
 */
char* GetOperandFromOperator(char* source, int operatorIndex, int rightOperand, int * limit){
    int parserState = 0;
    int parserDirection = rightOperand ? 1 : -1;
    int operandStartIndex = 0, operandEndIndex = 0;
    int parenthesesLeft = 0, hasFoundParentheses = 0;
    int operatorValue = GetOperatorValue(source[operatorIndex]);
    int lastOperator = 0; // Used to determine priority for unary operators

    char parenthesesStart = rightOperand ? '(' : ')';
    char parenthesesEnd = rightOperand ? ')' : '(';
    int stringIndex = operatorIndex;

    // Get to the operand
    while (parserState == 0){
        stringIndex += parserDirection;
        if(source[stringIndex] != ' '){
            parserState = 1;
            // Place the mark
            if(rightOperand){
                operandStartIndex = stringIndex;
            }else{
                operandEndIndex = stringIndex;
            }

            // Special case for unary operator when parsing to the right
            if(GetOperatorValue(source[stringIndex]) == 3 ){ // 3 is +- operators
                stringIndex += parserDirection;
            }
        }
    }

    // Get to the other side of the operand, the twist is here.
    while (parenthesesLeft > 0 || parserState == 1){

        // Look for parentheses
        if(source[stringIndex] == parenthesesStart){
            hasFoundParentheses = 1;
            parenthesesLeft += 1;
            stringIndex += parserDirection;
            continue;
        }

        if(source[stringIndex] == parenthesesEnd){
            hasFoundParentheses = 1;
            parenthesesLeft -= 1;

            // Likely to happen in a function call
            if(parenthesesLeft < 0){
                parserState = 3;
                if(rightOperand){
                    operandEndIndex = stringIndex - 1;
                }else{
                    operandStartIndex = stringIndex + 1;
                }
                continue;
            }
            stringIndex += parserDirection;
            continue;
        }

        // Small optimisation
        if(parenthesesLeft > 0){
            stringIndex += parserDirection;
            continue;
        }

        // So by now the following assumptions are made
        // 1 - We aren't between parentheses
        // 2 - No implicit multiplications are present
        // 3 - No fuckery with operators like "test = +-+-+-+-+-+-+-+-3;" although I attempt to support them

        // Higher value operators have less priority
        int currentValue = GetOperatorValue(source[stringIndex]);


        // The condition is different due to the evaluation order which is left to right, aside from the unary operators
        if((rightOperand ? currentValue >= operatorValue: currentValue > operatorValue)){
            if(currentValue == NO_OPERATOR_VALUE){
                if(source[stringIndex] == ' '){
                    stringIndex += parserDirection;
                    continue;
                }

                // Found an operand, so reset the operator eval for unary
                if(rightOperand) lastOperator = NO_OPERATOR_VALUE;

                // maybe it is the start of a function ?
                if(hasFoundParentheses){
                    parserState = 2;
                    continue;
                }
                // If no full () set is found, assume we didn't fully travel the operand
                stringIndex += parserDirection;
                continue;
            }

            // Special case when parsing unary operator to the right
            if(rightOperand && operatorValue == 3 && lastOperator < currentValue){
                stringIndex += parserDirection;
                continue;
            }

            // Stop, we found an operator of same worth.
            parserState = 3;
            if(rightOperand){
                operandEndIndex = stringIndex - 1;
            }else{
                operandStartIndex = stringIndex + 1;
            }
        }

        // Special case for unary operators from the right
        if(rightOperand && operatorValue == 3) { // 3 is + - operators
            lastOperator = currentValue;
        } // Special case for unary operators from the left
        if(!rightOperand && operatorValue < 3 && currentValue == 3){
            lastOperator = NO_OPERATOR_VALUE;
            for(int j=1; 1; ++j){
                int subCurrentValue = GetOperatorValue(source[stringIndex - j]);
                if(subCurrentValue != NO_OPERATOR_VALUE){
                    lastOperator = subCurrentValue;
                    continue;
                }

                // No operator value, can be almost anything
                if(source[stringIndex - j] == ' ') continue;
                // Else we found something. Did we found a high priority operator ?
                if(lastOperator <= operatorValue){ // If so, we allow continuing and going out of the loop
                    stringIndex -= j;
                    parserState = 1;
                    break;
                }
                // No other operator found
                operandStartIndex = stringIndex;
                parserState = 3;
                break;
            }
        }
        stringIndex += parserDirection;
    }

    // Status when we get the name of a function and nothing else.
    while (parserState == 2){
        if(source[stringIndex] != ' '){
            stringIndex += parserDirection;
            continue;
        }
        if(rightOperand){
            operandEndIndex = stringIndex - 1;
        }else{
            operandStartIndex = stringIndex + 1;
        }
        parserState = 3;
    }

    // At this point, we know both the start and end point of our operand, let's copy it
    char * operand = malloc(operandEndIndex - operandStartIndex + 2);
    memcpy(operand, source+operandStartIndex, operandEndIndex - operandStartIndex + 1);
    // Make sure the string is null terminated
    operand[operandEndIndex - operandStartIndex + 1] = '\0';

    // Send back the limitIndex
    *limit = rightOperand ? operandEndIndex : operandStartIndex;

    return operand;
}

/**
 * Replace any gl_FragData[n] reference by creating an out variable with the manual layout binding
 * @param source  The shader source as a string
 * @return The shader as a string, maybe at a different memory location
 */
char * ReplaceGLFragData(char * source, int * sourceLength){

    // 10 is arbitrary, but I don't expect the shader to use so many
    // TODO I guess the array could be accessed with one or more spaces :think:
    // TODO wait they can access via a variable !
    for (int i = 0; i < 10; ++i) {
        // Check for 2 forms on the glFragData and take the first one found
        char needle[30];
        sprintf(needle, "gl_FragData[%i]", i);

        // Skip if the draw buffer isn't used at this index
        char * useFragData = strstr(source, &needle[0]);
        if(useFragData == NULL){
            sprintf(needle, "gl_FragData[int(%i.0)]", i);
            useFragData = strstr(source, &needle[0]);
            if(useFragData == NULL) continue;
        }

        // Construct replacement string
        char replacement[20];
        char replacementLine[70];
        sprintf(replacement, "vgpu_FragData%i", i);
        sprintf(replacementLine, "\nlayout(location = %i) out mediump vec4 %s;\n", i, replacement);
        int insertPoint = FindPositionAfterDirectives(source);

        // And place them into the shader
        source = InplaceReplaceSimple(source, sourceLength, &needle[0], &replacement[0]);
        source = InplaceInsertByIndex(source, sourceLength, insertPoint + 1, &replacementLine[0]);
    }
    return source;
}

/**
 * Replace the gl_FragColor
 * @param source The shader as a string
 * @return The shader a a string, maybe in a different memory location
 */
char * ReplaceGLFragColor(char * source, int * sourceLength){
    if(strstr(source, "gl_FragColor")){
        source = InplaceReplaceSimple(source, sourceLength, "gl_FragColor", "vgpu_FragColor");
        int insertPoint = FindPositionAfterDirectives(source);
        source = InplaceInsertByIndex(source, sourceLength, insertPoint + 1, "out mediump vec4 vgpu_FragColor;\n");
    }
    return source;
}

/**
 * Remove all extensions right now by replacing them with spaces
 * @param source The shader as a string
 * @return The shader as a string, maybe in a different memory location
 */
char * RemoveUnsupportedExtensions(char * source){
    //TODO remove only specific extensions ?
    for(char * extensionPtr = strstr(source, "#extension "); extensionPtr; extensionPtr = strstr(source, "#extension ")){
        int i = 0;
        while(extensionPtr[i] != '\n'){
            extensionPtr[i] = ' ';
            ++i;
        }
    }
    return source;
}

/**
 * Replace the variable name in a shader, mostly used to avoid keyword clashing
 * @param source The shader as a string
 * @param initialName The initial name for the variable
 * @param newName The new name for the variable
 * @return The shader as a string, maybe in a different memory location
 */
char * ReplaceVariableName(char * source, int * sourceLength, char * initialName, char* newName) {

    char * toReplace = malloc(strlen(initialName) + 3);
    char * replacement = malloc(strlen(newName) + 3);
    char * charBefore = "{}([];+-*/~!%<>,&| \n\t";
    char * charAfter = ")[];+-*/%<>;,|&. \n\t";

    // Prepare the fixed part of the strings
    strcpy(toReplace+1, initialName);
    toReplace[strlen(initialName)+2] = '\0';

    strcpy(replacement+1, newName);
    replacement[strlen(newName)+2] = '\0';

    for (int i = 0; i < strlen(charBefore); ++i) {
        for (int j = 0; j < strlen(charAfter); ++j) {
            // Prepare the string to replace
            toReplace[0] = charBefore[i];
            toReplace[strlen(initialName)+1] = charAfter[j];

            // Prepare the replacement string
            replacement[0] = charBefore[i];
            replacement[strlen(newName)+1] = charAfter[j];

            source = InplaceReplaceSimple(source, sourceLength, toReplace, replacement);
        }
    }

    free(toReplace);
    free(replacement);

    return source;
}

/**
 * Replace a function definition and calls to the function to another name
 * @param source The shader as a string
 * @param sourceLength The shader length
 * @param initialName The name be to changed
 * @param finalName The name to use instead
 * @return The shader as a string, maybe in a different memory location
 */
char * ReplaceFunctionName(char * source, int * sourceLength, char * initialName, char * finalName){
    for(unsigned long currentPosition = 0; 1; currentPosition += strlen(initialName)){
        unsigned long newPosition = strstrPos(source + currentPosition, initialName);
        if(newPosition == 0) // No more calls
            break;

        // Check if that is indeed a function call on the right side
        if (source[GetNextTokenPosition(source, currentPosition + newPosition + strlen(initialName), '(', " \n\t")] != '('){
            currentPosition += newPosition;
            continue; // Skip to the next potential call
        }

        // Check the naming on the left side
        if (isValidFunctionName(source[currentPosition + newPosition - 1])){
            currentPosition += newPosition;
            continue; // Skip to the next potential call
        }

        // This is a valid function call/definition, replace it
        source = InplaceReplaceByIndex(source, sourceLength, currentPosition + newPosition, currentPosition + newPosition + strlen(initialName) - 1, finalName);
        currentPosition += newPosition;
    }
    return source;
}

/** Remove 'const ' storage qualifier from variables inside {..} blocks
 * @param source The pointer to the start of the shader */
char * RemoveConstInsideBlocks(char* source, int * sourceLength){
    int insideBlock = 0;
    char * keyword = "const \0";
    int keywordLength = strlen(keyword);


    for(int i=0; i< *sourceLength+1; ++i){
        // Step 1, look for a block
        if(source[i] == '{'){
            insideBlock += 1;
            continue;
        }
        if(!insideBlock) continue;

        // A block has been found, look for the keyword
        if(source[i] == keyword[0]){
            int keywordMatch = 1;
            int j = 1;
            while (j < keywordLength){
                if (source[i+j] != keyword[j]){
                    keywordMatch = 0;
                    break;
                }
                j +=1;
            }
            // A match is found, remove it
            if(keywordMatch){
                source = InplaceReplaceByIndex(source, sourceLength, i, i+j - 1, " ");
                continue;
            }
        }

        // Look for an exit
        if(source[i] == '}'){
            insideBlock -= 1;
            continue;
        }
    }
    return source;
}

/** Find the first point which is right after most shader directives
 * @param source The shader as a string
 * @return The index position after the #version line, start of the shader if not found
 */
int FindPositionAfterDirectives(char * source){
    const char * position = FindString(source, "#version");
    if (position == NULL) return 0;
    for(int i=7; 1; ++i){
        if(position[i] == '\n'){
            if(position[i+1] == '#') continue; // a directive is present right after, skip
            return i;
        }
    }
}

int FindPositionAfterVersion(char * source){
    const char * position = FindString(source, "#version");
    if (position == NULL) return 0;
    for(int i=7; 1; ++i){
        if(position[i] == '\n'){
            return i;
        }
    }
}

/**
 * Replace and inserts precision qualifiers as necessary, see LIBGL_VGPU_PRECISION
 * @param source The shader as a string
 * @param sourceLength The length of the string
 * @return The shader as a string, maybe in a different memory location
 */
char * ReplacePrecisionQualifiers(char * source, int * sourceLength, int isVertex){

    if(!doesShaderVersionContainsES(source)){
        if (globals4es.vgpu_dump) {
            printf("\nSKIPPING the replacement qualifiers step\n");
        }
        return source;
    }

    // Step 1 is to remove any "precision" qualifiers
    for(unsigned long currentPosition=strstrPos(source, "precision "); currentPosition>0;currentPosition=strstrPos(source, "precision ")){
        // Once a qualifier is found, get to the end of the instruction and replace
        int endPosition = GetNextTokenPosition(source, currentPosition, ';', "");
        source = InplaceReplaceByIndex(source, sourceLength, currentPosition, endPosition,"");
    }

    // Step 2 is to insert precision qualifiers, even the ones we think are defaults, since there are defaults only for some types

    int insertPoint = FindPositionAfterDirectives(source);
    source = InplaceInsertByIndex(source, sourceLength, insertPoint,
                                   "\nprecision lowp sampler2D;\n"
                                   "precision lowp sampler3D;\n"
                                   "precision lowp sampler2DShadow;\n"
                                   "precision lowp samplerCubeShadow;\n"
                                   "precision lowp sampler2DArray;\n"
                                   "precision lowp sampler2DArrayShadow;\n"
                                   "precision lowp samplerCube;\n"
                                   "#ifdef GL_EXT_texture_buffer\n"
                                   "precision lowp samplerBuffer;\n"
                                   "precision lowp imageBuffer;\n"
                                   "#endif\n"
                                   "#ifdef GL_EXT_texture_cube_map_array\n"
                                   "precision lowp imageCubeArray;\n"
                                   "precision lowp samplerCubeArray;\n"
                                   "precision lowp samplerCubeArrayShadow;\n"
                                   "#endif\n"
                                   "#ifdef GL_OES_texture_storage_multisample_2d_array\n"
                                   "precision lowp sampler2DMS;\n"
                                   "precision lowp sampler2DMSArray;\n"
                                   "#endif\n");

    if(GetShaderVersion(source) > 300){
        source = InplaceInsertByIndex(source, sourceLength,insertPoint,
                                      "\nprecision lowp image2D;\n"
                                      "precision lowp image2DArray;\n"
                                      "precision lowp image3D;\n"
                                      "precision lowp imageCube;\n");
    }
    int supportHighp = ((isVertex || hardext.highp) ? 1 : 0);
    source = InplaceInsertByIndex(source, sourceLength, insertPoint, supportHighp ? "\nprecision highp float;\n" : "\nprecision medium float;\n");

    if (globals4es.vgpu_precision != 0){
        char * target_precision;
        switch (globals4es.vgpu_precision) {
            case 1: target_precision = "highp"; break;
            case 2: target_precision = "mediump"; break;
            case 3: target_precision = "lowp"; break;
            default: target_precision = "highp";
        }
        source = ReplaceVariableName(source, sourceLength, "highp", target_precision);
        source = ReplaceVariableName(source, sourceLength, "mediump", target_precision);
        source = ReplaceVariableName(source, sourceLength, "lowp", target_precision);
    }

    return source;
}

/**
 * @param openingToken The opening token
 * @return All closing tokens, if available
 */
char * GetClosingTokens(char openingToken){
    switch (openingToken) {
        case '(': return ")";
        case '[': return "]";
        case ',': return ",)";
        case '{': return "}";
        case ';': return ";";

        default: return "";
    }
}

/**
 * @param openingToken The opening token
 * @return Whether the token is an opening token
 */
int isOpeningToken(char openingToken){
    return openingToken != ',' && strlen(GetClosingTokens(openingToken)) != 0;
}

int GetClosingTokenPosition(const char * source, int initialTokenPosition){
    return GetClosingTokenPositionTokenOverride(source, initialTokenPosition, source[initialTokenPosition]);
}

/**
 * Get the index of the closing token within a string, same as initialTokenPosition if not found
 * @param source The string to look into
 * @param initialTokenPosition The opening token position
 * @return The closing token position
 */
int GetClosingTokenPositionTokenOverride(const char * source, int initialTokenPosition, char initialToken){
    // Step 1: Determine the closing token
    char openingToken = initialToken;
    char * closingTokens = GetClosingTokens(openingToken);
    printf("Closing tokens: %s", closingTokens);
    if (strlen(closingTokens) == 0){
        printf("No closing tokens, somehow \n");
        return initialTokenPosition;
    }

    // Step 2: Go through the string to find what we want
    for(int i=initialTokenPosition+1; i<strlen(source); ++i){
        // Loop though all the available closing tokens first, since opening/closing tokens can be identical
        for(int j=0; j<strlen(closingTokens); ++j){
            if (source[i] == closingTokens[j]){
                return i;
            }
        }

        if (isOpeningToken(source[i])){
            i = GetClosingTokenPosition(source, i);
            continue;
        }
    }
    printf("No closing tokens 2 , somehow \n");
    return initialTokenPosition; // Nothing found
}



/**
 * Return the position of the first token corresponding to what we want
 * @param source The source string
 * @param initialPosition The starting position to look from
 * @param token The token you want to find
 * @param acceptedChars All chars we can go over without tripping. Empty means all chars are allowed.
 * @return
 */
int GetNextTokenPosition(const char * source, int initialPosition, const char token, const char * acceptedChars){
    for(int i=initialPosition+1; i< strlen(source); ++i){
        // Tripping check
        if(strlen(acceptedChars) > 0){
            for(int j=0; j< strlen(acceptedChars); ++j){
                if (source[i] == acceptedChars[j]) break; // No tripping, continue
            }
            return initialPosition; // Tripped, meaning the token is not found
        }

        if (source[i] == token){
            return i;
        }
    }
    return initialPosition;
}

/**
 * @param haystack
 * @param needle
 * @return The position of the first occurence of the needle in the haystack
 */
unsigned long strstrPos(const char * haystack, const char * needle){
    char * substr = strstr(haystack, needle);
    if (substr == NULL) return 0;
    return (substr - haystack);
}

/**
 * Inserts int(...) on a specific argument from each call to the function
 * @param source The shader as a string
 * @param functionName The name of the function to manipulate
 * @param argumentPosition The position of the argument to manipulate, from 0. If not found, no changes are made.
 * @return The shader as a string, maybe in a different memory location
 */
char * insertIntAtFunctionCall(char * source, int * sourceSize, const char * functionName, int argumentPosition){
    //TODO a less naive function for edge-cases ?
    unsigned long functionCallPosition = strstrPos(source, functionName);
    while(functionCallPosition != 0){
        int openingTokenPosition = GetNextTokenPosition(source, functionCallPosition + strlen(functionName), '(', " \n\r\t");
        if (source[openingTokenPosition] == '('){
            // Function call found, determine the start and end of the argument
            int endArgPos = openingTokenPosition;
            int startArgPos = openingTokenPosition;

            // Note the additional check to see we aren't at the end of a function
            for(int argCount=0; argCount<=argumentPosition && source[startArgPos] != ')'; ++argCount){
                endArgPos = GetClosingTokenPositionTokenOverride(source, endArgPos, ',');
                if (argCount == argumentPosition){
                    // Argument found, insert the int(...)
                    source = InplaceInsertByIndex(source, sourceSize, endArgPos+1, ")");
                    source = InplaceInsertByIndex(source, sourceSize, startArgPos+1, "int(");
                    break;
                }
                // Not the arg we want, got to the next one
                startArgPos = endArgPos;
            }
        }

        // Get the next function call
        unsigned long offset = strstrPos(source + functionCallPosition + strlen(functionName), functionName);
        if (offset == 0) break; // No more function calls
        functionCallPosition += offset + strlen(functionName);
    }
    return source;
}

/**
 * @param source The shader as a string
 * @return The shader version: eg. 310 for #version 310 es
 */
int GetShaderVersion(const char * source){
    // Oh yeah, I won't care much about this function
    if(FindString(source, "#version 320 es")){return 320;}
    if(FindString(source, "#version 310 es")){return 310;}
    if(FindString(source, "#version 300 es")){return 300;}
    if(FindString(source, "#version 150")){return 150;}
    if(FindString(source, "#version 130")){return 130;}
    if(FindString(source, "#version 120")){return 120;}
    return 100;
}
