Here is the MIPT students's project within confines of "modern language concepts" course.

The goal is in developement of two simple risc architectures: stack(the working title: "StAr") and register based; and coding a compiler and assembler for StAr, interpreters for both, and a binary translator from the first to the second one.

== Compiler is ready ==

It lacks predefined functions, thery will be on as soon as their list will be compiled.
To use compiler, you do:

    $ cd compiler.star
    $ mvn package
    $ ./compile.sh -help
    
It will show you options for compiling streams. The simplest usage scenario is:

    $ ./compile.sh program.calc -o prog.asm
    
This will produce prog.asm, representing algorithm from program.calc. For now compiler supports two variants of statements:

    {assignment} a = b + c
    {output} print a, b, c
    
Now input clauses, as the original task beneathed. It is insensitive to extra spaces and empty lines. Variables beginning with i,j,k,l,m,n and literals [0-9]+ are integers, other variables, literals, containing a dot are floating point, and functions are floating point. Types are being converted automatically. Arrays are multidimensional, they are indexed by [i1, i2, i3] expression. In assignment "a = b, c, d, e" is possible, and all expressions b, c, d, e will be evluated, but they will stay in the stack, and a will be assigned to e's value.