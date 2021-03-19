#ifndef MD5_H
#define MD5_H

#ifdef __cplusplus
extern "C" {
#endif

typedef unsigned int UINT4;

#ifndef PROTOTYPES
#define PROTOTYPES 1
#endif

typedef unsigned char *POINTER;

typedef unsigned short int UINT2;

#if PROTOTYPES
#define PROTO_LIST(list) list
#else
#define PROTO_LIST(list) ()
#endif

typedef struct {
    UINT4 state[4];                  /* state (ABCD) */
    UINT4 count[2];                  /* number of bits, modulo 2^64 (lsb first) */
    unsigned char buffer[64];        /* input buffer */
} MD5_CTX;

void MD5Init PROTO_LIST ((MD5_CTX * ));

void MD5Update PROTO_LIST
((MD5_CTX * ,
         unsigned char *, unsigned int));

void MD5Final PROTO_LIST ((unsigned char[16], MD5_CTX *));

void md5(const unsigned char *input, unsigned int inputLen, unsigned char *output);

#ifdef __cplusplus
}
#endif
#endif