#include <stdio.h>
#include <math.h>
#include <stdlib.h>

// バイナリーファイルから配列を作り出す関数
double *get_list(char *fileName, int *length){
  double check_flag;
  int i, size_impulse;
  short tmp_f;
  double *impulse;
  FILE *ifp;

  if((ifp = fopen(fileName, "rb")) == NULL){
    fprintf(stderr, "Cannot open file: %s\n", fileName);
    exit(EXIT_FAILURE);
  }

  check_flag = 0; size_impulse = 0;
  while(1){
    if (fread(&tmp_f, sizeof(float), 1, ifp) != 1) {
      check_flag = 1;
      break;
    }

    if( check_flag == 1 )
      break;

    size_impulse++;
  }

  if( (impulse=(double *) calloc( size_impulse, sizeof(double))) == NULL ){
    fprintf(stderr,"cannot allocate memory.\n");
    exit(EXIT_FAILURE);
  }

  fseek(ifp, 0, SEEK_SET);

  for(i=0;i<size_impulse;i++){
    if (fread(&tmp_f, sizeof(short), 1, ifp) != 1) {
    	size_impulse--;
    } else {
    	impulse[i] = (double)tmp_f;
    }
  }

  *length = size_impulse;
  return impulse;
}

typedef struct _complex {
	float im;
	float re;
} complex;

complex exp_j(double N) {
	complex c;
	c.re = cos(N);
	c.im = sin(N);
	return c;
}

complex *dft(complex *x, int N) {
	for () {
		
	}
}

int main (void) {
	double *y, *s, *p;
	int ylength, slength, plength;
	int samp_freq = 8000;

	y = get_list("resources/noisy_song.raw", &ylength);

}
