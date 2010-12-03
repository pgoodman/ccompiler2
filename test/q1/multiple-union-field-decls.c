
union {
    int foo;
    int bar;
    int foo; /* expect error */
};
