
typedef struct {
    int a;
    float b;
    double c;
} struct_t;

static struct_t foo(struct_t x) {
    x.c = 10.0;
    return x;
}

int main(void) {
    struct_t test;
    foo(test);
    return 0;
}
