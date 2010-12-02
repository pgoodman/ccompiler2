
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
