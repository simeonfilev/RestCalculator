# REST API CALCULATOR

## Prerequirements:
 - Apache Maven 3.6.3
 - JDK 11.0.6
 - If you want to use MySqlDatabase as storage: MySQL 8.0
 - Tomcat 9.0.34
 - IntelliJ IDEA Ultimate 2019.3.4

## Getting started:
1. Open Terminal in the folder that you want to copy the project
2. Type: git clone https://github.com/simeonfilev/RestCalculator.git 
3. Import project in IntelliJ : File->Open->RestCalculator
4. Create new Run configuration
![Build configuration](https://i.imgur.com/AsauyQE.png)
![Build configuration](https://i.imgur.com/8VAF21F.png)
5. Run the project from Run-> Run 'Tomcat 9.0.34' Or (Shift+F10)

## Requests

**GET:(https://{host}/calculator/expressions)** - returns all processed expressions.
Example response: `"expressions":  [{"expression": "113*5","answer":  565,"id":  0},
{"expression": "1133*5","answer":  5665,"id":  1},
{"expression": "113*5","answer":  565,"id":  2},
{"expression": "11*5","answer":  55,"id":  3},
{"expression": "11*5","answer":  55,"id":  4}]`


**POST:(https://{host}/calculator/expressions?expression={expression})** - where expression is the text you want to calculate
Example Response: `{"expression": "113*5","answer":  565}`



## Storage available:

 - MySQL Database (Accepts as system variables: 'DB_USERNAME', 'DB_PASSWORD',' SERVER_NAME' and 'DB_NAME')
 - File based (File name can be changed from class 'FileStorageImpl')

**Change type of storage:** Go to RestCalculator class and change the field private StorageInterface storage to whatever storage you want to use! ('FileStorageImpl' or 'MySQLStorageImpl')
