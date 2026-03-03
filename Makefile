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

#########################
# DEFINITIONS :: PARSER #
#########################
INPUT    = ${INPUT_DIR}/TEST_05_Classes.txt
OUTPUT   = ${OUTPUT_DIR}/output.txt

##########
# TARGET #
##########
compile:
	clear
	@echo "*******************************"
	@echo "*                             *"
	@echo "* [0] Remove COMPILER program *"
	@echo "*                             *"
	@echo "*******************************"
	rm -rf COMPILER
	@echo "\n"
	@echo "************************************************************"
	@echo "*                                                          *"
	@echo "* [1] Remove *.class files and JFlex-CUP generated files:  *"
	@echo "*     Lexer.java                                           *"
	@echo "*     Parser.java                                          *"
	@echo "*     TokenNames.java                                      *"
	@echo "*                                                          *"
	@echo "************************************************************"
	rm -rf ${JFlex_CUP_GENERATED_FILES} ${BIN_DIR}/*.class ${BIN_DIR}/*/*.class
	@echo "\n"
	@echo "************************************************************"
	@echo "*                                                          *"
	@echo "* [2] Use JFlex to synthesize Lexer.java from LEX_FILE.lex *"
	@echo "*                                                          *"
	@echo "************************************************************"
	$(JFlex_PROGRAM) ${JFlex_FLAGS} -d ${JFlex_DEST_DIR} ${JFlex_FILE}
	@echo "\n"
	@echo "*******************************************************************************"
	@echo "*                                                                             *"
	@echo "* [3] Use CUP to synthesize Parser.java and TokenNames.java from CUP_FILE.cup *"
	@echo "*                                                                             *"
	@echo "*******************************************************************************"
	$(CUP_PROGRAM) ${CUP_FLAGS} -destdir ${SRC_DIR} ${CUP_FILE}
	@echo "\n"
	@echo "********************************************************"
	@echo "*                                                      *"
	@echo "* [4] Create *.class files from *.java files + CUP JAR *"
	@echo "*                                                      *"
	@echo "********************************************************"
	mkdir -p ${BIN_DIR}
	javac -cp ${EXTERNAL_JAR_FILES} -d ${BIN_DIR} ${SRC_FILES}
	@echo "\n"
	@echo "***********************************************************"
	@echo "*                                                         *"
	@echo "* [5] Create a JAR file from from *.class files + CUP JAR *"
	@echo "*                                                         *"
	@echo "***********************************************************"
	jar cfm COMPILER ${MANIFEST_FILE} -C ${BIN_DIR} .
	@echo "\n"
	@echo "*****************************"
	@echo "*                           *"
	@echo "* [6] Run resulting program *"
	@echo "*                           *"
	@echo "*****************************"
	java -jar COMPILER ${INPUT} ${OUTPUT}

	@echo "****************remove before submission:****************"
	@# Check if the output file contains the specific failure strings
	@if grep -qE "ERROR|Register Allocation Failed" ${OUTPUT}; then \
		echo "\n------------------------------------------------\n"; \
		echo "COMPILER reported an error. Skipping SPIM phase."; \
		echo "Content of ${OUTPUT}:"; \
		cat ${OUTPUT}; \
		echo "\n------------------------------------------------\n"; \
	else \
		echo "[7] Running MIPS program using SPIM"; \
		spim -file ${OUTPUT} > ${OUTPUT_DIR}/MIPS_OUTPUT.txt; \
		cat ${OUTPUT_DIR}/MIPS_OUTPUT.txt; \
	fi

############
# TEST ALL #
############
test-all:
	@echo "Running tests on all files in ${INPUT_DIR}..."
	@mkdir -p ${OUTPUT_DIR}
	@pass=0; fail=0; skip=0; passed_nums=""; \
	for file in ${INPUT_DIR}/TEST_*.txt; do \
		filename=$$(basename $$file .txt); \
		echo "------------------------------------------------"; \
		echo "Testing: $$filename"; \
		java -jar ${COMPILER_NAME} $$file ${OUTPUT_DIR}/$$filename.s 2>/dev/null; \
		if grep -qE "^ERROR|^Register Allocation Failed" ${OUTPUT_DIR}/$$filename.s 2>/dev/null; then \
			echo "COMPILER reported an error for $$filename. Skipping SPIM."; \
			actual=$$(cat ${OUTPUT_DIR}/$$filename.s); \
		else \
			echo "Running SPIM for $$filename..."; \
			spim -file ${OUTPUT_DIR}/$$filename.s > ${OUTPUT_DIR}/MIPS_OUTPUT_$$filename.txt 2>&1; \
			actual=$$(cat ${OUTPUT_DIR}/MIPS_OUTPUT_$$filename.txt); \
		fi; \
		expected_file=${BASEDIR}/expected_output/$${filename}_Expected_Output.txt; \
		if [ ! -f "$$expected_file" ]; then \
			echo "  [SKIP] No expected output file for $$filename"; \
			skip=$$((skip+1)); \
		else \
			expected=$$(cat "$$expected_file" | tr -d '\r'); \
			actual=$$(echo "$$actual" | tr -d '\r'); \
			if [ "$$actual" = "$$expected" ]; then \
				echo "  [PASS] $$filename"; \
				pass=$$((pass+1)); \
				num=$$(echo "$$filename" | grep -oP '(?<=TEST_)\d+' | sed 's/^0*//'); \
				if [ -z "$$passed_nums" ]; then passed_nums="$$num"; else passed_nums="$$passed_nums, $$num"; fi; \
			else \
				echo "  [FAIL] $$filename"; \
				echo "  Expected: $$expected"; \
				echo "  Actual:   $$actual"; \
				fail=$$((fail+1)); \
			fi; \
		fi; \
	done; \
	echo "================================================"; \
	echo "Results: $$pass passed, $$fail failed, $$skip skipped."; \
	echo "Passed tests: $$passed_nums"; \
	echo "================================================"

##############
# CLEAN      #
##############
clean:
	@echo "Cleaning up generated files..."
	rm -rf ${COMPILER_NAME}
	rm -rf ${JFlex_CUP_GENERATED_FILES}
	rm -rf ${BIN_DIR}/*.class ${BIN_DIR}/*/*.class
	rm -rf ${OUTPUT_DIR}/*.txt
	@echo "Clean complete!"

.PHONY: compile test-all clean