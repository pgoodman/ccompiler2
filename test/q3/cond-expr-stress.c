
int main(void) {
    float x[10];
    int y[10];

    1 ? 0.0 : 1;

    1.0 ? -1 : 1;

    (void *) 1 ? (void *) 0 : 1.0; /* expect error */

    0 ? main : (void *) 0;

    0 ? main : &main;

    x == y ? x : y; /* expect error */

    return 0;
}
