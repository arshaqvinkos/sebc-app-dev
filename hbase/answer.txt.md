```sh

RowKey : Module(n)_Domain_Email_Hash(IP)_Timestamp_Random Number  

Column Family : Domain 
	 Column: Domain

Column Family : User 

     Column: Email 
     Column: IP 

TimeStamp



Considerations For Put:

1.RowKey is a concatenation of Module(n),Domain,Email,Timestamp in (YYYYMMDDHHMMSS),IP and a random number.

2.We will use the Module(n) where n is the number of regions to add a sequence between 000 to 999 so that during writes data is distributed among all the regions.

3.Timestamp and random number are added to avoid duplicate Rowkeys


Considerations For Get:

1. With Domain Name as part of the rowkey, we can filter for a particular Domain and then use Column Family:User and Timestamp to get number of visitors per domain for a time period

2. Similarly, with User Email as part of the rowkey, we can filter for a particular User and then use Column Family:Domain and Timestamp to get distinct domains a particular user visited for a time period

```