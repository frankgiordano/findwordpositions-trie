# findwordpositions-trie
A program that implements word dictionary with location positions stored from text file using trie structure

/*
 * author: Frank Giordano 11/26/2015
 * program implements word dictionary with positions stored
 * implements second half of the following link
 * http://www.ardendertat.com/2011/12/20/programming-interview-questions-23-find-word-positions-in-text/
 * this program uses trie data structure.
 * difference from my programs doing the same with a Hash structure is here I take words into consider only.
 * non alphabetic characters are ignored. 
 * 
 * Thanks for the following code snippet for reference at the following site:
 * http://alexcode.tumblr.com/question_9
 * 
 */


Please note this problem stores the first character position of the word within the line number.. It does not store the occurence position of the entire string word..

For instance,

"test frank giordano"

test located at line 1, position 0 frank located at line 1, position 5 etc

whereas entire word position would be: test located at position 0 and frank located at position 1 and giordano located at position 3
