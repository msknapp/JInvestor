JInvestor
=========

JInvestor, short for java investor, is a collection of (primarily) java projects whose purpose is to analyze investment opportunities in the stock market and the housing market.
These tools are still under development at the time of writing, currently they don't do much.  In time,  
the tools take care of downloading/acquiring data, persisting it, performing analysis of the data, and making results available to the investor.

JInvestor aims to use the latest and smartest advancements in cloud technology.  It uses hadoop and mahout for data analysis.  

What Do You Mean By Analysis?
-----------------------------

The goal here is to find investment opportunities, to find houses or stock shares that are being sold way too high or low.  There are multiple ways to do this,
but here is a list of what JInvestor aims to do:

1. Use mahout to cluster houses and companies into groups
2. Use a mahout classification algorithm to estimate stock prices and house prices
3. Create numerical estimates on the worth of an asset using equations that the experts recommend.

Certainly this sounds very ambitious.

Ulterior Motive
---------------

The second reason I am writing this is to gain experience using hadoop/cloudera tools.  I am training for Cloudera's data science certification.
There will be times when you think that using hadoop, HBase, Mahout, etc. is overkill, and you will probably be right.  I am using them anyways 
because that is what I need experience with.  Hopefully this will not prevent you from developing your own work in here, JInvestor should 
always be as loosely coupled as possible.

Technology
==========

Like I said before, we want to use the latest and greatest FOSS cloud technology to make this all work.  If you are thinking about using JInvestor,
but are worried about all the dependencies and integration it involves, don't be discouraged.  I absolutely believe in keeping code loosely coupled
at all times, JInvestor should be easy to adapt to not use hadoop, mahout, HBase, etc.

That being said, here is what I plan to use:

* cloudera-hadoop 4.5.0 (or whatever EMR supports)
* mahout
* HBase
* Amazon Web Services: S3, EC2, and EMR 
* maven
* Spring

UI Work
=======

I am not a UI developer, I do all backend work.  My plans at the moment are to either produce result files in HDFS/S3, and/or to provide a JAXRS restful 
web service.  If you are interested in developing a web front-end for this tool, I would be very happy to talk with you.

Potential Developers/Users
==========================

Please contact me if you want to for anything related to JInvestor: michaelscottknapp ~at~ gmail.com.
If you think I am reinventing the wheel here, and you know something that already does what I'm doing, please tell me.  If you have ANY interest 
in developing a stock tool of your own, please contact me and we can discuss integrating our work.  
You might think that JInvestor is too different from what you want to develop, but I always try to keep code loosely coupled,
so it should be possible to integrate it with your own work somehow.  If you are interested in 
helping me develop the application, please contact me.

Please keep in mind that it is currently under development so it doesn't do much useful right now.  However, if you have a question about how
something works please ask. 