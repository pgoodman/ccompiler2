
struct {
    int (foo)(); /* expect error */
};

int main() {
    int (baz)(); /* expect error */

    typedef int bar;
    typedef int (fizz)();
}
