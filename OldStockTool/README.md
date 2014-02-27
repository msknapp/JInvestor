Old Stock Tool
==============

This is an old java project I wrote back in 2010, whose goal was to predict stock prices.  I don't use it or develop with it today for several reasons.  First, I was much less experienced at programming back in 2010, and the code quality is pretty bad.  The whole project is pretty tough to work with now.  Second, I tried investing based on its recommendations for a year or two, and discovered that I was doing no better than somebody who had invested in a diversified portfolio of very risky stock.

I keep the project here as a reference, in case anybody would like to know how to make stock predictions.

How it Works
============

Essentially the tool collected stock price history data from yahoo, and financial indicator data from the Federal Reserve Board, the Saint Louis Federal Reserve Board website, and the Bureau of Labor and Statistics website.  It would form them into arrays, offset by about one month, so that the stock prices were paired up with the financial indicators that happened about one month prior.  The theory was that these financial indicators were in ways forecasting stock prices in different ways for different companies.  

The code would try hundreds of combinations of financial indicators, up to 16 at a single time though, and a multi variable statistical regression was performed on the data.  I would keep the combination with the highest correlation coefficient.

Later, I would plug in the current values of the financial indicators and produce a list of stock predictions based on the statistical regressions.  For the vast majority of the companies, it produced prices that were slightly higher or lower than their current prices.  For some though, it produce wildly high or low price estimates.  This is to be expected, the stock market can be influenced by factors that never happened in the past.  A company can announce a new patent that will double their net worth, or a lawsuit could happen that threatens to make them bankrupt.

In any case, you can review it if you want to.  Now that I'm a much better developer, I might retry this one day, hopefully soon.
