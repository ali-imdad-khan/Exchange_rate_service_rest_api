# Exchange_rate_service_rest_api
Java / Spring RestAPI exchange rate run time request service

This service is an implementation of the following tasks as 
defined below referencing the user stories described in the Scalable Test Assessment:

As a user, who accesses this service through a user interface, ...
1. I want to retrieve the ECB reference rate for a currency pair, e.g. USD/EUR or
HUF/EUR.
2. I want to retrieve an exchange rate for other pairs, e.g. HUF/USD.
3. I want to retrieve a list of supported currencies and see how many times they were
requested.
4. I want to convert an amount in a given currency to another, e.g. 15 EUR = ??? GBP
5. I want to retrieve a link to a public website showing an interactive chart for a given
currency pair.
The user interface is not part of this assignment.

###############################################

This project is build over the following requirements:

-Java 
-XML
-Spring
-Maven
-HTTP endpoints
-URLs

##########################################
Endpoints according to user requirement tasks:

1. I want to retrieve the ECB reference rate for a currency pair, e.g. USD/EUR or
HUF/EUR.

For euro currency pairs: USD/EUR, HUF/EUR etc.

http://localhost:8080/euro-rate/{currency}

Response:
{"requestName":"gbp_euro_ref_rate","requestValue":"1.1738466956215519"}

For currency against the euro pairs: EUR/USD, EUR/HUF etc.

http://localhost:8080/currency-rate/{currency}
Response:
{"requestName":"euro_gbp_ref_rate","requestValue":"0.85190"}

------------------------------- 
2. I want to retrieve an exchange rate for other pairs, e.g. HUF/USD.

For any currency pair: USD/GBP, GBP/ZAR etc.
http://localhost:8080/pair-rate/{currency_from}-{currency_to}

Response:
{"requestName":"zar_chf_rate","requestValue":"0.062431418890402865"}

------------------------------- 
3. I want to retrieve a list of supported currencies and see how many times they were
requested.

A list of all currencies show on ECB website along with the number of requests made for each currency
http://localhost:8080/list

Response:
[{"requestName":"Total currencies","requestValue":"31"},{"requestName":"CHF","requestValue":"1"},
{"requestName":"HRK","requestValue":"0"},{"requestName":"MXN","requestValue":"0"},{"requestName":"ZAR","requestValue":"2"},{"requestName":"INR","requestValue":"0"},
{"requestName":"CNY","requestValue":"0"},{"requestName":"THB","requestValue":"0"},{"requestName":"AUD","requestValue":"0"},
{"requestName":"ILS","requestValue":"0"},{"requestName":"KRW","requestValue":"0"},{"requestName":"JPY","requestValue":"0"},
{"requestName":"PLN","requestValue":"0"},{"requestName":"GBP","requestValue":"4"},{"requestName":"IDR","requestValue":"0"},
{"requestName":"HUF","requestValue":"0"},{"requestName":"PHP","requestValue":"0"},{"requestName":"TRY","requestValue":"0"},
{"requestName":"ISK","requestValue":"0"},{"requestName":"HKD","requestValue":"0"},{"requestName":"EUR","requestValue":"0"},
{"requestName":"DKK","requestValue":"0"},{"requestName":"USD","requestValue":"2"},{"requestName":"CAD","requestValue":"0"},
{"requestName":"MYR","requestValue":"0"},{"requestName":"BGN","requestValue":"0"},{"requestName":"NOK","requestValue":"0"},
{"requestName":"RON","requestValue":"0"},{"requestName":"SGD","requestValue":"0"},{"requestName":"CZK","requestValue":"0"},
{"requestName":"SEK","requestValue":"0"},{"requestName":"NZD","requestValue":"0"},{"requestName":"BRL","requestValue":"0"}]

------------------------------- 
4. I want to convert an amount in a given currency to another, e.g. 15 EUR = ??? GBP

To convert any currency amount to another currency
http://localhost:8080/currency-converter/{currency_amount}-{currency_from}-{currency_to}

Response:
{"requestName":"15_eur_gbp_amount","requestValue":"12.7785"}

------------------------------- 
5. I want to retrieve a link to a public website showing an interactive chart for a given
currency pair.

This endpoint returns the link for a public website for an interactive chart
http://localhost:8080/chart/{currency_from}-{currency_to}

Response:
{"requestName":"public_web_link","requestValue":"https://www.xe.com/currencycharts/?from=EUR&to=GBP"}

This endpoint automatically redirects to a public website for an interactive chart
http://localhost:8080/chart-open/{currency_from}-{currency_to}

Response:
Automatic redirect to the public website


######################################

Run the app

1. Move into the target folder:
exchangerateservice\exchangerateservice\target

2. Run command:
$ java -jar exchangerateservice-0.0.1-SNAPSHOT.jar
$

######################################

Docker Instructions

1. build
docker build -f Dockerfile -t ecbexchangeservice

2. run
docker run -p 8080:8080 ecbexchangeservice
