# Blade TOIF

The Blade Tool Output Integration Framework (TOIF) is a powerful composite
vulnerability detection platform that automatically combines results
from separate code defect scanner tools into one common reporting format
and defect management platform.  Blade TOIF normalizes and analyzes the results
of each tool to add weighting to defect reports, remove duplicates and assign
consistent Common Weakness Enumeration (CWE) codes.   The management platform
 provides the list of defects, along with sorting by defect type and tool,
 weighting and action status (defect citing).


# Build Instructions

```
mvn clean verify
```

# Note

Building Linux Blade TOIF OSS package on the Windows platform results in
the permissions executables to be incorrectly set.  The workaround is to build
Blade TOIF OSS on the Linux Platform or change permissions of executables within the Linux package using the chmod command.
