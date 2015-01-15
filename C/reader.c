#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv){
  short check_flag;
  int i, size_impulse;
  short *impulse, tmp_f;
  FILE *ifp;

  if((ifp = fopen(argv[1], "rb")) == NULL){
    fprintf(stderr, "Cannot open file: %s\n", argv[1]);
    exit(EXIT_FAILURE);
  }


  check_flag = 0; size_impulse = 0;
  while(1){
    if (fread(&tmp_f, sizeof(short), 1, ifp) != 1) {
      check_flag = 1;
      break;
    }

    if( check_flag == 1 )
      break;

    size_impulse++;
  }

  if( (impulse=(short *) calloc( size_impulse, sizeof(short))) == NULL ){
    fprintf(stderr,"cannot allocate memory.\n");
    exit(EXIT_FAILURE);
  }

  fseek(ifp, 0, SEEK_SET); // You can read the data file again.

  for(i=0;i<size_impulse;i++){
    if (fread(&tmp_f, sizeof(short), 1, ifp) != 1) {
      fprintf(stderr, "Error\n");
    }
    impulse[i] = tmp_f;
    fprintf(stderr, "%d\n", tmp_f);
    printf("%d\n", tmp_f);
  }

  return EXIT_SUCCESS;
}