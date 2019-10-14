
# SimRank

This repository contains the source code implementation of SimRank and a dataset for testing the algorithm.


## Introduction


SimRank is an algorthim used to compute the similarity among nodes in a graph. The original SimRank paper is in the following ([link](http://ilpubs.stanford.edu:8090/508/1/2001-41.pdf)).

We exploited sparse matrix to optimize the memory usage and thus increasing the computational performance.


## Repository Structure

This repository is organized as follows:

* The [src](./src) directory contains the implementation of the SimRank tool
	
* The [Data](./Data) directory contains the dataset exploited to test the algorithm:
	

## How to cite
If you find our work useful for your research, please cite it using the following BibTex entries:

```
@INPROCEEDINGS{8498236, 
author={P. T. {Nguyen} and J. {Di Rocco} and R. {Rubei} and D. {Di Ruscio}}, 
booktitle={2018 44th Euromicro Conference on Software Engineering and Advanced Applications (SEAA)}, 
title={CrossSim: Exploiting Mutual Relationships to Detect Similar OSS Projects}, 
year={2018}, 
volume={}, 
number={}, 
pages={388-395}, 
keywords={data mining;project management;public domain software;recommender systems;software development management;software engineering;software reusability;CrossSim;open source software projects;related artifacts;similar GitHub repositories;exploiting mutual relationships;detect similar OSS projects;software development;knowledge-intensive activity;technology trends;external libraries;recommender systems;software engineering;real-time recommendations;reusable artifacts;open source software repositories;Libraries;Open source software;Ecosystems;Semantics;Computational modeling;Software systems;Mining software repositories, software similarities, SimRank}, 
doi={10.1109/SEAA.2018.00069}, 
ISSN={}, 
month={Aug},} 

```

or this one

```
@inproceedings{Nguyen:2015:ESP:2740908.2742141,
 author = {Nguyen, Phuong and Tomeo, Paolo and Di Noia, Tommaso and Di Sciascio, Eugenio},
 title = {An Evaluation of SimRank and Personalized PageRank to Build a Recommender System for the Web of Data},
 booktitle = {Proceedings of the 24th International Conference on World Wide Web},
 series = {WWW '15 Companion},
 year = {2015},
 isbn = {978-1-4503-3473-0},
 location = {Florence, Italy},
 pages = {1477--1482},
 numpages = {6},
 url = {http://doi.acm.org/10.1145/2740908.2742141},
 doi = {10.1145/2740908.2742141},
 acmid = {2742141},
 publisher = {ACM},
 address = {New York, NY, USA},
 keywords = {personalized pagerank, recommender systems, simrank, web of data},
} 
```
