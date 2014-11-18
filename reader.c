#include <stdio.h>

int main(void) {
  FILE *fp;
  char *fname = "impulse_16khz.dat";
  float buf[100000];
  int  i, size;

  fp = fopen( fname, "rb" );
  if( fp == NULL ){
    printf( "%sファイルが開けません¥n", fname );
    return -1;
  }

  size = fread( buf, sizeof( float ), 100000, fp );

  for( i=0; i<size; i++ ){
    printf( "%f\n", buf[i] );
  }

  fclose( fp );
  return 0;
}
