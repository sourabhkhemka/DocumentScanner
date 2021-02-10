***for full documentation see [wiki](https://github.com/sourabhkhemka/DocumentScanner/wiki)***
# DocumentScanner: RScan
DocumentScanner or RScan (as I like to call it) is an OpenCV based project to scan documents in various modes. This repository contains source code to scan documents just like state-of-the-art mobile apps like Microsoft Lens or Adobe Scan.

## Repository's directory structure
The same algorithm to scan documents is available in both Python & Java.
* **Python** code to scan documents can be located at: [DocumentScanner/RScan/Python/scan](https://github.com/sourabhkhemka/DocumentScanner/tree/main/RScan/Python/scan)

* **Java** code to scan documents can be located at: 
    * [DocumentScanner/RScan/Java/src/scan](https://github.com/sourabhkhemka/DocumentScanner/tree/main/RScan/Java/src/scan) has the code for Scan package that contains all the   methods to scan documents

    * Main.java in [DocumentScanner/RScan/Java/main](https://github.com/sourabhkhemka/DocumentScanner/tree/main/RScan/Java/main) imports the Scan package and calls public methods of the Scan class to scan images.
    
* Both Python & Java directories have **empty folders** named, "crop". I'll be uploading code to automatically crop documents in the near future.

## Built with
* Python 3.5.4 & OpenCV 3.4.3
* Java JDK 11.0.8 & OpenCV 3.4.4
    
## Complete Documentation:

[Wiki](https://github.com/sourabhkhemka/DocumentScanner/wiki) contains complete documentation of the project. 

Java code is supposed to make RScan ready for Andriod Development.

## Next target to achieve:
RScan requires two or three parameters to be provided manually in order to scan the documents. These parameter selections need to be automated to give a completeness to the project. Once done, an API to scan documents can be coded.

## Example outputs:
![example](https://github.com/sourabhkhemka/DocumentScanner/blob/main/wiki_images/rsca_label.jpeg)

## About author:
* [LinkedIn profile](https://www.linkedin.com/in/sourabh-khemka-b6894513a/)
* email id: sourabhkhemkask@gmail.com
