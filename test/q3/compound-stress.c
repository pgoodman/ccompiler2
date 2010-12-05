
int main(void) {

    struct {
        double foo; /* 8 */
        int bar; /* 4 */
        int (*foo_bar)(void *, void *); /* 8 */
        int (*baz[2][sizeof(struct { float x[10];})])(void *, void *);
    } foo;

    sizeof foo;
    sizeof foo.foo;
    sizeof foo.bar;
    sizeof foo.foo_bar;

    return 0;
}
