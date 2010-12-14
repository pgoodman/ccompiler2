
struct {
    int (foo)(); /* expect error */
};

int main() {
    int (baz)();

    typedef int bar;
    typedef int (fizz)();
}
