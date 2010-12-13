
typedef struct {
    int a;
    float b;
    double c;
    char z;
    double y;
} struct_t;

static void by_double_pointer(struct_t **y);
static void by_triple_pointer(struct_t ***z);

static void by_pointer(struct_t *x) {
    x->c = 10.0;
    (*x).a = 77;
    by_double_pointer(&x);
}

static void by_double_pointer(struct_t **y) {
    (*y)->c = 99.505;
    by_triple_pointer(&y);
}

static void by_triple_pointer(struct_t ***z) {
    (*(*z))->z = 'X';
}

int main(void) {
    extern int printf(const char *, ...);
    struct_t test, *test_addr = &test;
    test.a = 9;
    by_pointer(&test);


    printf("a = %d, b = %f, c = %lf, z = %c\n", test.a, test.b, test.c, test.z);

    return 0;
}
