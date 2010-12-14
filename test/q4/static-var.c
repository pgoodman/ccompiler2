extern int printf(const char *, ...);

static void test_static(int x) {
    static int y = 0;

    if(x == 2) {
        y = -1;
    }

    printf("x = %d, y = %d\n", x, y);
}

int main(void) {
    test_static(0);
    test_static(1);
    test_static(2);
    test_static(3);
    test_static(4);
    test_static(5);

    return 0;
}
