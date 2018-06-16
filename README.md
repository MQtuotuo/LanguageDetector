# Language Detector
Language Detector written in Java

The application is based on calculating and comparing language profiles of N-gram frequencies. 

Reference: William B. Cavnar and John M. Trenkle. **N-Gram-Based Text Categorization**. In Proceedings of SDAIR-94, 3rd Annual Symposium on Document Analysis and Information Retrieval, 1994.

The Universal Declaration of Human Rights Database is used since i) it has been translated into over 460 different languages and dialects and ii) it’s free. In this application, only a subset of them are used to create language profiles:
czc.txt - Czech (Cesky)
dns.txt - Danish (Dansk)
dut.txt - Dutch (Nederlands)
eng.txt - English
frn.txt - French (Français)
ger.txt - German (Deutsch)
grk.txt - Ellinika’ (Greek)
hng.txt - Hungarian
itn.txt - Italian
jpn.txt - Japanese (Nihongo)
lat.txt - Latvian
lit.txt - Lithuanian (Lietuviskai)
ltn.txt - Latin (Latina)
lux.txt - Luxembourgish (Lëtzebuergeusch)
mls.txt - Maltese
por.txt - Portuguese
rum.txt - Romanian (Româna)
rus.txt - Russian (Russky)
spn.txt - Español (Spanish)
ukr.txt - Ukrainian (Ukrayins’ka)
yps.txt - Yapese

## Start

Starting the program

     >java -jar LanguageDetector.jar
     Do you wish to train your model first (y/n)?
     y
     Language profiles are saved
     Enter some large text (or exit): 
     what is the weather today
     Detected Language : eng

     Enter some text (or exit): 
     exit

The program first reads all texts in the trainingData folder. Then, it generates the language frequency profiles for each language in langModels folder and saves them. Next, text can be entered to identify the language.


## Program Structure
* **langdetector** source code
* **test** source code of unit tests
* **trainingData** training data
* **langModels** language frequency profiles
* **doc** generated java doc