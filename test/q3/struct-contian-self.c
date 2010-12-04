
struct foo {
    int moo;
    struct foo bar; /* expect error */
};

struct bar {
    int moo;
    struct bar *bar;
};
