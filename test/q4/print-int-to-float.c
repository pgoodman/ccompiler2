
float temp(double x) {
    return (float) x;
}

void print_int_to_float(float x) {
    extern int printf(const char *, ...);
    printf("%f\n", x);
}

int main(void) {
    print_int_to_float((float) 10);
    return 0;
}
