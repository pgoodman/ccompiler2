
typedef struct {
    int a;
    float b;
    double c;
} struct_t;

static void by_pointer(struct_t *x) {
    x->c = 10.0;
}

int main(void) {
    extern int printf(const char *, ...);
    struct_t test;
    by_pointer(&test);
    printf("test.c = %f\n", test.c);
    return 0;
}
