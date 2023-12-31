#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "string_utils.h"

const char* AllSeparators = " \t\n\r.,;()[]{}-<>+*/%&\\\"'^$=!:?";

char* ResizeIfNeeded(char* pBuffer, int *size, int addsize);

char* InplaceReplace(char* pBuffer, int* size, const char* S, const char* D)
{
    int lS = strlen(S), lD = strlen(D);
    pBuffer = ResizeIfNeeded(pBuffer, size, (lD-lS)*CountString(pBuffer, S));
    char* p = pBuffer;
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // check if good to replace, strchr also found '\0' :)
        if(strchr(AllSeparators, p[lS])!=NULL && (p==pBuffer || strchr(AllSeparators, p[-1])!=NULL)) {
            // move out rest of string
            memmove(p+lD, p+lS, strlen(p)-lS+1);
            // replace
            memcpy(p, D, strlen(D));
            // next
            p+=lD;
        } else p+=lS;
    }
    
    return pBuffer;
}

char * InplaceReplaceByIndex(char* pBuffer, int* size, const int startIndex, const int endIndex, const char* replacement)
{
    //SHUT_LOGD("BY INDEX: %s", replacement);
    //SHUT_LOGD("BY INDEX: %i", strlen(replacement));

    int length_difference;
    if(endIndex < startIndex)
        length_difference = strlen(replacement) + (endIndex - startIndex);
    else if(endIndex == startIndex)
        length_difference = strlen(replacement) - 1; // The initial char gets replaced
    else
        length_difference = strlen(replacement) - (endIndex - startIndex); // Can be negative if repl is smaller

    pBuffer = ResizeIfNeeded(pBuffer, size, length_difference);
    //SHUT_LOGD("BEFORE MOVING: \n%s", pBuffer);
    // Move the end of the string
    memmove(pBuffer + startIndex + strlen(replacement) , pBuffer + endIndex + 1, strlen(pBuffer) - endIndex + 1);
    //SHUT_LOGD("AFTER MOVING 1: \n%s", pBuffer);

    // Insert the replacement
    memcpy(pBuffer + startIndex, replacement, strlen(replacement));
    //strncpy(pBuffer + startIndex, replacement, strlen(replacement));
    //SHUT_LOGD("AFTER MOVING 2: \n%s", pBuffer);

    return pBuffer;
}

/**
 * Insert the string at the index, pushing "every chars to the right"
 * @param source The shader as a string
 * @param sourceLength The ALLOCATED length of the shader
 * @param insertPoint The index at which the string is inserted.
 * @param insertedString The string to insert
 * @return The shader as a string, maybe in a different memory location
 */
char * InplaceInsertByIndex(char * source, int *sourceLength, const int insertPoint, const char *insertedString){
    int insertLength = strlen(insertedString);
    source = ResizeIfNeeded(source, sourceLength, insertLength);
    memmove(source + insertPoint + insertLength,  source + insertPoint, strlen(source) - insertPoint + 1);
    memcpy(source + insertPoint, insertedString, insertLength);

    return source;
}

char* InplaceInsert(char* pBuffer, const char* S, char* master, int* size)
{
    char* m = ResizeIfNeeded(master, size, strlen(S));
    if(m!=master) {
        pBuffer += (m-master);
        master = m;
    }
    char* p = pBuffer;
    int lS = strlen(S), ll = strlen(pBuffer);
    memmove(p+lS, p, ll+1);
    memcpy(p, S, lS);

    return master;
}

char* GetLine(char* pBuffer, int num)
{
    char *p = pBuffer;
    while(num-- && (p=strstr(p, "\n"))) p+=strlen("\n");
    return (p)?p:pBuffer;
}

int CountLine(const char* pBuffer)
{
    int n=0;
    const char* p = pBuffer;
    while((p=strstr(p, "\n"))) {
        p+=strlen("\n");
        n++;
    }
    return n;
}

int GetLineFor(const char* pBuffer, const char* S)
{
    int n=0;
    const char* p = pBuffer;
    const char* end = FindString(pBuffer, S);
    if(!end)
        return 0;
    while((p=strstr(p, "\n"))) {
        p+=strlen("\n");
        n++;
        if(p>=end)
            return n;
    }
    return n;
}

