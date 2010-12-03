/* extern int g(int (*)(double, double []); */

extern int printf(const char *, ...);
extern char *strcpy(const char * const, char *);

int f(x, py)
	double x, *py[];
{
	printf("%g", x, *py);
}

struct complex {
	double x;
	double y;
};

typedef union simple {
	struct complex z;
	struct { int i, j; }  point;
	enum { RED, GREEN, BLUE = 10 } color;
} sunion, *psunion;

int main(int argc, char **argv){
	int i;
	double z = 1.0;
	union simple s;
	struct complex ca[];
	psunion ps[10];
	char * volatile * const *ppps;
	
top:
	
	for (i = 0; i < argc; i++) {
		printf("%d: %s\n", i, argv[i]);
		printf("%d: %s\n", i, i[argv]);
		printf("%d: %s\n", i, i[i]);
	}
middle:

	printf("%d, %g\n", s.point.i);
	printf("%g\n", ps[1]->z.x * ps[2]->z.y);
	printf("%g\n", ps[1]->z.x << ps[2]->z.y);
	printf("%g\n", ps->z.x);
	if (i == argc)
		printf("That was all\n");
	else
		while (i > argc)
			i--;
	f(3.14, &z);
	if (0 || 1) goto hell;
	if (1 && 2) goto middle;
	if (1 | i ) goto middle;
	if (1 | s ) goto middle;
	i = i + 2;
	j = i;
	return 0;
}
