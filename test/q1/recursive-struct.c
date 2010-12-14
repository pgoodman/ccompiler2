
struct foo {
    struct foo *self; /* good */
};

struct bar {
    struct bar self; /* bad */
};
