extern int printf(char *, ...);

int factorial(int n) {
	int i, p;
	
	p = 1;
	for (i = 1; i <=n; i++)
		p = p*i;
	return p;
}

int main() {
	int f;
	
	f = factorial(5);
	printf("The value is %d\n", f);
	return 0;
}