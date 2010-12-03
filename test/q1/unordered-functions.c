
/* note: this is valid code and no errors are expected. */

int a(void); /* forward declaration */

int main(void) {
    return a();
}

int c(void) {
    return b();
}

int b(void) {
    return a();
}

int a(void) {
    return 0;
}
