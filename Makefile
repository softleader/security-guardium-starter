# OPTIONS

## VERSION defines the release version FOB the bundle.
VERSION ?=
## JAVA defines the Java version to compile, test or install the source code.
JAVA ?=
## BOOT defines the Spring Boot version to use
BOOT ?=

# Functions
define java_version
$(if $(filter-out "",$(JAVA)),-D'java.version=$(JAVA)',)
endef

##@ General

help: ## Display this help.
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n"} /^[a-zA-Z_0-9-]+:.*?##/ { printf "  \033[36m%-25s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

##@ Develop

format: ## Format the source code.
	mvn validate

clean: ## Remove files generated at build-time.
	mvn clean -e

compile: clean  ## Clean and compile the source code.
	mvn compile -e $(call java_version)

test: clean ## Clean and test the compiled code.
	mvn test -e $(call java_version)

install: clean ## Install project to local repository w/o unit testing.
	mvn install -e -DskipTests -Prelease $(call java_version)

spring-boot-version: ## Get current Spring Boot version
	@mvn help:evaluate -Dexpression=spring-boot.version -DforceStdout -q

bump-spring: bump-spring-boot ## Bump Spring versions

bump-spring-boot: ## Bump Spring Boot version
ifeq ($(strip $(BOOT)),)
	$(error BOOT is required)
endif
	mvn versions:set-property -Dproperty=spring-boot.version -DnewVersion=$(BOOT)
	mvn versions:commit

update-dependencies: ## Update all the dependencies to the latest version
	mvn versions:update-properties
	mvn versions:commit

##@ Delivery

version: ## Get current project version
	@mvn help:evaluate -Dexpression=project.version -DforceStdout -q

new-version: ## Update version.
ifeq ($(strip $(VERSION)),)
	$(error VERSION is required)
endif
	mvn versions:set -DnewVersion=$(VERSION)
	mvn versions:commit

release: ## Pack w/o unit testing, and release to remote repository.
	mvn deploy -e -DskipTests -Prelease $(call java_version)
