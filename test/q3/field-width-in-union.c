
struct {
    int foo:10;
};

union {
    int foo:10; /* expect error */
};
