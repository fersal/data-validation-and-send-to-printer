# data-validation-and-send-to-printer

Hi <br />
Here's a Spring Boot app that loads your CSVs and parses data while validating dates and employeeId.
I prefer using a Functional programming approach to keep cyclomatic complexity low so you won't see too many if statements nor for loops. Instead I use streams also reducing memory footprint. For example, while validating usage dates I could have had like 4 or 6 different if statements looking for all the different month/day combinations but instead I enforce the one format and let user know if there's a problem while still finishing the report.
I will say I probably do need a for loop to print usage details in columns but I ran out of time. <br />
Thanks.
