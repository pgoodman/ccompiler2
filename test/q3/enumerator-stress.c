
enum {
    A = 'A',
    B = 1,
    X = 0xAA,
    C = 1.0, /* expect error */
    D = "abc", /* expect error */
    E = ""[0]
};
