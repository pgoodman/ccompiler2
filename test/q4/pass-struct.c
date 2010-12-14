
typedef struct {
    int a;
    float b;
    double c;
    char z;
    double y;
} struct_t;

extern int printf(const char *, ...);

static void by_double_pointer(struct_t **y);
static void by_triple_pointer(struct_t ***z);

static void by_value(struct_t test) {
    printf("a = %d, b = %f, c = %lf, z = %c\n", test.a, test.b, test.c, test.z);
}

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
    struct_t test, *test_addr = &test;
    test.a = 9;
    by_pointer(&test);


    printf("a = %d, b = %f, c = %lf, z = %c\n", test.a, test.b, test.c, test.z);

    by_value(test);

    return 0;
}
