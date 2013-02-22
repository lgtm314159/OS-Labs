1) Change directory to lab2-xin-junyang/lab2 (suppose you are already under
lab2-xin-junyang/):

   cd lab2

>>>>> Please make sure you are in directory lab2-xin-junyang/lab2 before running any of the following commands! <<<<<

2) Compile all the java files by running:

   bash compileLab2.sh

3) Create a text file which contains the input data, let say the filename is 'process.txt':

   vim process.txt

   And paste the input data into the file. You can then replace the content of this file with new input data.

4) Create a text file which contains the random numbers provided, let say the filename is 'random-numbers.txt'.

   vim random-numbers.txt

   And paste the input data into the file. You can then replace the content of this file with new input data.

5) Run the program against the 'process.txt' and 'random-numbers.txt' files, along with the algorithm specified:

   a. FCFS
   
      bash runLab2.sh process.txt random-numbers.txt fcfs
   
   b. RR
   
      bash runLab2.sh process.txt random-numbers.txt rr
   
   c. Uniprogramming
   
      bash runLab2.sh process.txt random-numbers.txt unip
   
   d. HPRN

      bash runLab2.sh process.txt random-numbers.txt hprn

   The result will be printed out to stdout.

