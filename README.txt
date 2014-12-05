Multi-Faceted Topic Modeling Package
------------------------------------

Copyright (c) 2009-2010 Semantic Frontiers Group
University of Illinois at Urbana-Champaign
http://apfel.ai.uiuc.edu/

Author: Michael Paul <mpaul39@gmail.com>


Please cite one of the following papers (whichever is more relevant) in any work 
that uses this material:

@inproceedings{PaulGirju09,
    address = {Singapore},
    author = {Paul, Michael and Girju, Roxana},
    booktitle = {Proceedings of the 2009 Conference on Empirical Methods in Natural Language Processing},
    month = {August},
    pages = {1408--1417},
    publisher = {Association for Computational Linguistics},
    title = {Cross-Cultural Analysis of Blogs and Forums with Mixed-Collection Topic Models},
    url = {http://www.aclweb.org/anthology/D/D09/D09-1146.pdf},
    year = {2009}
}

@inproceedings{PaulGirju10,
    address = {Atlanta, Georgia},
    author = {Paul, Michael and Girju, Roxana},
    booktitle = {Proceedings of the Twenty-Fourth AAAI Conference on Artificial Intelligence (AAAI-10)},
    month = {July},
    pages = {545--550},
    title = {A Two-Dimensional Topic-Aspect Model for Discovering Multi-Faceted Topics},
    url = {http://www.aaai.org/ocs/index.php/AAAI/AAAI10/paper/download/1730/2034},
    year = {2010}
}

The Multi-Faceted Topic Modeling Package is a free software; you can 
redistribute it and/or modify it under the terms of the GNU General Public 
License as published by the Free Software Foundation; either version 2 of the 
License, or (at your option) any later version.

The Multi-Faceted Topic Modeling Package is distributed in the hope that it will 
be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public 
License for more details.

You should have received a copy of the GNU General Public License along 
with this software; if not, write to the Free Software Foundation, Inc., 
59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.

================================================================================

1. Introduction
---------------

This software package includes implementations of ccLDA and TAM (described
in the papers above) in addition to a one-dimensional LDA implementation.
Please refer to the papers for an overview of the models. The command line 
parameters for this software will follow the notation from the papers.


2. Revision History
-------------------

This current implementation offers minimal functionality, so it is conceivable
that more may be added in the future. In particular, this implementation 
currently only offers a way of running inference on training documents and not
on new unseen documents. It would be a good idea to periodically check if there 
is a new version of this software available, in case new features are added, or 
bugs are corrected.

v0.16: 5/07/2011 - Fixed a bug related to the previous bug correction. TAM
                   was not behaving correctly under the previous version.
v0.15: 4/23/2011 - Two bug corrections, both for the TAM model. First, an array 
                   for aspect counts was not allocated to the correct size, which 
                   would have caused the problem to crash under certain parameter 
                   settings (if Z > Y). Second, two of the counts used in
                   computing sampling probabilities for the x/l variables were 
                   switched. Many thanks to Shima Gerani for pointing these out.

v0.1: 11/11/2010 - Initial release.


3. Installation
---------------

This program is not implemented as a Java package, but it would be a good idea
to keep all the source files in the same directory. Straightforward Java
compilation can be done with the following command:

> javac *.java


4. Usage
--------

To run the program, enter the command:

> java LearnTopicModel -model <model_name> -input <input_file> [-iters <int>] <model-specific parameters>


<model_name> can be one of: lda | cclda | tam
<input_file> is the filename of the input (format described in a later section).

The optional parameter -iters specifies the number of Gibbs sampling iterations
to perform. If unspecified, this defaults to 100.

Additionally, each of the three models has its own command-line parameters,
which are described below.

When the program finishes, it writes the final variable assignments to the file
<input_file>.assign -- each model has its own output format, described in the 
subsections below. The variables theta, phi, etc. can be computed from this
output. For convenience, python scripts are included to print out the top
words for the topics. Each model has its own output script (topwords.py,
topwords_cclda.py, topwords_tam.py), but they are all used in the same way:

> python topwords.py input_docs.txt.assign > output_topwords.txt


4.1. LDA
--------

This package includes an implementation of a standard topic model with a 
background distribution, which is called by supplying "lda" to the -model
parameter. This is essentially the basic LDA model augmented with a background
distribution for common/non-topical words (similar to what is used in 
(Chemudugunta et al, 2006)).

The command-line parameters for this model are:

-Z <int>                  The number of topics.
[-alpha <double>]         The prior for document-topic distributions. 
                          Default 1.0.
[-beta <double>]          The prior for topic-word distributions. Default 0.01.
[-gamma0 <double>]        The prior for belonging to the background.
                          Default 1.0.
[-gamma1 <double>]        The prior for not belonging to the background.
                          Default 1.0.

Example usage:

> java LearnTopicModel -model lda -input input_docs.txt -iters 500 -Z 100 -alpha 0.1 -gamma0 90 -gamma1 10

The output format is the same as the input format, except each word token
"word" has been replaced with "word:z:x" where z is the integer topic assignment
and x is the integer assignment denoting if the token was assigned to the 
background distribution (0) or not (1).


4.2. ccLDA
--------

This package includes an implementation of ccLDA (cross-collection LDA)
as described in (Paul & Girju, 2009), which is called by supplying "cclda" to
the -model parameter.

The command-line parameters for this model are:

-Z <int>                  The number of topics.
[-alpha <double>]         The prior for document-topic distributions. 
                          Default 1.0.
[-beta <double>]          The prior for topic-word distributions. Default 0.01.
[-delta <double>]         The prior for collection-specific topic-word 
                          distributions. Default 0.01.
[-gamma0 <double>]        The prior for belonging to the collection-independent
                          topic. Default 1.0.
[-gamma1 <double>]        The prior for belonging to the collection-specific
                          topic. Default 1.0.

Example usage:

> java LearnTopicModel -model cclda -input input_docs.txt -iters 2000 -Z 30 -gamma0 10 -gamma1 10

The output format is the same as the input format, except each word token
"word" has been replaced with "word:z:x" where z is the integer topic assignment
and x is the integer assignment denoting if the token was assigned to the 
collection-independent distribution (0) or the collection-specific one (1).

The top words for both the collection-independent and collection-specific topics
can be printed out with topwords_cclda.py.


4.3. TAM
--------

This package includes an implementation of TAM (the topic-aspect model)
as described in (Paul & Girju, 2010), which is called by supplying "tam" to
the -model parameter.

The command-line parameters for this model are:

NOTE: Here, "beta" is different from the usual topic model notation. "beta"
      is the hyperparameter for the distribution of aspects in a document,
      NOT the distribution over words in a topic. The topic-word distribution
      is instead defined by the hyperparameter "omega". This follows the 
      notation in the TAM paper, but be careful not to confuse these.

-Z <int>                  The number of topics.
-Y <int>                  The number of aspects. (Works best with Y=2.)
[-alpha <double>]         The prior for document-topic distributions. 
                          Default 1.0.
[-beta <double>]          The prior for document-aspect distributions. 
                          Default 1.0.
[-omega <double>]         The prior for topic-word distributions. Default 0.01.
[-delta0 <double>]        The prior for belonging to the background level (l=0).
                          Default 10.0.
[-delta1 <double>]        The prior for belonging to the topical level (l=1).
                          Default 10.0.
[-gamma0 <double>]        The prior for belonging to the aspect-independent
                          route (x=0). Default 1.0.
[-gamma1 <double>]        The prior for belonging to the aspect-dependent
                          route (x=1). Default 1.0.
[-labelPrior <double>]    This is the value that -beta will take if the aspect
                          matches the document label. More details below.

Example usage:

> java LearnTopicModel -model tam -input input_docs.txt -iters 4000 -Z 30 -Y 2 -alpha 0.1 -beta 0.1

The output format is the same as the input format, except each word token
"word" has been replaced with "word:z:y:l:x" where z is the integer topic 
assignment, y is the integer aspect assignment, l denotes if it belongs to the
background level (0) or topical level (1), and x denotes if it depends on the
aspect (1) or not (0).

The top words for both the aspect-independent and aspect-specific topics as
well as the background distribution can be printed out with topwords_tam.py.

A note on the -labelPrior parameter: this can be used if your documents are
labeled (as described in the input format section below). The value for
-beta will be replaced by the value supplied in -labelPrior for the aspect
that matches the document label, while all other aspects will get the
value passed in for -beta. This can be used to bias the document-aspect
distribution to favor the aspect that matches up with the true label
(for example, by using parameters like -beta 1.0 -labelPrior 9.0).
As the -labelPrior value approaches infinity, TAM reduces to ccLDA.

Also note that you may want to set a strong prior for the background 
distribution to get it to behave as expected; that is, it should be more 
likely for words to come from the background distribution, so delta0
might be greater than delta1 (for example, delta0=80.0, delta1=20.0,
but this should be based on your own assumptions about the data).

5. Input Format
---------------

The format of the input file for ccLDA and TAM is:


0 this is a doc
0 this is another doc
...
...
1 this is a doc from another collection
2 this is a doc from a third collection


Each line should contain the words in the document separated by 1 space. You
may need to do some pre-processing (removing non-word characters, word
stemming, stop-word removal, etc).

The number at the start of each line denotes which collection the 
document comes from (starting at 0). The collection is required for ccLDA and 
may optionally be used in TAM with the -labelPrior parameter. If you are not
using this, the line still needs to begin with an integer. In this case you 
should just begin each line with 0.

For LDA, the input format is mostly the same, but it should NOT begin with
an integer. The line should simply begin with the first word in the document.

Note that this Gibbs sampler iterates through the tokens in a sequential and
not random order. If you would like some randomness, it might be a good idea
to shuffle the lines (documents) in your input file so that the documents are
sampled in a random order (if you believe that your default input is not
random enough -- if your text comes from different collections, sampling will 
work better if the collections are not contiguous in your input file).


6. Output Format
----------------

The output format follows the input format, except each word token is 
appended with its variable assignments at the end of the Gibbs sampling
procedure. Details specific to each model are found in the subsections above. 

The output is written to a file of the same name as the input file, except
the filename is appended with ".assign".


7. Viewing the Top Words
------------------------

Python scripts are included to print out the top words for the topics. Each 
model has its own output script (topwords.py, topwords_cclda.py, topwords_tam.py), 
but they are all used in the same way. The script takes a command line
argument of the file containing the variable assignments (i.e. the output file
from the Java program).

Example usage:

> python topwords.py input_docs.txt.assign > output_topwords_lda.txt
> python topwords_cclda.py input_docs.txt.assign > output_topwords_cclda.txt
> python topwords_tam.py input_docs.txt.assign > output_topwords_tam.txt

For ccLDA and TAM, the words that immediately appear under each topic are the
collection-neutral or aspect-neutral words, then each set of collection- or
aspect-specific words is shown.

8. Troubleshooting
------------------

The program does not offer much in the way of error messages. If the program
crashes, here are some possible causes:

- There is a blank line in the input (empty document)
- A line does not begin with an integer (for ccLDA or TAM)
- The collection numbers do not start with 0 or are not sequential (for ccLDA)

Note that the documents themselves do not have to be in any order.

It is also possible that there are bugs in the program. Please email me if you
believe this is the case.


References
----------
Blei, D.; Ng, A.; and Jordan, M. 2003. Latent dirichlet
allocation. Journal of Machine Learning Research 3.

Chemudugunta, C.; Smyth, P.; and Steyvers, M. 2006. Modeling
general and specific aspects of documents with a probabilistic
topic model. In NIPS.
