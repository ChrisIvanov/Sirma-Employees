# Sirma-Employees

This projects serves the purpose of displaying a solution to a real live problem.

The task of the project is to configure the time in which a pair of employees spent together on common projects.

The result should be the pair with the longest period of time spent together, in the format 
(Employee Id, Employee Id, Time). 
The program should also display the total time of the projects that the pair shares, each on a new line in the format
(Project Id, Time)

A preconfigured dataset (a CSV file including Employee ID, Project ID, Start Date and End Date)
is to be fed into the program in order for the employee data to be processed. If the End Date should be NULL,
the date is automatically configured to TODAY.

The main algorithm should be able to recognize different date formats from the CSV sheet, 
and convert them to the international date format (ISO 8601) - big-endian (YYYY-MM-DD).

