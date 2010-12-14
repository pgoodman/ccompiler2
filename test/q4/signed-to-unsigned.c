
extern int printf(const char *, ...);

static unsigned drop_sign(int x) {
    return (unsigned) x;
}

static int add_sign(unsigned x) {
    return (int) x;
}

int main(void) {

    printf("%u\n", drop_sign(10));
    printf("%u\n", drop_sign(~0));
    printf("%u\n", drop_sign(-10));

    printf("%d\n", add_sign(10));
    printf("%d\n", add_sign(~0));

    return 0;
}
