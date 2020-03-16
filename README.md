# Amazon Search Volume Estimation

## What assumption do you make ?

- We checked the given URL for completion api unfortunately it was not working, but found out the endpoint
  to be `https://completion.amazon.com/api/2017/suggestions?prefix=samsung&mid=ATVPDKIKX0DER&alias=aps`.
  We assume the parameter `mid` stands for marketplace id has no effect on the search result.
  
- User can input multiword keyword either as space separated or concatenated by `+` symbol.
- Any other symbol will be consider just as part of keyword.  


## How does your algorithm works 

- For a given prefix fetch amazon completion suggestion for each letter of the keyword.

- Score keyword by position of it in a given search suggestion by formula `(numberOfSuggestions-index)/numberOfSuggestion*100`

- Take the average of score .

The above algorithm takes into consider the relative position of a given keyword in the given list of suggestion. From
our observation when our input in very small for eg we have input only 'i' for 'iphone' amazon does not yet know the
context so keyword those are visible at that point are highly searched. If we keep on adding more letters to our 
keyword the search completion becomes narrow down and tends to exact match of the given keyword. The formula above 
incentivise those keyword that comes on top in initial few input of letter and may letter drop out due to relevancy.

## Do you think the *hint that we gave you earlier is correct and if so - why ?

From our observation we see there is a weightage among the element in a suggestion. So their relative ordering does matter.
A suggestion increasingly push up suggestion list as more and more letter of keyword start matches to that value. 

## How precise do you think your outcome?

We do not have actual score of keyword available so measuring accuracy is hard. However the algorithm returns stable output
everytime a same keyword is input. Also search suggestion can be result of many other think like country, marketplaceid etc.
. So try to guess volume just from the suggestion may not be full indication.