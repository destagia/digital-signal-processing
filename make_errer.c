#include <stdio.h>
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

// 配列からバイナリーファイルを作成する
void make_file(char *filename, double *data, int length){
	int n;
	short tmp;
	FILE *file_output;

	if((file_output = fopen(filename,"wb")) == NULL){
	fprintf(stderr, "Cannot write %s\n", filename);  exit(-1);
	}

	for(n=0;n<length;n++){
		tmp = (short)data[n];
		fwrite(&tmp, sizeof(short), 1, file_output);
	}

	fclose(file_output);
}

int main(void){
	int K = 80;
	int sampfreq = 8000;
	int samptime = 50;
	int voicetime = 57;
	int voicelength = 0;
	int voiceend = sampfreq * 65;
	double *d, *x, *e, *s;
	double *impulse;
	double **h;
	double *res1,*res2;
	double alpha = 0.02;
	double error2sum = 0;
	int dlength = 0;
	int xlength = 0;
	int ilength = 0;
	int length = voiceend;
	int i,n,k;
	double convolution = 0.0000000, X = 0.0;

	d = get_list("resources/observed_song2.raw", &dlength);
	x = get_list("resources/song2.raw", &xlength);

	impulse = get_list("resources/imp.raw", &ilength);
	// printf("finish read file.\n");
	// 誤差eはxと同じ長さ。
	e = (double*)calloc(length, sizeof(double*));
	s = (double*)calloc(length, sizeof(double*));

	// フィルタ係数はxの長さと同じだけ用意する。
	h = (double**)calloc(length, sizeof(double*));
	// h0はすべての値が0でスタートする。
	h[0] = (double*)calloc(K, sizeof(double));
	for (i=0;i<K;i++) {
		h[0][i] = 0.00000000000000;
	}
	// printf("start calc filter coefiicient.\n");
	// h1以降の誤差eとフィルタ係数hの値を求める。

	for (n=0;n<voiceend-1;n++) {

		// 誤差値の計算
		for (k=0;k<K;k++) {
			convolution += h[n][k] * x[n-k];
		}
		e[n] = d[n] - convolution;
		convolution = 0;

		// 次のフィルタ係数h[n+1]を算出
		for (k=0;k<K;k++) {
			X += x[n-k] * x[n-k];
		}
		if (X == 0.0) {
			X = 1.0;
		}

		h[n+1] = (double*)calloc(K, sizeof(double));
		for (k=0;k<K;k++) {
			h[n+1][k] = h[n][k] + ((alpha/X)*e[n]*x[n-k]);
		}
		X = 0.0;

		for (k=0;k<K;k++) {
			convolution += h[n][k]*x[n-k];
		}
		s[n] = d[n] - convolution;
		convolution = 0;
	}

	voicelength = voiceend-(voicetime*sampfreq);
	res1 = (double*)calloc(voicelength, sizeof(double));
	res2 = (double*)calloc(voicelength, sizeof(double));
	for (n=0;n<voicelength;n++) {
		res1[n] = d[n+voicetime*sampfreq];
		res2[n] = s[n+voicetime*sampfreq];
	}

	// make_file("only_voice.raw", res2, voicelength);
	// make_file("song_voice.raw", res1, voicelength);

	for(k=0;k<K;k++){
		// printf("%lf\n", h[3*sampfreq][k]);
		// printf("%lf\n", h[40*sampfreq][k]);
		// printf("%lf\n", impulse[k]);
	}

	// make_file("error80.raw", e, length);
	// printf("%lf\n", error2sum / length);
	return EXIT_SUCCESS;
}
