# GNU Diff for Java


# Original document

I have translated the GNU Diff algorithm to a Java class. The Diff class computes the differences between two Object arrays as a list of changes. This is very general purpose. Any of the options to GNU diff can be efficiently implemented as variations on how Object.equals() is implemented and how the change list is printed.
DiffPrint now sports a setOutput() method. The DiffPrint.Base class and derivatives should really be renamed out of the empty package.

Unified and context printing now combine nearby changes.

Many people have asked me to change the license to LGPL. My port is based on GNU Diff, which is GPL. Until someone convinces me otherwise, I don't believe that I have the right to change the license. I have corresponded with the copyright holders of GNU Diff, and they are unwilling to change the license. Their position is that the GPL helps force companies to GPL more code in order to use existing GPL code.

The GPL restrictions do not apply to purely dynamically loaded code (otherwise, you would be unable to run GNU diff on a proprietary OS). When I get some time, I (or anyone who beats me to it) will create a plugin API so that applications can compile against an LGPL interface, and load the GPL implementation at runtime. This will also make comparing the performance of diff algorithms very convenient. While all Java classes are dynamically loaded at runtime, directly referenced classes are also used at compile time, and thus might be considered in violation of the GPL.

# Classes

Diff.java The Diff algorithm v1.15
DiffTest.java Test for bugs submitted by users.
DiffPrint.java A base class for printing the change list in 'ed' style to test the algorithm. Could form the basis for a complete Java implementation of all the GNU diff comparison and output options.

# TODO

Publish the revised interface that simplifies doing things with elements that are the same (as opposed to the usual requirement of dealing with just those that are different).
