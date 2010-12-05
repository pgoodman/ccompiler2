
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

int c(int x) {
    return 0;
}

float foo(a, b, c)
    int c;
    float b, a;
{
    return a * b * c;
}
