
struct foo {
    struct {
        int bar[sizeof(struct foo)]; /* expect error */
    } baz;
};