int CountString(const char* pBuffer, const char* S)
{
    const char* p = pBuffer;
    int lS = strlen(S);
    int n = 0;
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // check if good to count, strchr also found '\0' :)
        if(strchr(AllSeparators, p[lS])!=NULL && (p==pBuffer || strchr(AllSeparators, p[-1])!=NULL))
            n++;
        p+=lS;
    }
    return n;
}

const char* FindString(const char* pBuffer, const char* S)
{
    const char* p = pBuffer;
    int lS = strlen(S);
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // check if good to count, strchr also found '\0' :)
        if(strchr(AllSeparators, p[lS])!=NULL && (p==pBuffer || strchr(AllSeparators, p[-1])!=NULL))
            return p;
        p+=lS;
    }
    return NULL;
}

char* FindStringNC(char* pBuffer, const char* S)
{
    char* p = pBuffer;
    int lS = strlen(S);
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // check if good to count, strchr also found '\0' :)
        if(strchr(AllSeparators, p[lS])!=NULL && (p==pBuffer || strchr(AllSeparators, p[-1])!=NULL))
            return p;
        p+=lS;
    }
    return NULL;
}

char* ResizeIfNeeded(char* pBuffer, int *size, int addsize) {
    char* p = pBuffer;
    int newsize = strlen(pBuffer)+addsize+1;
    if (newsize>*size) {
        //newsize += 100;
        p = (char*)realloc(pBuffer, newsize);
        *size=newsize;
    }
    return p;
}

char* Append(char* pBuffer, int* size, const char* S) {
    char* p =pBuffer;
    p = ResizeIfNeeded(pBuffer, size, strlen(S));
    strcat(p, S);
    return p;
}

int isBlank(char c)  {
    switch(c) {
        case ' ':
        case '\t':
        case '\n':
        case '\r':
        case ':':
        case ',':
        case ';':
        case '/':
            return 1;
        default:
            return 0;
    }
}

int isDigit(char value){
    return (value >= '0' && value <= '9');
}

int isValidFunctionName(char value){
    return ((value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z') || (value == '_'));
}

char* StrNext(char *pBuffer, const char* S) {
    if(!pBuffer) return NULL;
    char *p = strstr(pBuffer, S);
    return (p)?p:(p+strlen(S));
}

char* NextStr(char* pBuffer) {
    if(!pBuffer) return NULL;
    while(isBlank(*pBuffer))
        ++pBuffer;
    return pBuffer;
}

char* NextBlank(char* pBuffer) {
    if(!pBuffer) return NULL;
    while(!isBlank(*pBuffer))
        ++pBuffer;
    return pBuffer;
}

char* NextLine(char* pBuffer) {
    if(!pBuffer) return NULL;
    while(*pBuffer && *pBuffer!='\n')
        ++pBuffer;
    return pBuffer;
}

const char* GetNextStr(char* pBuffer) {
    static char buff[100] = {0};
    buff[0] = '\0';
    if(!pBuffer) return NULL;
    char* p1 = NextStr(pBuffer);
    if(!p1) return buff;
    char* p2 = NextBlank(p1);
    if(!p2) return buff;
    int i=0;
    while(p1!=p2 && i<99)
        buff[i++] = *(p1++);
    buff[i] = '\0';
    return buff;
}

int CountStringSimple(char* pBuffer, const char* S)
{
    char* p = pBuffer;
    int lS = strlen(S);
    int n = 0;
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        n++;
        p+=lS;
    }
    return n;
}

char* InplaceReplaceSimple(char* pBuffer, int* size, const char* S, const char* D)
{
    int lS = strlen(S), lD = strlen(D);
    pBuffer = ResizeIfNeeded(pBuffer, size, (lD-lS)*CountStringSimple(pBuffer, S));
    char* p = pBuffer;
    while((p = strstr(p, S)))
    {
        // found an occurence of S
        // move out rest of string
        memmove(p+lD, p+lS, strlen(p)-lS+1);
        // replace
        memcpy(p, D, strlen(D));
        // next
        p+=lD;
    }
    
    return pBuffer;
}
