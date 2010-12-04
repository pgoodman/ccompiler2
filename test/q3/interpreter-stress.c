
/* make sure to set Env.DEBUG on! */

/* this file is meant to stress test the compile-time evaluation of
 * constant expressions and to make sure that the compiler figures out
 * the sizes of types.
 *
 * Note: all type sizes are rounded up to the nearest multiple of 4 bytes.
 *       this is simplifies any alignment requirements, as well as follows
 *       ANSI standards, which stipulate minimum sizes only.
 */

typedef enum _foo {
    FOO,
    BAR = 10,
    BAZ
} foo;

int main(void) {

    int arr1[];
    int arr2[3];
    int arr3[BAR], *arr4;
    int arr5[][BAR][BAZ];
    int arr6[BAR][BAZ];

    /* enum sizes */
    sizeof FOO;
    sizeof BAR;
    sizeof(enum _foo);
    sizeof(foo);

    /* basic types */
    sizeof(char);
    sizeof(unsigned char);
    sizeof(int);
    sizeof(unsigned int);
    sizeof(long int);
    sizeof(float);
    sizeof(double);
    sizeof(long double);

    /* basic pointer types */
    sizeof(char *);
    sizeof(unsigned char *);
    sizeof(int *);
    sizeof(unsigned int *);
    sizeof(long int *);
    sizeof(float *);
    sizeof(double *);
    sizeof(long double *);

    /* array sizes */
    sizeof arr1;
    sizeof arr2;
    sizeof arr3; /* expect: 4 * 10 = 40 */
    sizeof arr4; /* pointer size */
    sizeof arr5; /* pointer size */
    sizeof arr6; /* expect 10 * 11 * 4 = 440 */
}