__author__ = 'Oscar'

import os
from subprocess import check_output

if __name__ == "__main__":
    ct = check_output("git status -s")
    mod = [each for each in ct.split("\n") if " M " in each and (".java" in each or "pom.xml" in each)]
    packages = set([each[3:].split("/")[0] for each in mod])
    if len(packages) > 0:
        for each in packages:
            command = "mvn -amd -pl %s compile package -fae -DskipTests=true" % each
            os.system(command)
        os.system('git commit -a -m "Temporary commit"')