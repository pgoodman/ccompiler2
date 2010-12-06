
int a(int x)
    float x; /* expect error */
{
    return 0;
}

int b(x)
    float x;
{
    return 0;
}

typedef int (func_t)(void);

int c(int x, func_t foo /* expect error */) {
    return 0;
}

int d(func_t) { /* expect error */
    return 0;
}

float foo(a, b, c, e)
    func_t e; /* expect error */
    int c;
    float b, a;
{
    func_t bar; /* expect error */
    int (baz)(void *);
    return a * b * c; /* expect error */
}
