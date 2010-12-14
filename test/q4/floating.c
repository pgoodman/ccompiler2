
extern int printf(const char *, ...);

int main(void) {
    float x = 9.0;

    printf("x = %f\n", x);
    printf("++x = %f\n", ++x);
    printf("++x = %f\n", ++x);
    printf("--x = %f\n", --x);

    printf("+x = %f\n", +x);
    printf("-x = %f\n", -x);

    return 0;
}
