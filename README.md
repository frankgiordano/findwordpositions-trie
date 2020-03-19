# findwordpositions-trie

A program that implements word dictionary with location positions stored from text file using trie structure.

This program implements word dictionary with positions stored. It implements something similar to the
first half of the following link:

http://www.ardendertat.com/2011/12/20/programming-interview-questions-23-find-word-positions-in-text/

Thanks for the following code snippet for reference at the following site:
http://alexcode.tumblr.com/question_9

author: Frank Giordano 11/26/2015


NOTE: This program stores the first character position of the word within the line number. It does not store the occurence position of the entire string word.

For instance:

"test frank giordano"

test located at line 1, position 0 frank located at line 1, position 5 etc

whereas all the words positions would be: test located at position 0 and frank located at position 1 and giordano located at position 3
