#include<stdio.h>
#include<string.h>
#include<stdlib.h> 
#define MAX 1024
struct term{
	long coeff;
	long exp;
}; 
typedef struct POLY{
	int NumTerm;
	struct term terms[MAX]; 
}poly,* polyptr;

void DeleteSpace(char s[MAX], char polynomial[MAX]){
	int i=0,j=0;
	while(s[i]!='\0'&&s[i]!='\n'){
		if(s[i]!=' ') polynomial[j++]=s[i++];
		else i++;
	}
	polynomial[j]='\0';
}
void InsertTerm(int state,long a, long b, polyptr polys){
	int i;
	for(i = 0; i<polys->NumTerm; i++){
		if(b==polys->terms[i].exp){
			polys->terms[i].coeff += (state? -a:a);
			break;
		}
	}
	if(i==polys->NumTerm){
		polys->terms[i].coeff= (state? -a:a);
		polys->terms[i].exp=b;
		polys->NumTerm++;
	}
}

void ReadPoly(char polynomial[MAX], polyptr polys){
	int lens, i, j, k;
	int state=0;
	int sign=0;
	long a, b;
	char tem[30]; 

	for(i = 0; i<MAX; i++) 
		polys->terms[i].coeff=0, polys->terms[i].exp=0;
	lens=strlen(polynomial);
	polys->NumTerm=0;

	for(i = 0; i < lens; i++){
		if(polynomial[i]=='+')
			state=0;
		else if(polynomial[i]=='-')
			state=1;
		else if(polynomial[i]=='{'){
			int NumComma = 0;
			i++;
			for(k = 0; k<30; k++) tem[k]='\0';
			for(j = 0; i<lens; ){
				if(polynomial[i]!='}'){
					if(polynomial[i]==',') NumComma++;
					if(polynomial[i]==','&& NumComma%2==0) {
						sscanf(tem, "(%ld,%ld)", &a,&b);
						InsertTerm(state, a, b, polys);
						for(k = 0; k<30; k++) tem[k]='\0';
						j=0;

					}
					else{
						tem[j++]=polynomial[i];
					}
				}
				else
					break;
				i++;
			}
			sscanf(tem, "(%ld,%ld)", &a,&b);
			InsertTerm(state, a, b, polys);
		}
		else{
			return 0;
		}
	}
}
void SortPoly(polyptr polys){
	int i, j;
	int wrong=1;
	struct term temp;
	for(i = polys->NumTerm-1; i>0 && wrong==1 ; i--){
		wrong = 0;
		for(j = 0; j<i; j++){
			if(polys->terms[j].exp > polys->terms[j+1].exp){
				temp=polys->terms[j];
				polys->terms[j] = polys->terms[j+1];
				polys->terms[j+1] = temp;
				wrong=1;
			}
		}
	}
}
void PrintPoly(polyptr polys){
	int i,first;
	for(i = 0,first=0; i<polys->NumTerm; i++){
		if(i==0){
			printf("{");
			if(polys->terms[i].coeff==0)
				continue; 
		}
		else
			if(polys->terms[i].coeff==0)
				continue; 
			if(first>0)
				printf(",");
		printf("(%ld,%ld)", polys->terms[i].coeff, polys->terms[i].exp);
		first++;
	}
	printf("}");
}
int main(){
	char s[MAX], polynomial[MAX];
	polyptr polys;
	polys=(polyptr)malloc(sizeof(poly));
	fgets(s,100000,stdin);
	DeleteSpace(s, polynomial);
	ReadPoly(polynomial, polys);
	SortPoly(polys);
	int notZero, i;
	for(i=0, notZero=0;i<polys->NumTerm;i++){
		if(polys->terms[i].coeff!=0) {
			notZero++;
		}
	}
	if(notZero==0)
		printf("0");
	else
		PrintPoly(polys);
	return 0;
}
