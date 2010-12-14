extern int printf(const char *, ...);

int main(void) {
    int x = 0;
    unsigned ux = 0;
    unsigned y = 1;
    unsigned z = 2;

    printf("x = %d\n", x);
    printf("++x = %d\n", ++x);
    printf("++x = %d\n", ++x);
    printf("--x = %d\n", --x);

    printf("+x = %d\n", +x);
    printf("-x = %d\n", -x);

    printf("x = %d\n", x);
    printf("~x = %d\n", ~x);

    printf("x-- = %d\n", x--);
    printf("x-- = %d\n", x--);
    printf("x = %d\n", x);

    printf("x << 0 = %d\n", x << 0);
    printf("x << y = %d\n", x << y);
    printf("x << z = %d\n", x << z);

    x = -450;

    printf("x = %d\n", x);
    printf("x >> 0 = %d\n", x >> 0);
    printf("x >> y = %d\n", x >> y);
    printf("x >> z = %d\n", x >> z);

    x = 450;

    printf("x = %d\n", x);
    printf("x >> 0 = %d\n", x >> 0);
    printf("x >> y = %d\n", x >> y);
    printf("x >> z = %d\n", x >> z);

    ux = 450;

    printf("ux = %d\n", ux);
    printf("ux >> 0 = %d\n", ux >> 0);
    printf("ux >> y = %d\n", ux >> y);
    printf("ux >> z = %d\n", ux >> z);

    printf("x + x = %d\n", x + x);
    printf("x - x = %d\n", x - x);

    printf("x + ux = %u\n", x + ux);
    printf("x - ux = %u\n", x - ux);

    printf("ux + x = %u\n", ux + x);
    printf("ux - x = %u\n", ux - x);

    printf("ux + ux = %u\n", ux + ux);
    printf("ux - ux = %u\n", ux - ux);

    printf("x > x = %d\n", x > x);
    printf("x >= x = %d\n", x >= x);
    printf("x < x = %d\n", x < x);
    printf("x <= x = %d\n", x <= x);
    printf("x == x = %d\n", x == x);
    printf("x != x = %d\n", x != x);

    printf("cond result: %s\n", 0 ? "yes" : "no");
    printf("cond result: %s\n", 1 ? "yes" : "no");

    printf("x = %d\n", x);
    printf("x += x = %d\n", x += x);
    printf("x *= x = %d\n", x *= x);
    printf("x /= x = %d\n", x /= x);
    printf("x -= x = %d\n", x -= x);

    return 0;
}
