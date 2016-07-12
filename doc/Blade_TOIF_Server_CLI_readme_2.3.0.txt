Readme file for

    * Blade Tool Output Integration Framework Server Command Line Interface (CLI) version 2.3.0

Contents
========

Blade TOIF Server (CLI) 2.3.0

   1. What is Blade TOIF Server (CLI)?
   2. Blade TOIF Server (CLI) 2.3.0 New Features
   3. Blade TOIF Server (CLI) 2.3.0 Improvements
   4. Supported Open Source Software (OSS) Static Code Analysis (SCA) Tool Versions
   5. Known Issues
   6. Getting help

KDM Analytics customer support

----------------------------------------------------------------------------
Blade TOIF Server (CLI) version 2.3.0
======================================

1. What is Blade TOIF Server (CLI)?

The Blade TOIF Server (CLI) is a powerful vulnerability detection platform for Integrated Development Environments (Eclipse). 
It allows users to perform vulnerability sightings on a project utilizing multiple open source software (OSS) static code analysis (SCA) tools, and analyze the results in a common format using a single viewer to:
* Integrate multiple OSS SCA tools as “data feeds” into the repository; addressing wider breath and depth of vulnerability coverage and common processing of results 
* Normalization and correlation of “data feeds” based on discernable patterns described as Software Fault Patterns (SFPs) and CWEs
* Collated SFP/CWE findings
* Prioritized report output with weighted results across tools/vendors and exporting in XML and TSV format for further analysis in spreadsheet
* Utilization of open source development to advance the Software Assurance space
* A standard-based common protocol for exchanging vulnerability findings

Blade TOIF Server (CLI) takes the output of OSS SCA tools and displays the results in the TOIF Findings View that has been installed in Eclipse 4.4.2 Luna (recommended) or Eclipse 4.3.2 Kepler.

Blade TOIF Server (CLI) includes the following components:

* TOIF Adaptor: TOIF Adaptor is used to collect the output from various OSS SCA tools and convert their output into TOIF xml
* TOIF Assimilator: After running the TOIF Adaptor you need to run the Assimilator to merge TOIF findings and/or KDM data into a common fact-orientated repository or file.
* TOIF Findings View: Once you have your TOIF findings assimilated you view the TOIF Findings View to display the results in Eclipse.
* TSV Output: Assimilated output data (vulnerability findings) is converted to a tab-separated-values format so that it can be viewed by text editors or spreadsheets

2. Blade TOIF Server (CLI) 2.3.0 New Features:
* TOIF Findings Configuration Preferences table allows customized priority setting of the vulnerability findings reported in the TOIF Findings view
* Grouping of vulnerability type findings by two or more vulnerability detection tools in the TOIF Findings view
* TSV Output utility to convert assimilated output data to a tab-separated-values format so that it can be viewed by text editors or spreadsheets
* Support for Windows 10 and SUSE Linux Enterprise Linux (SLED) 12

3. Blade TOIF Server (CLI) Improvements:
* Preserve column order of re-ordered columns within the TOIF Findings view for a specific workspace
* Enhanced TOIF Findings view sort order

4. Supported OSS SCA Tool Versions

Blade TOIF Server (CLI) 2.3.0 supports the following OSS SCA tool versions for windows and linux:
* cppcheck-1.60.1
* findbugs-3.0.0
* jlint-3.0
* rats-2.3
* splint-3.1.2

5. Known Issues
 
* Traceback displays numbers as reported by TOIF Adaptors: The traceback currently shows numbers as reported by the TOIF Adaptors unlike the code locations in the view list (which are normalized to kdm locations). 
* Use of the Jlint 3.0 OSS SCA tool with TOIF 2.3.0 is not supported on the Fedora-17.x 64-bit, SUSE Linux Enterprise Desktop 12 64-bit or RHEL 7.1 64-bit operating system.

6. Getting help
  
For Blade TOIF Server (CLI) source resources, please visit GitHub https://github.com/KdmAnalytics/toif/.

For information about installing and using Blade TOIF Server (CLI), consult the Blade TOIF Server (CLI) 2.3.0 User's Guide. 
  
-----------------------------------------------------------------------
KDM Analytics customer support
====================================
  
If, after consulting the published information, you still have questions about using this KDM Analytics product, please contact KDM Analytics Customer Support using one of these methods:
  
* E-mail: support@kdmanalytics.com
  

Copyright © 2006-2016 KDM Analytics, Inc. All rights reserved. Information subject to change without notice.
  