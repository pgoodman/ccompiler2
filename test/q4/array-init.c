

int x[2] = {73, 67};

extern int printf(const char *, ...);

int main(void) {
    printf("x[0] = %d, x[1] = %d, %d\n", *x, x[1], *"");
    return 0;
}
