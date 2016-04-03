# katapult
Empower engineering teams using Red Hat technologies with an intuitive experience to quickly and confidently promote their code from development to production.

Prerequisites
-------------
1. Java
2. Apache Maven
3. A GitHub Account

* Log into GitHub and generate an access token for use here:
--  https://help.github.com/articles/creating-an-access-token-for-command-line-use/
* Create 2 environment variables:
-- `GITHUB_USERNAME`, `GITHUB_TOKEN`

For instance you may put into your `~/.bash_profile`:

    export GITHUB_USERNAME=ALRubinger
    export GITHUB_TOKEN=[token created from above]
    
Updates to `~./bash_profile` may require logging out of the shell to be visible; you may check 
by typing into a terminal:

    $ echo $GITHUB_USERNAME
    
4. A locally-running instance of OpenShift 

ALR has been running his tests against a local instance of Origin; instructions for getting this stood up are here:
-- https://github.com/openshift/origin/blob/master/CONTRIBUTING.adoc

When running, this should give you a local API to execute against at https://localhost:8443, which is where the OpenShiftService tests currently look to make their calls.

Build and Run the Tests
-----------------------

1. Execute:

        $ mvn clean install

Optionally, if you don't have dependencies 3) and 4) above, you may skip local test execution:

        $ mvn clean install -DskipTests=true
        