
const int bar = 99;
const int zz = bar;
int foo[] = {0, 1};

int test(int x) {
    int baz = x;
    return baz;
}

void hello() {
    foo[0] = bar;
}

int main(void) {
    int baz = 7;
    test(baz);
    hello();
    return 0;
}
