
extern int printf(const char *, ...);

static void test_switch(int x) {
    switch(x) {
    case 1: printf("got 1, falling through.\n");
    case 2: printf("got 2\n"); break;
    case 3: printf("got 3\n"); break;
    default: printf("got default with %d\n", x);
    }
    printf("\n");
}

int main(void) {

    test_switch(0);

    test_switch(1);

    test_switch(2);

    test_switch(3);

    test_switch(4);

    return 0;
}
