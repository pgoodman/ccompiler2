
extern int printf(const char *, ...);

static int foo(int x) {
    printf("calling with %d\n", x);
    return x;
}

int main(void) {
    printf("testing &&:\n");
    printf("%d\n\n", foo(0) && foo(0));
    printf("%d\n\n", foo(0) && foo(1));
    printf("%d\n\n", foo(1) && foo(0));
    printf("%d\n\n", foo(1) && foo(1));
    printf("testing ||:\n");
    printf("%d\n\n", foo(0) || foo(0));
    printf("%d\n\n", foo(0) || foo(1));
    printf("%d\n\n", foo(1) || foo(0));
    printf("%d\n\n", foo(1) || foo(1));
    return 0;
}
