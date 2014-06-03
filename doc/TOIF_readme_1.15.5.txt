Readme file for

    * Tool Output Integration Framework version 1.15.5

Contents
========

Tool Output Integration Framework version 1.15.5

   1. What is Tool Output Integration Framework (TOIF)?
   2. TOIF 1.15.5 New Features
   3. Supported Generator Versions
   4. Known Issues
   5. Getting help

KDM Analytics customer support


----------------------------------------------------------------------------
Tool Output Integration Framework version 1.15.5
======================================

1. What is TOIF?

The Tool Output Integration Framework (TOIF) is a powerful open source vulnerability detection platform that provides analysts information of system defects with the ability to:
* Integrate multiple vulnerability detection tools as “data feeds” into the repository
* Collate findings from several tools
* Put vulnerability findings into the context of other facts about the system (such as metrics, architecture, design patterns, etc.)

TOIF takes the output of defect generator tools and displays the results in Eclipse.

TOIF includes the following components:

* TOIF Adaptor: TOIF Adaptor is used to collect the output from various vulnerability detection tools and convert their output into TOIF xml

* TOIF Assimilator: After running the TOIF Adaptor you need to run the Assimilator to merge TOIF findings and/or KDM data into a common fact-orientated repository or filE.

* TOIF Report View: Once you have your TOIF findings assimilated you view the TOIF Report View to display the results in Eclipse.

2. TOIF 1.15.5 New Features:
* No new features  - Bug fixes

3. Supported Generator Versions

TOIF 1.15.5 supports the following generator versions for windows and linux:
* cppcheck-1.40
* findbugs-1.3.9
* jlint-3.0
* rats-2.3
* splint-3.1.2

4. Known Issues

* Traceback displays numbers as reported by TOIF Adaptors: The traceback currently shows numbers as reported by the TOIF Adaptors unlike the code locations in the view list (which are normalized to kdm locations). 

5. Getting help
  
For TOIF source resources, please visit GitHub https://github.com/KdmAnalytics/toif/.

For information about installing and using TOIF, consult the TOIF User's Guide. 
  
-----------------------------------------------------------------------
KDM Analytics customer support
====================================
  
  If, after consulting the published information, you still have questions about using this KDM Analytics product, please contact KDM Analytics Customer Support using one of these methods:
  
   E-mail: support@kdmanalytics.com
  
  Copyright © 2006-2014 KDM Analytics Inc. All rights reserved.
  
 Information subject to change without notice.
  