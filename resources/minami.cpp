#include <complex>
#include <iostream>
#include <fstream>
#include <math.h>
#include <stdlib.h>
using namespace std;
 
//dft
void dft(complex<double> *x,complex<double> *X,int S,int N){
   int k,n;
   complex<double> e;
   
   for(k=S; k<S+N; k++){
       X[k] = 0;
       for(n=S; n<S+N; n++){
           e.real(cos(2*M_PI*k*n/N));
           e.imag(-sin(2*M_PI*k*n/N));
           X[k]=X[k]+x[n]*e;
       }
   }
}
 
//idft
void idft(complex<double> *x,complex<double> *X,int S,int N){
   int k,n;
   complex<double> e;
 
   for(n=S; n<S+N; n++){
       x[n] = 0;
       for(k=S; k<S+N; k++){
           e.real(cos(2*M_PI*k*n/N));
           e.imag(sin(2*M_PI*k*n/N));
           x[n]=x[n]+X[k]*e;
       }
 
       x[n]/=N;
   }
}
 
int main(int argc, const char * argv[]){
   complex<double> *y;
   complex<double> *Y;
   complex<double> *P;
   complex<double> *S;
   double *theata;
 
   short dummy_s;
   FILE *in_file,*out_file;
   int length=0,i=0,samplef=8000,L;
   float msec=0.032;
   L=samplef*msec;
 
   //ファイルの準備
 
   if((in_file=fopen("noisy_song.raw", "rb"))== NULL){
       printf("open error0¥n");
       return -1;
   }
   
   if ( (out_file=fopen("out.raw", "wb")) == NULL ) {
       printf("open error1¥n");
       return -1;
   }
   
   while (fread(&dummy_s, sizeof(short), 1, in_file))
       length++;
       
    fseek(in_file, 0L, SEEK_SET);
 
   y = new complex<double>[length];
   Y = new complex<double>[length];
   P = new complex<double>[L];
   S = new complex<double>[length];
 
   theata = (double*)malloc(length*sizeof(double));
   while (fread(&dummy_s,sizeof(short),1,in_file)) {
       y[i]=dummy_s;
       i++;
   }
   
   //短時間dft
   i=0;
 
   while (i<length) {
      if(i+L>length){
           L=length-i+1;
      }
      dft(y, Y, i, L);
      i=i+L;
   }
   
   L=samplef*msec;
  
   //雑音計算
   for (int j=0; j<L; j++) {
       for (i=0; i<31; i++) {
           P[j].real( P[j].real() + abs(Y[i*L+j]) );
       }
       P[j]/=31;
       //printf("%f, ", P[j].real());
   }
   
   //偏角計算
   for (int i=0; i<length; i++)
       theata[i]=arg(Y[i]);
       
   //スペクトルサブストラクション
   i=0;
   L=samplef*msec;
   double r;
   while (i<length) {
      r=abs(Y[i])-abs(P[i%L]);
      // printf("%lf, ", r);
      if (r<0) {
          r=0.01*abs(Y[i]);
      }
      S[i].real(r*cos(theata[i]));
      S[i].imag(r*sin(theata[i]));
      // printf("%f, %f\n", S[i].real(), S[i].imag());
      i++;
   }
   printf("\n");   
  //idft
  i=0;
  while (i<length) {
      if(i+L>length){
          L=length-i+1;
      }
      idft(y, S, i, L);
      i=i+L;
  }
   
   i=0;
 
   while (i<length) {
       dummy_s=y[i].real();
       printf("%d\n", dummy_s);
       fwrite(&dummy_s, sizeof(short), 1, out_file);
       i++;
   }
   
   fclose(in_file);
   fclose(out_file);
   delete [] y;
   delete [] Y;
   delete [] S;
   delete [] P;
   free(theata);
   return 0;
}