
/*
 * declare foo as function
 *      returning int
 */
typedef int (foo)(float);

/*
 * declare bar as function (pointer to char)
 *      returning function (pointer to int)
 *          returning function (pointer to void, pointer to void)
 *              returning int
 */
typedef int (((bar(char *)))(int *))(void *, void *);

/*
 * declare fizz as function (pointer to float)
 *      returning pointer
 *          to pointer
 *              to array
 *                  of pointer
 *                      to int
 */
typedef int *(**fizz(float *))[];

typedef void buzz[](void); /* expect error */
