
extern int printf(const char *, ...);

int main(void) {
    int arr[3] = {1, 2, 3};
    int *arr_ptr = &(arr[0]);

    printf("arr[0] = %d, arr[1] = %d, array[2] = %d\n", arr[0], arr[1], arr[2]);
    printf("*arr_ptr = %d\n", *arr_ptr);
    printf("*++arr_ptr = %d\n", *++arr_ptr);
    printf("*++arr_ptr = %d\n", *++arr_ptr);
    printf("*--arr_ptr = %d\n", *--arr_ptr);
    printf("*--arr_ptr = %d\n", *--arr_ptr);

    printf("null pointer = %p\n", (void *) 0);
    printf("(int) arr_ptr = %d\n", (int) arr_ptr);
    printf("(long int) arr_ptr = %ld\n", (long int) arr_ptr);
    printf("(unsigned) arr_ptr = %u\n", (unsigned) arr_ptr);
    printf("(unsigned long) arr_ptr = %lu\n", (unsigned long) arr_ptr);
    return 0;
}
