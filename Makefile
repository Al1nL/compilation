###############
# DIRECTORIES #
###############
BASEDIR           = $(shell pwd)
JFlex_DIR         = ${BASEDIR}/jflex
CUP_DIR           = ${BASEDIR}/cup
SRC_DIR           = ${BASEDIR}/src
BIN_DIR           = ${BASEDIR}/bin
INPUT_DIR         = ${BASEDIR}/input
OUTPUT_DIR        = ${BASEDIR}/output
EXTERNAL_JARS_DIR = ${BASEDIR}/external_jars
MANIFEST_DIR      = ${BASEDIR}/manifest

#########
# FILES #
#########
JFlex_GENERATED_FILE      = ${SRC_DIR}/Lexer.java
CUP_GENERATED_FILES       = ${SRC_DIR}/Parser.java ${SRC_DIR}/TokenNames.java
JFlex_CUP_GENERATED_FILES = ${JFlex_GENERATED_FILE} ${CUP_GENERATED_FILES}
SRC_FILES                 = ${SRC_DIR}/*.java ${SRC_DIR}/*/*.java
EXTERNAL_JAR_FILES        = ${EXTERNAL_JARS_DIR}/java-cup-11b-runtime.jar
MANIFEST_FILE             = ${MANIFEST_DIR}/MANIFEST.MF
COMPILER_NAME             = COMPILER

########################
# DEFINITIONS :: JFlex #
########################
JFlex_PROGRAM  = jflex
JFlex_FLAGS    = -q
JFlex_DEST_DIR = ${SRC_DIR}
JFlex_FILE     = ${JFlex_DIR}/LEX_FILE.lex

######################
# DEFINITIONS :: CUP #
######################
CUP_PROGRAM                    = java -jar ${EXTERNAL_JARS_DIR}/java-cup-11b.jar
CUP_FILE                       = ${CUP_DIR}/CUP_FILE.cup
CUP_GENERATED_PARSER_NAME      = Parser
CUP_GENERATED_SYMBOLS_FILENAME = TokenNames
CUP_FLAGS =                                \
-nowarn                                    \
-parser  ${CUP_GENERATED_PARSER_NAME}      \
-symbols ${CUP_GENERATED_SYMBOLS_FILENAME}

##################################################
# DEFAULT TARGET: build only                     #
# This is what the self-check script runs.       #
# Produces ex5/COMPILER — no input files needed. #
##################################################
all:
	rm -rf ${COMPILER_NAME}
	rm -rf ${JFlex_CUP_GENERATED_FILES} ${BIN_DIR}/*.class ${BIN_DIR}/*/*.class
	$(JFlex_PROGRAM) ${JFlex_FLAGS} -d ${JFlex_DEST_DIR} ${JFlex_FILE}
	$(CUP_PROGRAM) ${CUP_FLAGS} -destdir ${SRC_DIR} ${CUP_FILE}
	mkdir -p ${BIN_DIR}
	javac -cp ${EXTERNAL_JAR_FILES} -d ${BIN_DIR} ${SRC_FILES}
	jar cfm ${COMPILER_NAME} ${MANIFEST_FILE} -C ${BIN_DIR} .

##################################################
# EVERYTHING: build + compile one file + SPIM    #
# Default input: input/Input.txt                 #
# Override:  make everything INPUT=myfile.txt    #
##################################################
INPUT  ?= ${INPUT_DIR}/TEST_01_Print_Primes.txt
OUTPUT ?= ${OUTPUT_DIR}/out.s

everything: all
	mkdir -p ${OUTPUT_DIR}
	java -jar ${COMPILER_NAME} ${INPUT} ${OUTPUT}
	@if grep -qE "^ERROR|^Register Allocation Failed" ${OUTPUT} 2>/dev/null; then \
		echo "--- Compiler reported:"; \
		cat ${OUTPUT}; \
	else \
		echo "--- Running SPIM ---"; \
		spim -file ${OUTPUT}; \
	fi

##################################################
# TEST-ALL: run every *.txt in input/            #
# Default dirs: input/  expected_output/         #
# Override:                                      #
#   make test-all INPUT_DIR=my/tests             #
#                 EXPECTED_DIR=my/expected       #
##################################################
EXPECTED_DIR ?= ${BASEDIR}/expected_output

test-all: all
	@echo "Running tests on all files in ${INPUT_DIR}..."
	@mkdir -p ${OUTPUT_DIR}
	@pass=0; fail=0; failed_tests=""; \
	for file in $$(ls ${INPUT_DIR}/*.txt 2>/dev/null | sort); do \
		filename=$$(basename $$file .txt); \
		echo "------------------------------------------------"; \
		echo "Testing: $$filename"; \
		java -jar ${COMPILER_NAME} $$file ${OUTPUT_DIR}/$$filename.s 2>/dev/null; \
		expected_file=${EXPECTED_DIR}/$${filename}_Expected_Output.txt; \
		if [ ! -f "$$expected_file" ]; then \
			echo "  [SKIP] No expected output for $$filename"; \
			continue; \
		fi; \
		compiler_out=$$(cat ${OUTPUT_DIR}/$$filename.s 2>/dev/null); \
		if echo "$$compiler_out" | grep -qE "^ERROR|^Register Allocation Failed"; then \
			actual="$$compiler_out"; \
			echo "COMPILER reported: $$actual"; \
		else \
			echo "Running SPIM for $$filename..."; \
			actual=$$(spim -file ${OUTPUT_DIR}/$$filename.s 2>&1); \
		fi; \
		expected=$$(cat "$$expected_file" | tr -d '\r'); \
		actual=$$(echo "$$actual" | tr -d '\r'); \
		if [ "$$actual" = "$$expected" ]; then \
			echo "  [PASS] $$filename"; \
			pass=$$((pass+1)); \
		else \
			echo "  [FAIL] $$filename"; \
			echo "  Expected: $$expected"; \
			echo "  Actual:   $$actual"; \
			fail=$$((fail+1)); \
			failed_tests="$$failed_tests $$filename"; \
		fi; \
	done; \
	echo "================================================"; \
	total=$$((pass+fail)); \
	echo "Results: passed $$pass/$$total tests!"; \
	if [ -n "$$failed_tests" ]; then \
		echo "Failed tests:$$failed_tests"; \
	fi; \
	echo "================================================"

##########
# CLEAN  #
##########
clean:
	rm -rf ${COMPILER_NAME}
	rm -rf ${JFlex_CUP_GENERATED_FILES}
	rm -rf ${BIN_DIR}/*.class ${BIN_DIR}/*/*.class
	rm -rf ${OUTPUT_DIR}/*.s ${OUTPUT_DIR}/*.txt

.PHONY: all everything test-all clean